package com.profitus.crowdfunding.rest.dto

import java.math.BigDecimal

data class InvestmentRequest(val projectId: String = "",
                             val partyId: String = "",
                             val amount: BigDecimal = BigDecimal.ZERO )