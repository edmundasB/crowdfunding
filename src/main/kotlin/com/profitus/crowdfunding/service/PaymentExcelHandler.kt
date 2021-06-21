package com.profitus.crowdfunding.service

import com.profitus.crowdfunding.constants.LoanHeader
import com.profitus.crowdfunding.constants.ReturnType
import com.profitus.crowdfunding.model.Investment
import com.profitus.crowdfunding.model.Payin
import com.profitus.crowdfunding.model.Payout
import com.profitus.crowdfunding.model.enums.LoanAgreementType
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.Reader
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

class PaymentExcelHandler {

    private val formatter = DateTimeFormatterBuilder()
    .appendPattern("[d/M/yyyy]")
    .appendPattern("[yyyy-MM-dd]")
    .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
    .toFormatter()

    private fun createCsvParser(reader: Reader) : CSVParser {
        return CSVParser(reader,
                CSVFormat.DEFAULT.withHeader(
                        LoanHeader.SCHEDULED_RETURN,
                        LoanHeader.START_DATE,
                        LoanHeader.END_DATE,
                        LoanHeader.AMOUNT,
                        LoanHeader.PARTIAL_RETURN_DATE,
                        LoanHeader.PARTIAL_RETURN_PERIOD_DAYS,
                        LoanHeader.PARTIAL_RETURN_AMOUNT,
                        LoanHeader.PARTIAL_RETURN_TRANSFERRED_AMOUNT,
                        LoanHeader.TOTAL_EARLY_RETURN_DATE,
                        LoanHeader.LOAN_AGREEMENT,
                        LoanHeader.FINE_ON_PRE_RETURN)
                        .withIgnoreHeaderCase()
                        .withTrim()
                        .withDelimiter(';')
                        .withAllowMissingColumnNames()
                        .withFirstRecordAsHeader())
    }

    fun loadCreditInfoFromCsv(file: File, investorsSource: File): Project {
        val loanReader = file.bufferedReader()
        val investorsSourceReader = investorsSource.bufferedReader()
        try {
            val csvParser = createCsvParser(loanReader)
            checkMandatoryLoanFields(csvParser)
            checkPartialReturnLoanFields(csvParser)
            checkTotalPartialReturnLoanFields(csvParser)
            return makeProjectFromCsv(csvParser, investorsSourceReader)
        } catch (e: Exception){
            loanReader.close()
            investorsSourceReader.close()
            throw Exception(e.message)
        }
    }

