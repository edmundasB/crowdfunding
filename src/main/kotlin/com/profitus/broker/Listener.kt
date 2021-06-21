package com.profitus.broker

import com.profitus.broker.model.ProjectOpened
import com.profitus.crowdfunding.model.FinancingEntity
import com.profitus.crowdfunding.service.FinancingService
import org.springframework.beans.factory.annotation.Autowired
/*import org.springframework.kafka.annotation.KafkaListener*/
import org.springframework.stereotype.Service

@Service
class Listener {
/*
    @Autowired
    private lateinit var financingService: FinancingService
*/

/*    @KafkaListener(id = "project-crowdfunding-msg-consumer",  topics = ["project-opened-msg-topic"])*/
/*    fun consumeMessage(message: ProjectOpened) {*/
/*
        financingService.createFinancing(FinancingEntity(projectId = message.projectId,
                                        requiredAmount = message.requiredAmount,
                                        interestRate = message.interest,
                                        projectName = message.projectName,
                                        investmentTermMonths = message.investmentTermMonths,
                                        interestFrequency = message.interestFrequency))
    }
*/

}
