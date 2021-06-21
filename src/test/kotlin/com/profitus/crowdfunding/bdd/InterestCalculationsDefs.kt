package com.profitus.crowdfunding.bdd

import com.profitus.crowdfunding.service.Project
import com.profitus.crowdfunding.service.PaymentExcelHandler
import java.time.LocalDate

abstract class InterestCalculationsDefs {
     var handler: PaymentExcelHandler = PaymentExcelHandler()
     var investment: Project? = null
     lateinit var paymentDate: LocalDate
}