package com.profitus.crowdfunding.rest

import com.profitus.crowdfunding.domain.InvestmentId
import com.profitus.crowdfunding.domain.MakeInvestmentCommand
import com.profitus.crowdfunding.feign.TransactionRequest
import com.profitus.crowdfunding.feign.WalletFeignClient
import com.profitus.crowdfunding.rest.dto.InvestmentRequest
import com.profitus.crowdfunding.service.FinancingService
/*import org.axonframework.commandhandling.gateway.CommandGateway*/
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

/*@RestController
@RequestMapping("/v1/investment")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])*/
class InvestmentController {
/*
    @Autowired
    private lateinit var financingService: FinancingService
    @Autowired
    private lateinit var walletFeignClient: WalletFeignClient
    @Autowired
    private lateinit var commandGateway: CommandGateway

    @PostMapping
    fun investIntoProject(@RequestBody request: InvestmentRequest): ResponseEntity<Any> {
        val projectEntity = financingService.finByProjectId(request.projectId)

        if(request.amount < BigDecimal( 100)){
           return ResponseEntity("Investment amount ${request.amount} must be more or equal than 100", HttpStatus.BAD_REQUEST)
        }

        return if(projectEntity.isPresent) {
            val project = projectEntity.get()
            if(request.amount > project.getRemainingAmount()){
                return ResponseEntity("Investment amount ${request.amount} can't be bigger than  ${project.getRemainingAmount()} ", HttpStatus.BAD_REQUEST)
            }

            val transaction = TransactionRequest(debitPartyId = request.partyId,
                    creditProjectId = request.projectId,
                    debitAmount = request.amount)
            walletFeignClient.makeTransaction(transaction)

            financingService.addCollectedAmount(request.projectId, request.amount)
            commandGateway.sendAndWait<Any>(MakeInvestmentCommand(InvestmentId(),
                    projectId = request.projectId,
                    partyId = request.partyId,
                    projectName = project.projectName,
                    amount = request.amount,
                    interestRate = project.interestRate!!,
                    investmentTermMonths = project.investmentTermMonths,
                    interestFrequency = project.interestFrequency ))

            ResponseEntity(HttpStatus.CREATED)
        } else {
            ResponseEntity("Project ${request.projectId} not found", HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/{id}")
    fun getInvestmentInfo(@PathVariable id: String): ResponseEntity<Any> {
       return ResponseEntity(financingService.finByProjectId(id), HttpStatus.OK)
    }

    @GetMapping
    fun getInvestments(): ResponseEntity<Any> {
        return ResponseEntity(financingService.listFinancing(), HttpStatus.OK)
    }
*/

}
