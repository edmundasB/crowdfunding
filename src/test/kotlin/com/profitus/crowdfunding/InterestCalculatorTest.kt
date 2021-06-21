package com.profitus.crowdfunding

import com.profitus.crowdfunding.model.Payin
import com.profitus.crowdfunding.model.Payout
import com.profitus.crowdfunding.model.Investment
import com.profitus.crowdfunding.model.enums.LoanAgreementType
import com.profitus.crowdfunding.service.*
import org.junit.Test
import java.time.LocalDate
import java.time.Month
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import java.io.File
import java.math.BigDecimal

class InterestCalculatorTest {
    private lateinit var handler: PaymentExcelHandler

    @Before
    fun setUp() {
        handler = PaymentExcelHandler()
    }

    @Test
    fun `interest schedule calculated for borrower`(){
        val project = mockedInvestitionsList()
        val schedule = project.calculateBorrowerSchedule()

        Assert.assertEquals(3, schedule.payouts().size)
        Assert.assertEquals(7.47945, schedule.calculateTotalInterestAmount().toDouble(), 0.001)
    }

    @Test
    fun `total interest for investor calculated`() {
        val project = mockedInvestitionsList()
        val schedule = project.calculateInvestorsSchedule()

        Assert.assertEquals(12, schedule.roundedPayouts().size)

        Assert.assertEquals(3.34, schedule.calculateTotalInterestAmount("1").toDouble(), 0.01)
    }

    @Test
    fun `interest and payments dates list by month for investor`() {
        val payments: List<Payout> = excelInvestitionsList().calculateInvestorsSchedule().roundedPayouts().filter{ it.investorId == "9" }
        Assert.assertEquals(3, payments.size)
        Assert.assertEquals( 22.90, payments[0].totalInterest.amount.toDouble(), 0.0001)
    }

    @Test
    @Ignore
    fun `early return interest calculations`() {
        val paymentsWithEarlyReturn = mockedInvestitionsList()
        paymentsWithEarlyReturn.addEarlyRepayment(Payin( LocalDate.of(2020, Month.JANUARY, 15),
                BigDecimal(15.0), BigDecimal(50.0), earlyReturn = true))

        var payments = paymentsWithEarlyReturn.calculateInvestorsSchedule().roundedPayouts().filter { it.investorId == "1" }
        Assert.assertEquals(5, payments.size)

        var earlyPayment= payments.single { it.isEarlyReturn }
        Assert.assertEquals(0.07, earlyPayment.totalInterest.amount.toDouble(), 0.0001)

        var paymentAfterReturn = payments.single{it.date == LocalDate.of(2020, Month.FEBRUARY, 1)}

        Assert.assertEquals(0.71, paymentAfterReturn.totalInterest.amount.toDouble(), 0.01)

        var paymentAfterReturn2 = payments.single{it.date == LocalDate.of(2020, Month.MARCH, 1)}

        Assert.assertEquals(0.66, paymentAfterReturn2.totalInterest.amount.toDouble(), 0.01)
    }


    private fun mockedInvestitionsList(): Project {
        val startDate = LocalDate.of(2019, Month.DECEMBER, 1)
        val endDate = LocalDate.of(2020, Month.APRIL, 1)

        val interestRate =BigDecimal( "0.1")
        val investedAmount = BigDecimal("100")
        val investments = mutableListOf<Investment>()
        investments.add(Investment(id = "1", investorId = "", interestRate = interestRate, additionalInterestRate = BigDecimal.ZERO, investedAmount = investedAmount, gpmPercent = BigDecimal.ZERO))
        investments.add(Investment(id = "2", investorId = "", interestRate = interestRate, additionalInterestRate = BigDecimal.ZERO, investedAmount = investedAmount, gpmPercent = BigDecimal.ZERO))
        investments.add(Investment(id = "3", investorId = "", interestRate = interestRate, additionalInterestRate = BigDecimal.ZERO, investedAmount = investedAmount, gpmPercent = BigDecimal.ZERO))

        val payoutsDates = mutableListOf<LocalDate>()
        payoutsDates.add(LocalDate.of(2020, Month.JANUARY, 1))
        payoutsDates.add(LocalDate.of(2020, Month.FEBRUARY, 1))
        payoutsDates.add(LocalDate.of(2020, Month.MARCH, 1))

        val project = Project(startDate, endDate, LoanAgreementType.OLD, "1", investments, BigDecimal.ZERO)

        payoutsDates.map{ project.scheduleInterestPayout(it) }

        return project
    }

    private fun excelInvestitionsList(): Project {
        val file = File("src/test/resources/Test_Data.xlsx")
        val investment: Project = handler.loadInvestitionsFromXlsx(file, 1)
        return investment
    }

}
