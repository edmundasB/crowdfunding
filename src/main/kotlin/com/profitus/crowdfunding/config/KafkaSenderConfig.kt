package com.profitus.crowdfunding.config

/*import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer*/
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
/*import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import org.springframework.kafka.support.serializer.JsonSerializer*/
import java.util.*

/*@Configuration*/
class KafkaSenderConfig {

/*    @Bean
    fun producerConfigs(): Map<String, Any>? {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "kafka-service:9092"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return props
    }

    @Bean
    fun producerFactory(): ProducerFactory<String?, String?>? {
        return DefaultKafkaProducerFactory(producerConfigs()!!)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String?, String?>? {
        val template = KafkaTemplate(producerFactory()!!)
        template.setMessageConverter(StringJsonMessageConverter())
        return template
    }*/
}
