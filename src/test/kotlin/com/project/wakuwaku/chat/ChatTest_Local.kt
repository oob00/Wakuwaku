package com.project.wakuwaku.chat

import com.project.wakuwaku.config.auth.JwtUtil
import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import com.project.wakuwaku.model.mongo.Chatting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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
class ChatTest_Local @Autowired constructor(
        private val userRepository: UserRepository,
        private val jwtUtil: JwtUtil
){
    private lateinit var messageQueue: LinkedBlockingQueue<Chatting>

    private lateinit var stompClient: WebSocketStompClient
    private lateinit var stompSession: StompSession

    private var wsUrl: String = "ws://localhost:8080/chat"

    private lateinit var user: Users

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
        stompSession = stompClient.connect(wsUrl, headers, null, object : StompSessionHandlerAdapter() {
        }).get(60, TimeUnit.SECONDS)

    }

    @AfterEach
    fun tearDown(){
        stompSession.disconnect()
        stompClient.stop()
    }

    @Test
    fun `입장 메세지 확인 - 로컬 카프카 기동 필요`() {

        val roomId = "roomId"

        stompSession.subscribe("/topic/$roomId", WakuStompFrameHandler(messageQueue))

        val testMessage = Chatting(roomId, content = "${user.nickname} 님이 접속하였습니다.")
        val receivedMessage = messageQueue.poll(5, TimeUnit.SECONDS)

        Assertions.assertEquals(testMessage.content, receivedMessage.content)
    }

    @Test
    fun `채팅 메세지 확인 - 로컬 카프카 기동 필요`() {

        val roomId = "roomId"

        stompSession.subscribe("/topic/$roomId", WakuStompFrameHandler(messageQueue))

        messageQueue.poll(5, TimeUnit.SECONDS)

        val testMessage = Chatting(chatRoomId = roomId, content = "안녕하세요")
        stompSession.send("/pub/message", testMessage)

        Thread.sleep(1000)

        val receivedMessage = messageQueue.poll(5, TimeUnit.SECONDS)

        Assertions.assertEquals(testMessage.content, receivedMessage.content)

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

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpAll(): Unit {

        }
    }
}

