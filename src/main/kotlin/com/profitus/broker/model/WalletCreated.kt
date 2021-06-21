package com.profitus.broker.model

import com.fasterxml.jackson.annotation.JsonProperty

data class WalletCreated(@JsonProperty val projectId: String = "",
                         @JsonProperty val walletId: String = "")