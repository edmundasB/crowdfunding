package com.profitus.crowdfunding.config

/*import feign.FeignException*/
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import javax.servlet.http.HttpServletResponse

@EnableWebMvc
@RestControllerAdvice
class GlobalExceptionHandler {
/*    @ExceptionHandler(FeignException.BadRequest::class)
    fun handleFeignBadRequestException(e: FeignException, response: HttpServletResponse): String? {
        response.status = e.status()
        return e.contentUTF8()
    }

    @ExceptionHandler(FeignException.InternalServerError::class)
    fun handleFeignInternalServerException(e: FeignException, response: HttpServletResponse): String? {
        response.status = e.status()
        return e.contentUTF8()
    }*/


}
