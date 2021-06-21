package com.profitus.crowdfunding.service

import com.profitus.crowdfunding.model.FinancingEntity
/*import com.profitus.crowdfunding.repository.FinancingRepository*/
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class FinancingService {

/*    @Autowired
    private lateinit var financingRepository: FinancingRepository

    fun createFinancing(request: FinancingEntity){
        val financingEntity = financingRepository.findByProjectId(request.projectId)
        if(financingEntity.isPresent) {
            val financing = financingEntity.get()
            financing.requiredAmount = financing.requiredAmount
            financingRepository.save(financing)
        } else {
            financingRepository.save(request)
        }
    }

    fun addCollectedAmount(projectId: String, amount: BigDecimal){
        val financingEntity = financingRepository.findByProjectId(projectId)
        if(financingEntity.isPresent){
            val financing = financingEntity.get()
            financing.addCollectedAmount(amount)
*//*            financingRepository.save(financing)*//*
        } else {
            throw Exception("Financing for project id $projectId not found")
        }
    }*/

/*    fun findById(id: String): Optional<FinancingEntity> {
        return financingRepository.findById(id)
    }

    fun finByProjectId(projectId: String): Optional<FinancingEntity> {
        return financingRepository.findByProjectId(projectId)
    }

    fun listFinancing(): List<FinancingEntity> {
        return financingRepository.findAll().toList()
    }*/

}
