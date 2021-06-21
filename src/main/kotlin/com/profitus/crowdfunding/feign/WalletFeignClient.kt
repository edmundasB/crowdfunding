package com.profitus.crowdfunding.feign

/*import org.springframework.cloud.openfeign.FeignClient*/
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

/*@FeignClient(name = "wallet-service", url = "wallet-service:8245/wallet-service/v1/wallet")*/
interface WalletFeignClient {
    @PostMapping("/transaction")
    fun makeTransaction(@RequestBody request: TransactionRequest)
}