    private fun makeProjectFromCsv(csvParser: CSVParser, investorsSource: BufferedReader): Project {
        val payoutDates = mutableListOf<LocalDate>()
        val earlyReturnDates = mutableListOf<Payin>()

        var startDate = ""
        var endDate = ""
        var pReturnAmount = ""
        var pReturnDate = ""
        var pActualReturnAmount = ""
        var agreementTypeString = ""
        var fineOnPreReturn = ""
        var totalReturnTransferredAmuount = BigDecimal.ZERO
        val loanAgreementType : LoanAgreementType



        for (csvRecord: CSVRecord in csvParser) {
            startDate = if (startDate.isNotEmpty()) startDate else csvRecord.get(LoanHeader.START_DATE)
            endDate = if (endDate.isNotEmpty()) endDate else csvRecord.get(LoanHeader.END_DATE)

            if (csvRecord.isMapped(LoanHeader.PARTIAL_RETURN_DATE)) {

                pReturnAmount = csvRecord.get(LoanHeader.PARTIAL_RETURN_AMOUNT).replace(",", ".")
                pActualReturnAmount = csvRecord.get(LoanHeader.PARTIAL_RETURN_TRANSFERRED_AMOUNT).replace(",", ".")
                pReturnDate = csvRecord.get(LoanHeader.PARTIAL_RETURN_DATE)

                if(!pReturnDate.isNullOrEmpty() && !pReturnAmount.isNullOrEmpty()) {
                    earlyReturnDates.add(Payin(
                            date = LocalDate.parse(pReturnDate, formatter),
                            daysFromLastPayment = BigDecimal.ZERO,
                            amount = BigDecimal(pReturnAmount),
                            actualPartialReturnAmount = BigDecimal(pActualReturnAmount),
                            earlyReturn = true))
                }
            }

            if (csvRecord.isMapped(LoanHeader.TOTAL_EARLY_RETURN_DATE)) {

                val tPartialReturnDate = csvRecord.get(LoanHeader.TOTAL_EARLY_RETURN_DATE)

                if (!tPartialReturnDate.isNullOrEmpty()) {
                    earlyReturnDates.add(Payin(date = LocalDate.parse(tPartialReturnDate, formatter),
                            daysFromLastPayment = BigDecimal.ZERO,
                            earlyTotalReturn = true))
                }

            }

            if (!csvRecord.get(LoanHeader.SCHEDULED_RETURN).isNullOrEmpty()) {
                val paymentDate = csvRecord.get(LoanHeader.SCHEDULED_RETURN)
                payoutDates.add(LocalDate.parse(paymentDate, formatter))
            }

            if (csvRecord.isMapped(LoanHeader.LOAN_AGREEMENT) && agreementTypeString == "") {
                agreementTypeString = csvRecord.get(LoanHeader.LOAN_AGREEMENT).toLowerCase()
            }

            if (csvRecord.isMapped(LoanHeader.FINE_ON_PRE_RETURN) && fineOnPreReturn == "") {
                fineOnPreReturn = csvRecord.get(LoanHeader.FINE_ON_PRE_RETURN).replace("%", "").replace(",", ".");
            }

            if (csvRecord.isMapped(LoanHeader.TOTAL_RETURN_TRANSFERRED_AMOUNT)
                    && totalReturnTransferredAmuount == BigDecimal.ZERO
                    && csvRecord.get(LoanHeader.TOTAL_RETURN_TRANSFERRED_AMOUNT) != "") {
                totalReturnTransferredAmuount = BigDecimal(csvRecord.get(LoanHeader.TOTAL_RETURN_TRANSFERRED_AMOUNT).replace(",", "."))
            }

        }

        loanAgreementType = when {
            LoanAgreementType.NEW.type == agreementTypeString -> LoanAgreementType.NEW
            LoanAgreementType.OLD.type == agreementTypeString -> LoanAgreementType.OLD
            else -> throw java.lang.IllegalArgumentException("Empty value in ${LoanHeader.LOAN_AGREEMENT}. Valid values ${LoanAgreementType.values()}")
        }

        val startDateFormatted = LocalDate.parse(startDate, formatter)
        val endDateFormatted = LocalDate.parse(endDate, formatter)

        val project = Project(startDateFormatted,
                endDateFormatted,
                loanAgreementType,
                fineOnPreReturn,
                loadInvestitionsFromCsv(investorsSource),
                totalReturnTransferredAmuount)
        payoutDates.map { project.scheduleInterestPayout(it) }
        earlyReturnDates.map { project.addEarlyRepayment(it) }

        return project
    }

    private fun checkMandatoryLoanFields(csvParser: CSVParser) {
        val requiredColumns = mutableListOf(
                LoanHeader.SCHEDULED_RETURN,
                LoanHeader.START_DATE,
                LoanHeader.END_DATE,
                LoanHeader.AMOUNT
        )
        val missingMandatoryColumns = checkMandatoryColumns(csvParser, requiredColumns)
        if (missingMandatoryColumns.isNotEmpty()) {
            throw IllegalArgumentException("Missing columns: $missingMandatoryColumns")
        }
    }

    private fun checkPartialReturnLoanFields(csvParser: CSVParser) {
        val requiredColumns = mutableListOf(
                LoanHeader.PARTIAL_RETURN_DATE,
                LoanHeader.PARTIAL_RETURN_AMOUNT,
                LoanHeader.PARTIAL_RETURN_TRANSFERRED_AMOUNT,
                LoanHeader.LOAN_AGREEMENT,
                LoanHeader.FINE_ON_PRE_RETURN
        )
        val missingMandatoryColumns = checkMandatoryColumns(csvParser, requiredColumns)
        if (missingMandatoryColumns.isNotEmpty() && missingMandatoryColumns.size < requiredColumns.size) {
            throw IllegalArgumentException("Missing columns: $missingMandatoryColumns")
        }
    }

    private fun checkTotalPartialReturnLoanFields(csvParser: CSVParser) {
        val requiredColumns = mutableListOf(
                LoanHeader.TOTAL_EARLY_RETURN_DATE
        )
        val missingMandatoryColumns = checkMandatoryColumns(csvParser, requiredColumns)
        if (missingMandatoryColumns.isNotEmpty()) {
            throw IllegalArgumentException("Missing columns: $missingMandatoryColumns")
        }
    }

    private fun checkMandatoryColumns(csvParser: CSVParser, mandatoryFields: MutableList<String>) : List<String> {
        return mandatoryFields.filter { mandatoryField -> !csvParser.headerMap.containsKey(mandatoryField) }
    }

