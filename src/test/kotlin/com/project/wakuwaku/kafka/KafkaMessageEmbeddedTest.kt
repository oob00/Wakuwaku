package com.project.wakuwaku.kafka

import com.project.wakuwaku.chat.KafkaMessageService
import com.project.wakuwaku.config.kafka.KafkaConstants
import com.project.wakuwaku.model.kafka.KafkaMessageDto
import com.project.wakuwaku.model.mongo.Chatting
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = [KafkaConstants.KAFKA_TOPIC])
class KafkaMessageEmbeddedTest {

    @Autowired
    private lateinit var kafkaMessageService: KafkaMessageService

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    private lateinit var consumer: Consumer<String, KafkaMessageDto>

    @BeforeEach
    fun setUp(){
        val producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker)
        val producerFactory = DefaultKafkaProducerFactory<String, KafkaMessageDto>(producerProps, StringSerializer(), JsonSerializer<KafkaMessageDto>())
        val kafkaTemplate = KafkaTemplate(producerFactory)

        kafkaMessageService.kafkaTemplate = kafkaTemplate

        val consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker)
        consumerProps["key.deserializer"] = StringDeserializer::class.java
        consumerProps["value.deserializer"] = JsonDeserializer::class.java
        consumerProps["group.id"] = KafkaConstants.GROUP_ID
        val consumerFactory = DefaultKafkaConsumerFactory<String, KafkaMessageDto>(consumerProps, StringDeserializer(), JsonDeserializer(KafkaMessageDto::class.java))
        consumer = consumerFactory.createConsumer()
        consumer.subscribe(listOf(KafkaConstants.KAFKA_TOPIC))
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer)
    }

    @AfterEach
    fun tearDown(){
        consumer.close()
    }

    @Test
    fun `test send and consume message`() {

        val testMessage = Chatting("testId", content = "Test Message")
        kafkaMessageService.send(KafkaConstants.KAFKA_TOPIC, testMessage)

        val records = KafkaTestUtils.getRecords(consumer)
        val receivedMessage = records.records(KafkaConstants.KAFKA_TOPIC).iterator().next().value()

        assertEquals(testMessage.content, receivedMessage.content)
    }

}

