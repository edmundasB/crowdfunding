package com.profitus.crowdfunding.domain

import java.util.*

data class InvestmentId(val identifier: String){
    constructor(): this(UUID.randomUUID().toString())
    override fun toString(): String = identifier
}