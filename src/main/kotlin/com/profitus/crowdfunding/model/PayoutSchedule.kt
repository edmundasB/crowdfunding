package com.profitus.crowdfunding.model

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDate

data class PayoutSchedule(private val payouts: List<Payout>, private val precision: Int) {

    fun roundedPayouts(): List<Payout> {
        return payouts.map { Payout(
                investorId = it.investorId,
                date = it.date,
                totalInterest = roundInterest(it.totalInterest, precision),
                additionalInterest = roundInterest(it.additionalInterest, precision),
                interest = roundInterest(it.interest, precision),
                initialAmount = it.initialAmount.setScale(precision, RoundingMode.HALF_UP),
                isEarlyReturn = it.isEarlyReturn,
                earlyTotalReturn = it.earlyTotalReturn,
                isInterest = it.isInterest) }}

    private fun roundInterest(interest: Interest, precision: Int): Interest =
            Interest(interest.amount.setScale(precision, RoundingMode.HALF_UP)
                    ,interest.gpm.setScale(precision, RoundingMode.HALF_UP))

    fun payouts(): List<Payout> = payouts

    fun calculateTotalInterestAmount(): BigDecimal {
        return payouts.map { it.totalInterest.amount }.fold(BigDecimal.ZERO, BigDecimal::add)
    }

    fun calculateTotalInterestAmount(investorId: String): BigDecimal {
        val foundInvestorPayouts = payouts.filter { investorId == it.investorId }.toList()
        return foundInvestorPayouts.map { it.totalInterest.amount }.fold(BigDecimal.ZERO, BigDecimal::add)
    }

    fun payouts(investorId: String): List<Payout> {
        return  payouts.filter { investorId == it.investorId }.toList()
    }

    fun calculatePayoutTotals(date: LocalDate): Payout {
        val payoutsByDateInterest = payouts.filter { it.date.isEqual(date) }

        return Payout(investorId = "",
                date = date,
                totalInterest = calculateTotalInterest(payoutsByDateInterest),
                additionalInterest = calculateAdditionalInterest(payoutsByDateInterest),
                interest = calculateInterest(payoutsByDateInterest),
                initialAmount = calculateTotalInitialAmount(payoutsByDateInterest),
                totalReturn = true)
    }


    private fun calculateTotalInterest(input: List<Payout>): Interest {
        val interest = input.map { it.totalInterest.amount }.fold(BigDecimal.ZERO, BigDecimal::add)
        val gpm = input.map { it.totalInterest.gpm }.fold(BigDecimal.ZERO, BigDecimal::add)
        return roundTotalInterest(interest, gpm)
    }

    private fun roundTotalInterest(interest: BigDecimal, gpm: BigDecimal): Interest {
        val totalRounded = interest.add(gpm).setScale(precision, RoundingMode.HALF_UP)
        return Interest(totalRounded.minus(gpm).setScale(precision, RoundingMode.HALF_UP), gpm.setScale(precision, RoundingMode.HALF_UP))
    }

    private fun calculateAdditionalInterest(input: List<Payout>): Interest {
        val totalInterest = input.map { it.additionalInterest.amount }.fold(BigDecimal.ZERO, BigDecimal::add)

        val gpm = input.map { it.additionalInterest.gpm }.fold(BigDecimal.ZERO, BigDecimal::add)

        return roundTotalInterest(totalInterest, gpm)
    }

    private fun calculateInterest(input: List<Payout>): Interest {
        val totalInterest = input.map { it.interest.amount }.fold(BigDecimal.ZERO, BigDecimal::add)

        val gpm = input.map { it.interest.gpm }.fold(BigDecimal.ZERO, BigDecimal::add)

        return roundTotalInterest(totalInterest, gpm)
    }

    private fun calculateTotalInitialAmount(input: List<Payout>): BigDecimal {
        return input.map { it.initialAmount }.fold(BigDecimal.ZERO, BigDecimal::add)
    }
}