    fun loadInvestitionsFromCsv(reader: BufferedReader): List<Investment> {
        val csvParser = CSVParser(reader,
                CSVFormat.DEFAULT.withHeader("Eil. nr.",
                        "Vartotojas",
                        "Asmens kodas / įmonės kodas",
                        "Šalies kodas",
                        "GPM",
                        "Investuotojo Vardas Pavardė / Pilnas įmonės pavadinimas",
                        "Palūkanų norma",
                        "Bendra palūkanų norma",
                        "Profitus palūkanos",
                        "Vystytojo palūkanos",
                        "Investavimo suma",
                        "Projektas",
                        "Statusas",
                        "Investavimo data",
                        "Tipas")
                        .withIgnoreHeaderCase()
                        .withTrim()
                        .withDelimiter(';')
                        .withAllowMissingColumnNames()
                        .withFirstRecordAsHeader()
                        .withAllowMissingColumnNames())

        val payoutDates = mutableListOf<Investment>()

        val useOldInterestField = csvParser.headerNames.contains("Palūkanų norma")

        for (csvRecord: CSVRecord in csvParser) {
            val investmentId = csvRecord.get(0)
            val investorId = csvRecord.get("Vartotojas")
            val amount = csvRecord.get("Investavimo suma").replace(",", ".")
            val notResident = csvRecord.get("Šalies kodas").toLowerCase() != "lt"
            var interestRate = "0"
            var additionalInterestRate = "0"
            var gmpPercent = csvRecord.get("GPM").replace("%", "")
            if(useOldInterestField){
                 interestRate = csvRecord.get("Palūkanų norma").replace("%", "").replace(",", ".")
            } else {
                 interestRate = csvRecord.get("Vystytojo palūkanos").replace("%", "").replace(",", ".")
                 additionalInterestRate = csvRecord.get("Profitus palūkanos").replace("%", "").replace(",", ".")
            }

            if(investmentId.length != 0) {
                payoutDates.add(Investment(
                                id = investmentId,
                                investorId = investorId,
                                gpmPercent = BigDecimal(gmpPercent).divide(BigDecimal(100), 4, RoundingMode.HALF_UP),
                                interestRate = BigDecimal(interestRate).divide(BigDecimal(100), 4, RoundingMode.HALF_UP),
                                additionalInterestRate = BigDecimal(additionalInterestRate).divide(BigDecimal(100), 4, RoundingMode.HALF_UP),
                                investedAmount = BigDecimal(amount),
                                notResident = notResident))
            }
        }

        return payoutDates
    }

    fun writeInvestmentsToFileForImport(investmentsData: Project): String {
        val columns = arrayOf(
                "Eil. nr.",
                "user_id",
                "investment",
                "interest_proc",
                "interest",
                "additional interest",
                "total interest",
                "refund",
                "fine",
                "GPM",
                "additional GPM",
                "total GPM",
                "GPM percent")

        val workbook = XSSFWorkbook()
        val headerFont = workbook.createFont()
        headerFont.bold = true
        headerFont.color = IndexedColors.BLACK.getIndex()

        val headerCellStyle = workbook.createCellStyle()
        headerCellStyle.setFont(headerFont)

        val payoutsSchedule = investmentsData.calculateInvestorsSchedule()
        val payoutsScheduleDates = payoutsSchedule.roundedPayouts()

        for(payout in payoutsScheduleDates.sortedBy { it.date }) {
            val sheetName = resolveSheetName(payout)
            var investorsSheet = workbook.getSheet(sheetName)

            if(investorsSheet == null){
                investorsSheet = workbook.createSheet(sheetName)
                val headerRow = investorsSheet.createRow(0)

                for ((index, column) in columns.withIndex()) {
                    val cell= headerRow.createCell(index)
                    cell.setCellValue(column)
                    cell.setCellStyle(headerCellStyle)
                }
            }

                investorsSheet.lastRowNum
                val row = investorsSheet.createRow(investorsSheet.lastRowNum + 1)

                val investment = investmentsData.investments.single { it.id == payout.investorId }

                row.createCell(0).setCellValue(investment.id)
                row.createCell(1).setCellValue(investment.investorId)
                row.createCell(2).setCellValue(investment.investedAmount.toPlainString())
                row.createCell(3).setCellValue(investment.interestRate.toPlainString())

                row.createCell(4).setCellValue(payout.interest.amount.toDouble())
                row.createCell(5).setCellValue(payout.additionalInterest.amount.toDouble())
                row.createCell(6).setCellValue(payout.totalInterest.amount.toDouble())

                row.createCell(7).setCellValue(payout.initialAmount.toDouble())
                row.createCell(8).setCellValue("0")
                row.createCell(9).setCellValue(payout.interest.gpm.toDouble())
                row.createCell(10).setCellValue(payout.additionalInterest.gpm.toDouble())
                row.createCell(11).setCellValue(payout.totalInterest.gpm.toDouble())
                row.createCell(12).setCellValue(investment.gpmPercent.multiply(BigDecimal(100)).setScale(2).toPlainString() + "%")
        }

        makeDeveloperSheet(workbook, investmentsData)

        val fileName = "Interest_calculations"+System.currentTimeMillis()+".xlsx"

        val fileOut = FileOutputStream("/tmp/$fileName")
        workbook.write(fileOut)

        fileOut.close()

        workbook.close()
        return fileName
    }

