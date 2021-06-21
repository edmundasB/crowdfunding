package com.profitus.crowdfunding.config

/*import org.apache.kafka.clients.consumer.ConsumerConfig*/
/*import org.apache.kafka.common.serialization.StringDeserializer*/
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
/*import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer*/
import java.util.*

/*@Configuration*/
/*@EnableKafka*/
class KafkaReceiverConfig {
/*    @Bean
    fun consumerConfigs(): Map<String, Any>? {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "kafka-service:9092"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        props[ConsumerConfig.GROUP_ID_CONFIG] = "json"
        return props
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String?, String?> {
        return DefaultKafkaConsumerFactory(consumerConfigs()!!, StringDeserializer(),
                JsonDeserializer())
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String>? {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.setConsumerFactory(consumerFactory())
        factory.setMessageConverter(StringJsonMessageConverter())
        return factory
    }*/

}
