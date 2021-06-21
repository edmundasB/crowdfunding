package com.profitus.crowdfunding.bdd;

import com.profitus.crowdfunding.model.Payout
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert
import java.io.File

import java.time.LocalDate;

class MonthlyPaymentsDefs: InterestCalculationsDefs() {

    @Given("Investors monthly list in sheet (\\d+)")
    fun  investorsList(sheetNo: Int) {
        var file =  File("src/test/resources/Test_Data.xlsx");
        investment = handler.loadInvestitionsFromXlsx(file, sheetNo-1);
    }

    @When("Comes date of the payment from monthly payment schedule \\((\\d+)-(\\d+)-(\\d+)\\)\$")
    fun comesDateOfThePaymentFromMonthlyPaymentSchedule(year: Int, month: Int, day: Int) {
        paymentDate = LocalDate.of(year, month, day)
    }

    @Then("User {int} receives {double} EUR of monthly interest")
    fun  userIdReceivesEUROfInterest( userId: Int, amount: Double) {
        var payments: List<Payout>  = investment!!.calculateInvestorsSchedule().payouts(userId.toString())
        var foundPayment = payments.single{it.date == paymentDate}
        Assert.assertEquals(foundPayment.totalInterest.amount.toDouble(), amount, 0.01)
    }
}
