package com.profitus.crowdfunding.service

import com.profitus.crowdfunding.model.*
import com.profitus.crowdfunding.model.enums.LoanAgreementType
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class Project(val startDate: LocalDate,
              private val endDate: LocalDate,
              val loanAgreementType: LoanAgreementType,
              private val fineOnPreReturn: String,
              var investments: List<Investment>,
              val totalReturnTransferredAmount: BigDecimal) {

    private var calculationsPrecision = 10
    private val ROUNDING_MODE = RoundingMode.HALF_UP
    private var resultRoundingPrecision = 2
    private val MATH_PRECISION = MathContext(calculationsPrecision, ROUNDING_MODE)
    var collectedAmount = investments
            .map { it.investedAmount }
            .fold(BigDecimal.ZERO, BigDecimal::add)

    val weightedInterestRate: BigDecimal = investments
            .map { it.weightedInterest(collectedAmount, MATH_PRECISION) }
            .fold(BigDecimal.ZERO, BigDecimal::add)

    val weightedAdditionalInterestRate: BigDecimal = investments
            .map { it.weightedAdditionalInterest(collectedAmount, MATH_PRECISION) }
            .fold(BigDecimal.ZERO, BigDecimal::add)

    private val DAYS_OF_THE_YEAR = BigDecimal("365")
    private var payin: MutableList<Payin> = mutableListOf()
    private var totalPayout: MutableList<TotalPayout> = mutableListOf()
    private var interestPayoutDates: MutableList<LocalDate> = mutableListOf()

    fun setRoundingScale(scale: Int) {
        resultRoundingPrecision = scale
    }

    fun calculateBorrowerSchedule(): PayoutSchedule {
        calculatePayoutDates()
        return PayoutSchedule(calculateReturnsByMonth(), resultRoundingPrecision)
    }

    fun calculateInvestorsSchedule(): PayoutSchedule {
        calculatePayoutDates()
        val schedule = mutableListOf<Payout>()
        val earlyReturns = mutableListOf<Payout>()
        //early returns
        investments.forEach { earlyReturns.addAll(calculateEarlyReturnsForInvestor(it.id)) }

        if (payin.any { it.earlyTotalReturn }) {
            val earlyTotalReturnDate = payin.single { it.earlyTotalReturn }.date
            interestPayoutDates = interestPayoutDates.filter { it.isBefore(earlyTotalReturnDate) }.toMutableList()
        }

        //interest returns
        investments.forEach { schedule.addAll(calculateInterestForInvestor(it.id, earlyReturns)) }

        schedule.addAll(earlyReturns)
        calculateTotalEarlyReturn(schedule)

        if (!payin.any { it.earlyTotalReturn }) { calculateLastPayout(schedule) }

        return PayoutSchedule(schedule, resultRoundingPrecision)
    }

    private fun calculateLastPayout(schedule: MutableList<Payout>) {
        val days = ChronoUnit.DAYS.between(interestPayoutDates.sorted().last(), endDate).toBigDecimal()
        for(investment in investments) {
            var balance = investment.investedAmount
            schedule.filter { it.investorId == investment.id }.map { balance -= it.initialAmount }

            val interest = calculateInterest(days, investment.interestRate, balance, notResident = investment.notResident, gmpPercent = investment.gpmPercent)
            val additionalInterest = calculateInterest(days, investment.additionalInterestRate, balance, notResident = investment.notResident, gmpPercent = investment.gpmPercent)

            schedule.add(Payout(investorId = investment.id,
                                date = endDate,
                                interest = interest,
                                additionalInterest = additionalInterest,
                                totalInterest = interest.sum(additionalInterest),
                                initialAmount = balance,
                                totalReturn = true))
        }
    }

    private fun calculateTotalEarlyReturn(schedule: MutableList<Payout>) {
        if (payin.any { it.earlyTotalReturn }) {
            val lastPayout = payin.single { it.earlyTotalReturn }
            investments.forEach { schedule.add(calculateTotalEarlyReturn(it.id, schedule, lastPayout)) }
        }
    }

    fun addEarlyRepayment(payin: Payin){
        this.payin.add(payin)
    }

    fun scheduleInterestPayout(interestPayoutdate: LocalDate) {
        if(!interestPayoutDates.contains(interestPayoutdate)) {
            interestPayoutDates.add(interestPayoutdate)
        }
    }

    fun getActualPartialReturnAmount(date: LocalDate): BigDecimal {
        return payin.first { it.date.isEqual(date) }.actualPartialReturnAmount
    }

    fun getActualPartialReturnDays(date: LocalDate): BigDecimal {
        return payin.first { it.date.isEqual(date) }.daysFromLastPayment
    }

    fun getActualTotalPayout(date: LocalDate): TotalPayout {
        return this.totalPayout.first { it.date.isEqual(date)}
    }

    fun getInitialAmount(date: LocalDate): BigDecimal {
        return payin.first { it.date.isEqual(date) }.amount
    }

    fun getLastPayinDate(): BigDecimal {
        var latestDate = payin.map { it.date }.max()!!
        val startDate = getLastInterestPayoutDate(endDate)
        latestDate = when (loanAgreementType) {
            LoanAgreementType.OLD -> latestDate.plusMonths(1)
            LoanAgreementType.NEW -> latestDate.plusMonths(0)
        }
        if (latestDate.isAfter(endDate)) {
            latestDate = endDate
        }
        return ChronoUnit.DAYS
                .between(startDate, latestDate)
                .toBigDecimal()
    }

    fun calculateTotalInterestForInvestors(date: LocalDate): BigDecimal {
        val selectedPayin = payin.first{ it.date.isEqual(date)}
        val partOfDaysInYear = calculateBetweenDates(date).divide(DAYS_OF_THE_YEAR, MATH_PRECISION)
        return selectedPayin.amount.multiply(partOfDaysInYear).multiply(weightedInterestRate)
    }

    private fun calculateTotalAmountWithFineOnPreReturnPartForInvestors(totalInvestorsInterest: BigDecimal): BigDecimal {
//        if (loanAgreementType == LoanAgreementType.NEW && BigDecimal(fineOnPreReturn) > BigDecimal.ZERO) {
//            val percentAmountFromTotalInvestors = calculateFineOnPreReturnPart(totalInvestorsInterest)
//            return totalInvestorsInterest.add(percentAmountFromTotalInvestors)
//        }
        return totalInvestorsInterest
    }

    private fun calculateFineOnPreReturnPart(amount: BigDecimal): BigDecimal {
        return when (loanAgreementType) {
            LoanAgreementType.OLD -> BigDecimal.ZERO
            LoanAgreementType.NEW -> {
                amount.multiply(BigDecimal(fineOnPreReturn).divide(BigDecimal(100)))
            }
        }
    }

    private fun calculateLastInvestorsPayout(amount: BigDecimal): BigDecimal {
        return amount
                .multiply(weightedInterestRate)
                .multiply(getLastPayinDate().divide(DAYS_OF_THE_YEAR, MATH_PRECISION))
    }

    fun calculateLastInitialAmountReturn(): BigDecimal {
        val latestPayin = this.payin.maxBy { it.date }
        val calculatedLastReturnsAmount = this.payin
                .filter {it != latestPayin}
                .map {it.amount}
                .fold(BigDecimal.ZERO, BigDecimal::add)
        return collectedAmount.minus(calculatedLastReturnsAmount)
    }

    fun calculateDifference(date: LocalDate) {
        val actualPartialReturnAmount = getActualPartialReturnAmount(date)
        val totalInvestorsAmountWithoutPreReturnFine = calculateTotalInterestForInvestors(date)
        val totalInvestorsWithPreReturn = calculateTotalAmountWithFineOnPreReturnPartForInvestors(totalInvestorsAmountWithoutPreReturnFine)
        val initialAmount = getInitialAmount(date)
        val preReturnAmount = calculateFineOnPreReturnPart(initialAmount)


        this.totalPayout.add(
                TotalPayout(date = date,
                        transferedAmount = actualPartialReturnAmount,
                        totalInvestorsInterestWithoutPreReturn = totalInvestorsAmountWithoutPreReturnFine,
                        totalInvestorsWithPreReturn = totalInvestorsWithPreReturn,
                        preReturnAmount = preReturnAmount,
                        initialAmount = initialAmount
                )
        )
    }

    fun calculateBetweenDates(endDate: LocalDate): BigDecimal {
        val startDate = getLastInterestPayoutDate(endDate)
        val calculatedOldDate = when (loanAgreementType) {
            LoanAgreementType.OLD -> endDate.plusMonths(1)
            LoanAgreementType.NEW -> endDate.plusMonths(0)
        }
        return ChronoUnit.DAYS
                .between(startDate, calculatedOldDate)
                .toBigDecimal()
    }

    fun calculateLastTotalReturnDifference(lastDate: LocalDate) {
        val lastActualPartialReturnAmount = calculateLastInitialAmountReturn()
        val lastTotalInvestorsAmountWithoutPreReturnFine = calculateLastInvestorsPayout(lastActualPartialReturnAmount)
        val lastTotalInvestorsAmountWithPreReturn = calculateTotalAmountWithFineOnPreReturnPartForInvestors(lastActualPartialReturnAmount)
        val lastPreReturnAmount = calculateFineOnPreReturnPart(lastActualPartialReturnAmount)

        this.totalPayout.add(
                TotalPayout(date = lastDate,
                        transferedAmount = totalReturnTransferredAmount,
                        totalInvestorsInterestWithoutPreReturn = lastTotalInvestorsAmountWithoutPreReturnFine,
                        totalInvestorsWithPreReturn = lastTotalInvestorsAmountWithPreReturn,
                        preReturnAmount = lastPreReturnAmount,
                        initialAmount = lastActualPartialReturnAmount
                )
        )
    }

    fun roundValueByDefaultScale(decimal: BigDecimal): BigDecimal {
        return decimal.setScale(resultRoundingPrecision, RoundingMode.HALF_UP)
    }

    fun getEarlyReturnFeePercent(): String {
        return when (loanAgreementType) {
            LoanAgreementType.OLD -> "0% (old)"
            LoanAgreementType.NEW -> "$fineOnPreReturn %"
        }
    }

    private fun calculatePayoutDates() {
        var earlyReturnDate = endDate
        if(payin.any { it.earlyTotalReturn }) { earlyReturnDate = payin.single { it.earlyTotalReturn }.date }

        var result = this.payin.filter{ it.earlyTotalReturn }.map{ calculatePartialReturnDays(it) }.toMutableList()

        result.addAll( this.payin.filter{ it.earlyReturn }
                .map{ calculatePartialReturnDays(it) }
                .toMutableList() )

        this.payin.clear()
        var lastPaymentDate = this.startDate

        for(date in interestPayoutDates.sorted()) {
            if(date.isBefore(earlyReturnDate.plusDays(1L)) && date.isBefore(endDate.plusDays(1L))) {
                val days = ChronoUnit.DAYS.between(lastPaymentDate, date).toBigDecimal()
                this.payin.add(Payin(date, days))
                lastPaymentDate = date
            }
        }
        result = validatePayinDates(result)
        this.payin.addAll(result)
    }

    private fun validatePayinDates(result: MutableList<Payin>): MutableList<Payin> {
        val scheduledPayouts = this.payin.distinctBy { it.date }.map { it.date }.toSet()
        return result.filter { it.date !in scheduledPayouts }.toMutableList()
    }

    private fun calculatePartialReturnDays(partialReturnDate: Payin): Payin {
        val monthsToAddForActualDate = when (this.loanAgreementType) {
            LoanAgreementType.OLD -> 1L
            LoanAgreementType.NEW -> 0L
        }
        val monthsDiff = ChronoUnit.MONTHS.between(startDate, partialReturnDate.date.plusMonths(monthsToAddForActualDate))
        var calculateFrom = getLastInterestPayoutDate(partialReturnDate.date)

        if(monthsDiff < 3) {
            var periodEndDate = this.startDate.plusMonths(3)

            if(periodEndDate.isAfter(this.endDate)){ periodEndDate = this.endDate }

            val daysToAdd = ChronoUnit.DAYS
                    .between(calculateFrom,periodEndDate)
                    .toBigDecimal()

            partialReturnDate.daysFromLastPayment = daysToAdd
            return partialReturnDate
        } else {
            var periodEndDate = partialReturnDate.date.plusMonths(monthsToAddForActualDate)

            if(periodEndDate.isAfter(this.endDate)){ periodEndDate = this.endDate }

            val daysToAdd = ChronoUnit.DAYS
                    .between(calculateFrom, periodEndDate)
                    .toBigDecimal()

            partialReturnDate.daysFromLastPayment = daysToAdd
            return partialReturnDate
        }
    }

    private fun getLastInterestPayoutDate(returnDate: LocalDate ): LocalDate {
        var result = this.startDate
        for(payoutDate in this.interestPayoutDates.sorted()){
            if(payoutDate.isBefore(returnDate)){
                result = payoutDate
            }
        }
        return result
    }

    private fun calculateReturnsByMonth(): List<Payout> {
        val payouts = mutableListOf<Payout>()
        payouts.addAll(calculateEarlyReturns())

        for(pay in payin){
            val interest= calculateInterest(pay.daysFromLastPayment, weightedInterestRate, collectedAmount)
            val additionalInterest= calculateInterest(pay.daysFromLastPayment, weightedAdditionalInterestRate, collectedAmount)
            payouts.add(Payout(investorId = "",
                    date = pay.date,
                    interest = interest,
                    additionalInterest = additionalInterest,
                    totalInterest = interest.sum(additionalInterest)))
        }
        return payouts
    }

    private fun calculateEarlyReturns(): List<Payout> {
        val earlyReturnsWithoutTotal = payin.filter { !it.earlyTotalReturn }.filter { it.earlyReturn }
        val payouts = mutableListOf<Payout>()

        for(pay in earlyReturnsWithoutTotal){
            val interest= calculateInterest(pay.daysFromLastPayment, weightedInterestRate, calculateEarlyReturnInterestForInvestor(collectedAmount, pay.amount))
            val additionalInterest=  calculateInterest(pay.daysFromLastPayment, weightedAdditionalInterestRate, calculateEarlyReturnInterestForInvestor(collectedAmount, pay.amount))
            val initialAmount = calculateEarlyReturnAmountForInvestor(pay.amount, collectedAmount)
            payouts.add(Payout(investorId = "",
                    date = pay.date,
                    interest = interest,
                    additionalInterest = additionalInterest,
                    totalInterest = interest.sum(additionalInterest),
                    initialAmount = initialAmount,
                    isEarlyReturn = true))
        }

        return payouts
    }

    private fun calculateInterest(periodDays: BigDecimal, interestRate: BigDecimal, amount: BigDecimal, notResident: Boolean, gmpPercent: BigDecimal): Interest {
        return if(notResident) {
            val totalAmount = periodDays
                    .divide(DAYS_OF_THE_YEAR, MATH_PRECISION)
                    .multiply(interestRate, MATH_PRECISION)
                    .multiply(amount, MATH_PRECISION)

            val amountWithoutGpm = totalAmount.multiply(BigDecimal(1).minus(gmpPercent), MATH_PRECISION)
            Interest(amountWithoutGpm, totalAmount.minus(amountWithoutGpm))
        } else {
            calculateInterest(periodDays, interestRate, amount)
        }
    }

    private fun calculateInterest(periodDays: BigDecimal, interestRate: BigDecimal, amount: BigDecimal): Interest {
        return Interest(periodDays
                .divide(DAYS_OF_THE_YEAR, MATH_PRECISION)
                .multiply(interestRate, MATH_PRECISION)
                .multiply(amount, MATH_PRECISION))
    }

    private fun calculateInterestForInvestor(investorId: String, earlyReturns: List<Payout>): List<Payout> {
        val investment = investments.single { it.id == investorId }
        val earlyReturnsFiltered = earlyReturns.filter { it.investorId == investorId }

        return payin.filter { !it.earlyReturn }.filter { !it.earlyTotalReturn }.map { calculatePayment(it, investment, earlyReturnsFiltered)}.toMutableList()

    }

    private fun calculateEarlyReturnsForInvestor(investorId: String): List<Payout> {
        val investment = investments.single { it.id == investorId }
        return payin.filter { it.earlyReturn }.map { calculateEarlyPayment(it, investment) }
    }

    private fun calculateTotalEarlyReturn(investorId: String, payouts: List<Payout>, payin: Payin): Payout {
        val investment = investments.single { it.id == investorId }
        val totalInvestorPayouts = payouts.filter { it.investorId == investorId }

        var balance = investment.investedAmount
        totalInvestorPayouts.filter { payin.date.isAfter(it.date) }.forEach { balance = balance.minus( it.initialAmount.setScale(2, RoundingMode.HALF_UP))  }

        val interest= calculateInterest(payin.daysFromLastPayment, investment.interestRate, balance, investment.notResident, investment.gpmPercent)
        val additionalInterest= calculateInterest(payin.daysFromLastPayment, investment.additionalInterestRate, balance, investment.notResident, investment.gpmPercent)

        return Payout(investorId = investment.id,
                      date = payin.date,
                      interest = interest,
                      totalInterest = interest.sum(additionalInterest),
                      initialAmount = balance,
                      additionalInterest = additionalInterest,
                      isEarlyReturn = payin.earlyReturn,
                      earlyTotalReturn =  payin.earlyTotalReturn)
    }

    private fun calculatePayment(payin: Payin, investment: Investment, earlyPayments: List<Payout>): Payout {
        var investedAmount = investment.investedAmount
        for(earlyPayment in earlyPayments){
            if(payin.date.isAfter(earlyPayment.date)){
                investedAmount = investedAmount.minus(earlyPayment.initialAmount)
            }
        }

        val interest= calculateInterest(payin.daysFromLastPayment, investment.interestRate, investedAmount, investment.notResident, investment.gpmPercent)
        val additionalInterest= calculateInterest(payin.daysFromLastPayment,
                                                         investment.additionalInterestRate,
                                                         investedAmount,
                                                         investment.notResident,
                                                         investment.gpmPercent)

        return Payout(investorId = investment.id,
                      date = payin.date,
                      totalInterest = interest.sum(additionalInterest),
                      additionalInterest = additionalInterest,
                      interest = interest,
                      isInterest = true)
    }

    private fun calculateEarlyPayment(payinDate: Payin, investment: Investment): Payout {
        val initialAmountPayout = calculateEarlyReturnAmountForInvestor(payinDate.amount, investment.investedAmount)
        val interestPayout = calculateInterest(payinDate.daysFromLastPayment, investment.interestRate, initialAmountPayout, investment.notResident, investment.gpmPercent)
        val additionalInterest = calculateInterest(payinDate.daysFromLastPayment, investment.additionalInterestRate, initialAmountPayout, investment.notResident, investment.gpmPercent)

        return Payout(investorId = investment.id,
                      date = payinDate.date,
                      totalInterest = interestPayout.sum(additionalInterest),
                      additionalInterest = additionalInterest,
                      interest = interestPayout,
                      initialAmount = initialAmountPayout,
                      isEarlyReturn = true)
    }

    private fun calculateEarlyReturnInterestForInvestor(investedAmount: BigDecimal, earlyPart: BigDecimal): BigDecimal {
        return investedAmount.multiply(earlyPart, MATH_PRECISION).divide(collectedAmount, MATH_PRECISION)
    }

    private fun calculateEarlyReturnAmountForInvestor(earlyPart: BigDecimal, investedAmount: BigDecimal): BigDecimal {
        return earlyPart.multiply(investedAmount, MATH_PRECISION).divide(collectedAmount, MATH_PRECISION)
    }


}
