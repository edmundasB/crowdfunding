package com.profitus.crowdfunding.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/*import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc*/

@Configuration
/*@EnableSwagger2*/
class SwaggerConfig {
    val CONTACT: Contact = Contact("Murali", "http://profitus.com/",
            "bianka@profitus.lt")
    val DEFAULT_API = ApiInfo("Crowdfunding application command api",
            "Crowdfunding application", "1.0", "urn:tos", CONTACT,
            "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", ArrayList())
    val consumes: Set<String> = HashSet<String>(Arrays.asList("application/json"))
    val produces: Set<String> = HashSet<String>(Arrays.asList("application/json"))

    @Bean
    fun api(): Docket? {
        return Docket(DocumentationType.SWAGGER_2).apiInfo(DEFAULT_API).consumes(consumes).produces(produces)
    }

}
