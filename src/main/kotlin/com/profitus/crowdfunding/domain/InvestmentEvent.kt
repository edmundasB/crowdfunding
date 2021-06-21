package com.profitus.crowdfunding.domain

import java.math.BigDecimal

data class InvestmentEvent(val aggregateIdentifier: InvestmentId,
                           val projectId: String,
                           val partyId: String,
                           val projectName: String? = "",
                           val amount: BigDecimal,
                           val interestRate: BigDecimal,
                           var investmentTermMonths: BigDecimal? = BigDecimal.ZERO,
                           var interestFrequency: String? = "")