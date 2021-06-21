package com.profitus.broker.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class ProjectOpened(@JsonProperty val projectId: String = "",
                         @JsonProperty val projectName: String? = "",
                         @JsonProperty val requiredAmount: BigDecimal? = BigDecimal.ZERO,
                         @JsonProperty val interest: BigDecimal? = BigDecimal.ZERO,
                         @JsonProperty var investmentTermMonths: BigDecimal? = BigDecimal.ZERO,
                         @JsonProperty var interestFrequency: String? = "")