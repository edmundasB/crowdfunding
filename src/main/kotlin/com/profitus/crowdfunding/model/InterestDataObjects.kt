package com.profitus.crowdfunding.model

import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate

data class Payout(val investorId: String,
                  val date: LocalDate,
                  val totalInterest: Interest,
                  val additionalInterest: Interest = Interest(),
                  val interest: Interest = Interest(),
                  val initialAmount: BigDecimal = BigDecimal.ZERO,
                  val isEarlyReturn: Boolean = false,
                  val earlyTotalReturn: Boolean = false,
                  val totalReturn: Boolean = false,
                  val isInterest: Boolean = false)

data class Payin(val date: LocalDate,
                 var daysFromLastPayment: BigDecimal,
                 val amount: BigDecimal = BigDecimal.ZERO,
                 val actualPartialReturnAmount: BigDecimal = BigDecimal.ZERO,
                 val earlyTotalReturn: Boolean = false,
                 val earlyReturn: Boolean = false,
                 val totalReturn: Boolean = false)

data class Interest(val amount: BigDecimal = BigDecimal.ZERO,
                    val gpm: BigDecimal = BigDecimal.ZERO) {
    fun sum(input: Interest) =  Interest(amount = this.amount.add(input.amount), gpm = this.gpm.add(input.gpm) )
}

class TotalPayout(val date: LocalDate,
                  val transferedAmount: BigDecimal,
                  val totalInvestorsInterestWithoutPreReturn: BigDecimal,
                  val totalInvestorsWithPreReturn: BigDecimal,
                  val preReturnAmount: BigDecimal = BigDecimal.ZERO,
                  val initialAmount: BigDecimal){
    fun calculateDifference() :BigDecimal {
        return transferedAmount.minus(totalInvestorsInterestWithoutPreReturn).minus(initialAmount)
    }

    fun calculateDifferenceWithPreReturn() :BigDecimal {
        return transferedAmount.minus(totalInvestorsInterestWithoutPreReturn).minus(initialAmount).minus(preReturnAmount)
    }
}

class Investment(val id: String,
                 val investorId: String,
                 val interestRate: BigDecimal,
                 val gpmPercent: BigDecimal,
                 val additionalInterestRate: BigDecimal,
                 val investedAmount: BigDecimal,
                 val notResident: Boolean = false){

    fun weightedInterest(totalAmount: BigDecimal, precision: MathContext): BigDecimal {
       return investedAmount.divide(totalAmount, precision).multiply(interestRate, precision)
    }

    fun weightedAdditionalInterest(totalAmount: BigDecimal, precision: MathContext): BigDecimal {
        return investedAmount.divide(totalAmount, precision).multiply(additionalInterestRate, precision)
    }
}