    private fun resolveSheetName(payout: Payout): String {
        val payoutDate = payout.date.toString()
        return when {
            payout.isEarlyReturn -> "${ReturnType.PARTIAL_RETURN} $payoutDate"
            payout.totalReturn -> "${ReturnType.FINAL_RETURN} $payoutDate"
            payout.earlyTotalReturn -> "${ReturnType.EARLY_TOTAL_RETURN} $payoutDate"
            else -> "${ReturnType.INTEREST} $payoutDate"
        }
    }

    private fun makeDeveloperSheet(workbook:XSSFWorkbook, investmentsData: Project) {
        val investorsSheet = workbook.createSheet("Developer data")

        val firstColumnLabels = arrayOf("Interest calculation start date",
                                                    "Total amount",
                                                    "Weighted interest rate",
                                                    "Weighted additional interest rate",
                                                    "Loan agreement type")

        val firstRowLabels = arrayOf("Interest",
                                                 "Additional interest",
                                                 "Total interest",
                                                 "GPM",
                                                 "Additional GPM",
                                                 "Total GPM",
                                                 "Initial amount",
                                                 "Transferred",
                                                 "Difference",
                                                 "Partial return interest days",
                                                 "Early total return days",
                                                 "Early return percent",
                                                 "Early return fee",
                                                 "Total Amount (Interest + GPM)")

        val headerFont = workbook.createFont()
        headerFont.bold = true
        headerFont.color = IndexedColors.BLACK.getIndex()

        val headerCellStyle = workbook.createCellStyle()
        headerCellStyle.setFont(headerFont)

        val schedule = investmentsData.calculateInvestorsSchedule()

        val payoutsDistinctDates = schedule.payouts().distinctBy { it.date to it.isInterest }
        val lastPayoutDate = payoutsDistinctDates.maxBy { it.date }?.date

        var cellIndex = 2
        val headerRow = investorsSheet.createRow(0)
        for (column in firstRowLabels) {
            val cell = headerRow.createCell(cellIndex)
            cell.setCellStyle(headerCellStyle)
            cell.setCellValue(column)

            cellIndex++
        }

        var index = 1
        for (column in firstColumnLabels) {
            val labelRow = investorsSheet.createRow(index)

            val labelCell = labelRow.createCell(0)
            labelCell.setCellStyle(headerCellStyle)
            labelCell.setCellValue(column)
            index++
        }

        investorsSheet.getRow(1).createCell(1).setCellValue(investmentsData.startDate.toString())
        investorsSheet.getRow(2).createCell(1).setCellValue(investmentsData.collectedAmount.toPlainString())
        investorsSheet.getRow(3).createCell(1).setCellValue(investmentsData.weightedInterestRate.multiply(BigDecimal(100)).toPlainString())
        investorsSheet.getRow(4).createCell(1).setCellValue(investmentsData.weightedAdditionalInterestRate.multiply(BigDecimal(100)).toPlainString())
        investorsSheet.getRow(5).createCell(1).setCellValue(investmentsData.loanAgreementType.type)

        for(payout in payoutsDistinctDates.sortedBy { it.date }) {
            val labelRow = investorsSheet.createRow(index)

            val labelcell = labelRow.createCell(0)
            labelcell.setCellStyle(headerCellStyle)

            val sheetName = resolveSheetName(payout)

            labelcell.setCellValue(sheetName)

            val payout = schedule.calculatePayoutTotals(payout.date)
            payout.totalInterest.amount
            labelRow.createCell(2).setCellValue(payout.interest.amount.toDouble())
            labelRow.createCell(3).setCellValue(payout.additionalInterest.amount.toDouble())
            labelRow.createCell(4).setCellValue(payout.totalInterest.amount.toDouble())

            labelRow.createCell(5).setCellValue(payout.interest.gpm.toDouble())
            labelRow.createCell(6).setCellValue(payout.additionalInterest.gpm.toDouble())
            labelRow.createCell(7).setCellValue(payout.totalInterest.gpm.toDouble())

            labelRow.createCell(8).setCellValue(payout.initialAmount.toDouble())
            labelRow.createCell(15).setCellValue(payout.interest.amount.add(payout.interest.gpm).toDouble())

            if(sheetName.startsWith("Partial return")) {
                val transfferedAmount = investmentsData.getActualPartialReturnAmount(payout.date)
                investmentsData.calculateDifference(payout.date)
                val totalPayout = investmentsData.getActualTotalPayout(payout.date)

                labelRow.createCell(9).setCellValue(transfferedAmount.toDouble())
                labelRow.createCell(10).setCellValue(investmentsData.roundValueByDefaultScale(totalPayout.calculateDifferenceWithPreReturn()).toDouble())
                labelRow.createCell(11).setCellValue(investmentsData.getActualPartialReturnDays(payout.date).toDouble())
                labelRow.createCell(13).setCellValue(investmentsData.getEarlyReturnFeePercent())
                labelRow.createCell(14).setCellValue(investmentsData.roundValueByDefaultScale(totalPayout.preReturnAmount).toPlainString())
            }

            if (sheetName.startsWith("Early total return") || payout.date == lastPayoutDate) {
                val totalPayoutPeriodInDays = ChronoUnit.DAYS
                        .between(investmentsData.startDate, payout.date)
                        .toBigDecimal()
                investmentsData.calculateLastTotalReturnDifference(payout.date)
                val totalPayout = investmentsData.getActualTotalPayout(payout.date)
                labelRow.createCell(9).setCellValue(investmentsData.totalReturnTransferredAmount.toDouble())
                labelRow.createCell(10).setCellValue(investmentsData.roundValueByDefaultScale(totalPayout.calculateDifferenceWithPreReturn()).toDouble())
                labelRow.createCell(11).setCellValue(investmentsData.getActualPartialReturnDays(payout.date).toDouble())
                labelRow.createCell(12).setCellValue(totalPayoutPeriodInDays.toDouble())
                labelRow.createCell(13).setCellValue(investmentsData.getEarlyReturnFeePercent())
                labelRow.createCell(14).setCellValue(investmentsData.roundValueByDefaultScale(totalPayout.preReturnAmount).toPlainString())
            }
            index++
        }
    }

