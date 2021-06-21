package com.profitus.crowdfunding.bdd;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert
import org.junit.Ignore
import java.io.File

class CheckDeveloperAndInvestorInterestrdefs: InterestCalculationsDefs() {

    @Given("Investors list for checking in sheet {int}")
    fun investorsListForCheckingInSheet(sheetNo: Int) {
        val file =  File("src/test/resources/Test_Data.xlsx");
        investment = handler.loadInvestitionsFromXlsx(file, sheetNo-1);
    }

    @When("Interest payment is initiated by RE developer")
    fun interestPaymentIsInitiatedByREDeveloper() {
    }

    @And("Sum of all investors interest repayments and sum of RE developer interest payments is equal")
    fun sumOfAllInvestorsInterestRepaymentsAndSumOfREDeveloperInterestPaymentsIsEqual() {
    }

    @Then("Interest payment is successfully completed")
    fun interestPaymentIsSuccessfullyCompleted() {
/*        Assert.assertEquals(investment!!.calculateInvestorsSchedule().calculateTotalInterestAmount(),
                investment!!.calculateBorrowerSchedule().calculateTotalInterestAmount(), 0.01)*/
    }
}
