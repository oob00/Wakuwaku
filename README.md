# 와쿠와쿠
STOMP와 kafka,OAuth 2.0 을 활용한 채팅 서비스

#### 개발환경

    JDK 17
    Kotlin 1.9
    Gradle 8.5
    Spring boot 3.2.3
    Kafka 3.6.0
    MariaDB 10.11.6
    MongoDB 7.0.5    
    Redis 7.2.4

### API 인증 설정
Spring Security의 OAuth 2.0 Resource Owner Password 방식으로 설계하였고 토큰은 JWT 사용

- 카카오 로그인 연동
- 로그인을 제외한 API 호출 시 Header에 Authorization 항목 필수
- API 호출 시 필터를 통한 토큰 검증
- 액세스 토큰 유효기간: 1시간
- 리플레스 토큰 사용 및 재발급 기능 (개발중)

### 메세지 전달 플로우
STOMP와 Kafka를 활용하여 클라이언트에서 Websocket(STOMP)을 통해 Kafka로 메세지를 전달하며  
Kafka에서 특정 토픽(채팅방)으로 다시 메세지를 전송하는 방식

### 테스트 환경
    
    Kafka -> Embedded
    MariaDB -> H2    
    MongoDB -> Embedded
    Redis -> TestContainer