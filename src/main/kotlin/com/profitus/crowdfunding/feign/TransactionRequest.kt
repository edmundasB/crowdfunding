package com.profitus.crowdfunding.feign

import java.math.BigDecimal

data class TransactionRequest(val debitPartyId: String = "",
                              val debitAmount: BigDecimal = BigDecimal.ZERO,
                              val creditProjectId: String = "")