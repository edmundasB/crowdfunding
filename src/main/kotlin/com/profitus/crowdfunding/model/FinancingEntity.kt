package com.profitus.crowdfunding.model

import java.math.BigDecimal
import java.util.*
/*
import javax.persistence.Entity
import javax.persistence.Id
*/

/*@Entity*/
class FinancingEntity(val projectId: String = "",
                      val projectName: String? = "",
                      var requiredAmount: BigDecimal? = BigDecimal.ZERO,
                      var interestRate: BigDecimal? = BigDecimal.ZERO,
                      var investmentTermMonths: BigDecimal? = BigDecimal.ZERO,
                      var interestFrequency: String? = "") {
 /*   @Id*/
    var id: String = UUID.randomUUID().toString()
    var collectedAmount: BigDecimal = BigDecimal.ZERO

    fun addCollectedAmount(amount: BigDecimal){
        this.collectedAmount = this.collectedAmount.plus(amount)
    }

    fun getRemainingAmount(): BigDecimal? {
        return this.requiredAmount?.minus(this.collectedAmount)
    }
}
