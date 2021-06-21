package com.profitus.crowdfunding.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.GetMapping
import reactor.core.publisher.Flux;



@Controller
class PingController {

    @GetMapping("/")
    @ResponseBody
    fun health(): Flux<String> {
        return Flux.just("crowdfunding service api version V1")
    }

}
