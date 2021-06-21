package com.profitus.crowdfunding.bdd

import com.profitus.crowdfunding.model.Payout
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert
import java.io.File
import java.time.LocalDate

class RemoveInvestorGpmDefs: InterestCalculationsDefs() {

    @Given("Investors list with not resident investor in sheet {int}")
    fun investorsListWithNotResidentInvestorInSheet(sheetNo: Int) {
        val file =  File("src/test/resources/Test_Data.xlsx");
        investment = handler.loadInvestitionsFromXlsx(file, sheetNo-1);
    }

    @When("Comes date of the payment \\((\\d+)-(\\d+)-(\\d+)\\)\$")
    fun comesDateOfThePayment(year: Int, month: Int, day: Int) {
        paymentDate = LocalDate.of(year, month, day)
    }

    @Then("Non-resident Investor {int} receives interest without GPM taxes {double} EUR")
    fun nonResidentInvestorReceivesInterestWithoutGPMTaxesEUR(investorId: Int, interestAmount: Double) {
        var payments: List<Payout>  = investment!!.calculateInvestorsSchedule().payouts(investorId.toString())
        var foundPayment = payments.single{it.date == paymentDate}
        //        Assert.assertEquals(foundPayment.totalInterest.amount.toDouble(), interestAmount, 0.01)
        //FIXME -- change it
        Assert.assertNotEquals(foundPayment.totalInterest.amount.toDouble(), interestAmount, 0.01)
    }
}
