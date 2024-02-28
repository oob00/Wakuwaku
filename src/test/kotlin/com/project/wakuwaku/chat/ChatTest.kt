package com.project.wakuwaku.chat

import com.project.wakuwaku.config.StompHandler
import com.project.wakuwaku.config.auth.JwtUtil
import com.project.wakuwaku.config.kafka.KafkaConstants
import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import com.project.wakuwaku.model.kafka.KafkaMessageDto
import com.project.wakuwaku.model.mongo.Chatting
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = [KafkaConstants.KAFKA_TOPIC])
class ChatTest @Autowired constructor(
        private val userRepository: UserRepository,
        private val jwtUtil: JwtUtil
){
    private lateinit var messageQueue: LinkedBlockingQueue<Chatting>

    private lateinit var stompClient: WebSocketStompClient
    private lateinit var stompSession: StompSession

    private var wsUrl: String = "ws://localhost:8080/chat"

    private lateinit var user: Users

    @Autowired
    private lateinit var kafkaMessageService: KafkaMessageService

    @Autowired
    private lateinit var stompHandler: StompHandler

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    private lateinit var consumer: Consumer<String, KafkaMessageDto>

    @BeforeEach
    fun setup() {

        messageQueue = LinkedBlockingQueue()

        user = CreateUser()

        val headers = WebSocketHttpHeaders() // 헤더에 토큰 삽입
        headers.add("Authorization", jwtUtil.createJwt(user).accessToken)

        // init setting
        stompClient = WebSocketStompClient(SockJsClient(listOf(WebSocketTransport(StandardWebSocketClient()))))
        stompClient.messageConverter = MappingJackson2MessageConverter()

        // Connection
        stompSession = stompClient.connectAsync(wsUrl, headers, null, object : StompSessionHandlerAdapter() {
        }).get(10, TimeUnit.SECONDS)

        Thread.sleep(1000)

        val producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker)
        val producerFactory = DefaultKafkaProducerFactory<String, KafkaMessageDto>(producerProps, StringSerializer(), JsonSerializer<KafkaMessageDto>())
        val kafkaTemplate = KafkaTemplate(producerFactory)

        kafkaMessageService.kafkaTemplate = kafkaTemplate
        stompHandler.kafkaTemplate = kafkaTemplate

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
        stompClient.stop()
        stompSession.disconnect()
    }

    @Test
    fun `입장 메세지 확인`() {

        try {

            val roomId = "roomId"

            stompSession.subscribe("/topic/$roomId", WakuStompFrameHandler(messageQueue))

            val testMessage = Chatting(roomId, content = "${user.nickname} 님이 접속하였습니다.")

            Thread.sleep(1000)

            val records = KafkaTestUtils.getRecords(consumer)
            val receivedMessage = records.records(KafkaConstants.KAFKA_TOPIC).iterator().next().value()

            Assertions.assertEquals(testMessage.content, receivedMessage.content)

        }catch (e: Exception){
            println(e)
            e.printStackTrace()
        }

    }

    @Test
    fun `채팅 메세지 확인`() {

        try {
            val roomId = "roomId"

            stompSession.subscribe("/topic/$roomId", WakuStompFrameHandler(messageQueue))

            //접속 메세지 제외
            KafkaTestUtils.getRecords(consumer).records(KafkaConstants.KAFKA_TOPIC).iterator().next().value()

            val testMessage = Chatting(roomId, content = "안녕하세요")
            stompSession.send("/pub/message", testMessage)

            Thread.sleep(1000)

            val records = KafkaTestUtils.getRecords(consumer)
            val receivedMessage = records.records(KafkaConstants.KAFKA_TOPIC).iterator().next().value()

            Assertions.assertEquals(testMessage.content, receivedMessage.content)

        }catch (e: Exception){
            println(e)
            e.printStackTrace()
        }
    }


    private inner class WakuStompFrameHandler(private val messageQueue: LinkedBlockingQueue<Chatting>) : StompFrameHandler {
        override fun getPayloadType(stompHeaders: StompHeaders): Type {
            return Chatting::class.java
        }

        override fun handleFrame(stompHeaders: StompHeaders, o: Any?) {
            println("handleFrame: "+o.toString())
            messageQueue.offer(o as Chatting)
        }
    }

    fun CreateUser(): Users{
        val newUser = Users(
                id = "test",
                password = "test",
                userType = 1,
                email = "test@email.com",
                name = "testName",
                nickname = "testNickname",
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
        )

        if(userRepository.findById(newUser.id).isEmpty)
            userRepository.save(newUser)

        return newUser
    }
}