    fun loadInvestitionsFromXlsx(file: File, sheetNo: Int): Project {
        val workbook: Workbook = XSSFWorkbook(file)

        val firstSheet: Sheet = workbook.getSheetAt(sheetNo)
        val investments = mutableListOf<Investment>()
        var startDate = LocalDate.now()
        var endDate = LocalDate.now()
        var nextPayout = LocalDate.now()
        var payoutsDates = mutableListOf<LocalDate>()

        for(row : Row in firstSheet) {
            var investorId = ""
            var interestRate = 0.00
            var investedAmount = 0.00
            var notResident = false
            for(cell: Cell in row) {
                when(cell.columnIndex) {
                    0 -> investorId = cell.numericCellValue.toInt().toString()
                    1 -> interestRate = cell.numericCellValue
                    2 -> investedAmount = cell.numericCellValue
                    3 -> startDate = cell.dateCellValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    5 -> endDate = cell.dateCellValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    4 -> nextPayout = cell.dateCellValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    6 -> notResident = cell.numericCellValue > 0
                }
            }

            if(investorId != "") {
                investments.add(Investment(id = investorId,
                        investorId = "",
                        interestRate = BigDecimal(interestRate),
                        gpmPercent = BigDecimal.ZERO,
                        additionalInterestRate = BigDecimal.ZERO,
                        investedAmount = BigDecimal(investedAmount),
                        notResident = notResident))
            }
        }

        payoutsDates.add(nextPayout)
        payoutsDates.add(endDate)

        val project = Project(startDate, LocalDate.parse("28/03/2020", formatter), LoanAgreementType.OLD, "1", investments, BigDecimal.ZERO)

        payoutsDates.map{ project.scheduleInterestPayout(it) }

        return project
    }
}
