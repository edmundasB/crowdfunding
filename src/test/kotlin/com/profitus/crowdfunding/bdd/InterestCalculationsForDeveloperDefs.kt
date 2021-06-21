package com.profitus.crowdfunding.bdd;

import com.profitus.crowdfunding.model.Payin
import com.profitus.crowdfunding.model.Payout
import io.cucumber.java.en.And
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert
import java.io.File
import java.math.BigDecimal

import java.time.LocalDate;

class InterestCalculationsForDeveloperDefs: InterestCalculationsDefs() {

    @Given("Given Investors monthly list  in sheet      {int}")
    fun investmentsMonthlyListInSheet(sheetNo: Int) {
        val file =  File("src/test/resources/Test_Data.xlsx");
        investment = handler.loadInvestitionsFromXlsx(file, sheetNo-1);
    }

    @And("Partial repayment date {int}-{int}-{int} and partial repayment sum {double} € and days from last return {int}")
    fun partialRepaymentDate(year: Int, month: Int, day: Int, amount: Double, period: Int) {
        investment?.addEarlyRepayment(Payin( LocalDate.of(year, month, day), BigDecimal(period), BigDecimal(amount), earlyReturn = true))
    }

    @And("Interest payment date {int}-{int}-{int}")
    fun interestPaymentDate(year: Int, month: Int, day: Int){
        investment?.scheduleInterestPayout(LocalDate.of(year, month, day))
    }


    @When("Comes date of the payment from payment schedule {int}-{int}-{int}")
    fun comesDateOfThePaymentFromDeveloperMonthlyPaymentSchedule(year: Int, month: Int, day: Int) {
        paymentDate = LocalDate.of(year, month, day)
    }

    @Then("User id-{int} receives {int}-{int}-{int} {double} € interest test")
    fun reDeveloperPaysEUROfInterest(userId: Int, year: Int, month: Int, day: Int, interestAmount: Double){
        var payments: List<Payout>  = investment!!.calculateInvestorsSchedule().payouts(userId.toString())
        var foundPayment = payments.single{it.date == paymentDate}
        Assert.assertEquals( interestAmount, foundPayment.totalInterest.amount.toDouble(), 0.01)
    }
}
