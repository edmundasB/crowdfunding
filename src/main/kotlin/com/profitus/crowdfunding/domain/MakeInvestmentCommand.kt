package com.profitus.crowdfunding.domain

/*import org.axonframework.modelling.command.TargetAggregateIdentifier*/
import java.math.BigDecimal

data class MakeInvestmentCommand(/*@TargetAggregateIdentifier */override val aggregateIdentifier: InvestmentId,
                                 val projectId: String,
                                 val partyId: String,
                                 val projectName: String? = "",
                                 val amount: BigDecimal,
                                 val interestRate: BigDecimal,
                                 var investmentTermMonths: BigDecimal? = BigDecimal.ZERO,
                                 var interestFrequency: String? = "" ): AbstractInvestmentCommand(aggregateIdentifier)
