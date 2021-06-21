package com.profitus.crowdfunding.domain

/*import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate*/
import java.math.BigDecimal

/*@Aggregate*/
class Investment {
  /*  @AggregateIdentifier
    private lateinit var id: InvestmentId
    private lateinit var projectId: String
    private lateinit var partyId: String
    private lateinit var amount: BigDecimal
    private lateinit var interestRate: BigDecimal
    private var investmentTermMonths: BigDecimal? = null
    private var interestFrequency: String? = null

    constructor()

    @CommandHandler
    constructor(makeInvestmentCommand: MakeInvestmentCommand) {
        AggregateLifecycle.apply(InvestmentEvent(makeInvestmentCommand.aggregateIdentifier,
                makeInvestmentCommand.projectId,
                makeInvestmentCommand.partyId,
                makeInvestmentCommand.projectName,
                makeInvestmentCommand.amount,
                makeInvestmentCommand.interestRate,
                makeInvestmentCommand.investmentTermMonths,
                makeInvestmentCommand.interestFrequency))
    }

    @EventSourcingHandler
    fun on(applicationCreatedEvent: InvestmentEvent) {
        id = applicationCreatedEvent.aggregateIdentifier
        projectId = applicationCreatedEvent.projectId
        partyId = applicationCreatedEvent.partyId
        amount = applicationCreatedEvent.amount
        interestRate = applicationCreatedEvent.interestRate
        investmentTermMonths = applicationCreatedEvent.investmentTermMonths
        interestFrequency = applicationCreatedEvent.interestFrequency
    }
*/
}
