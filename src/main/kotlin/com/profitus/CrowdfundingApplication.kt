package com.profitus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
/*import org.springframework.cloud.client.discovery.EnableDiscoveryClient*/
/*import org.springframework.cloud.netflix.ribbon.RibbonClient*/
/*import org.springframework.cloud.openfeign.EnableFeignClients*/

@SpringBootApplication
/*@EnableDiscoveryClient
@EnableFeignClients
@RibbonClient(name = "crowdfunding-service")*/
class CrowdfundingApplication

fun main(args: Array<String>) {
    runApplication<CrowdfundingApplication>(*args)
}
