package com.profitus.crowdfunding.bdd;

import com.profitus.crowdfunding.model.Payout
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert
import java.io.File

import java.time.LocalDate;

class QuarterlyInterestForDeveloperDefs: InterestCalculationsDefs() {
    @Given("Investments quarterly list in sheet {int}")
    fun investmentsMonthlyListInSheet(sheetNo: Int) {
        val file =  File("src/test/resources/Test_Data.xlsx");
        investment = handler.loadInvestitionsFromXlsx(file, sheetNo-1);
    }

    @When("Comes date of the payment from quarterly payments schedule \\((\\d+)-(\\d+)-(\\d+)\\)\$")
    fun comesDateOfThePaymentFromDeveloperMonthlyPaymentSchedule(year: Int, month: Int, day: Int) {
        paymentDate = LocalDate.of(year, month, day)
    }

    @Then("RE developer pays {double} EUR of quarterly interest")
    fun reDeveloperPaysEUROfInterest(interestAmount: Double) {
        var payments: List<Payout>  = investment!!.calculateBorrowerSchedule().roundedPayouts()
        var foundPayment = payments.single{it.date == paymentDate}
        Assert.assertEquals(foundPayment.totalInterest.amount.toDouble(), interestAmount, 0.01)
    }
}
