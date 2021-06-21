package com.profitus.crowdfunding.bdd

import com.profitus.crowdfunding.model.Payin
import com.profitus.crowdfunding.model.Payout
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

class ReDeveloperRepaysPartOfLoanDefs: InterestCalculationsDefs() {

    var totalAmountOfEarlyReturn = 0.0
    var earlyReturnPeriod = 0

    @Given("Period for which interest is calculated {int} days, amount of partial repayment {double} €")
    fun  investorsList(period: Int, amountOfRepayment: Double) {
        this.totalAmountOfEarlyReturn = amountOfRepayment
        this.earlyReturnPeriod = period
        var file =  File("src/test/resources/Test_Data.xlsx")
        investment = handler.loadInvestitionsFromXlsx(file, 2)
        investment!!.addEarlyRepayment(Payin(LocalDate.of(2019, Month.JANUARY, 1), BigDecimal(period),
                BigDecimal(amountOfRepayment), earlyReturn = true))
    }

    @When("RE developer informs about repayment")
    fun comesDateOfThePaymentFromDeveloperMonthlyPaymentSchedule() {
    }

    @Then("RE developer pays {double} € of interests with repayment")
    fun reDeveloperPaysEUROfInterest(interestAmount: Double) {
/*        var payments: List<InterestPayment>  = investment!!.calculateEarlyReturnInterestTotal()
        var foundPayment = payments.single{it.isEarlyReturn}*/
       // Assert.assertEquals(interestAmount, investment!!.calculateEarlyReturnInterestTotal(totalAmountOfEarlyReturn, earlyReturnPeriod), 0.01)
    //TODO make check with shedule
    }

    @Then("User {int} receives {double} €")
    fun investorReceives(userId: Int,interestAmount: Double) {
        var payments: List<Payout>  = investment!!.calculateInvestorsSchedule().payouts(userId.toString())
        var foundPayment = payments.single{it.isEarlyReturn}
        Assert.assertEquals(interestAmount, (foundPayment.totalInterest.amount + foundPayment.initialAmount).toDouble(), 0.01)
    }

}