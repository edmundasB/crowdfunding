package com.profitus.crowdfunding.domain

abstract class AbstractInvestmentCommand(open val aggregateIdentifier: InvestmentId)