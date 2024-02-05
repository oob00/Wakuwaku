package com.project.wakuwaku.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfiguration(
        private val stompHandler: StompHandler
) : WebSocketMessageBrokerConfigurer {

    // STOMP 엔드포인트를 등록하는 메서드
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS()
    }

    // 메시지 브로커를 구성하는 메서드
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        //kafka 사용으로 주석 처리
        //registry.enableSimpleBroker("/sub")
        //메세지 발행 요청 url -> 메세지 발생 시
        registry.setApplicationDestinationPrefixes("/pub")
    }

    // 클라이언트 인바운드 채널을 구성하는 메서드
    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(stompHandler)
    }

    // STOMP에서 64KB 이상의 데이터 전송을 못하는 문제 해결
    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        registry.setMessageSizeLimit(160 * 64 * 1024) // 10MB
        registry.setSendTimeLimit(20 * 10000) // 200 seconds
        registry.setSendBufferSizeLimit(3 * 512 * 1024) // 1.5MB
    }
}