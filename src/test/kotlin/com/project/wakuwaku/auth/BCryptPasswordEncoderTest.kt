package com.project.wakuwaku.auth

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


class BCryptPasswordEncoderTest {

    @Test
    fun `BCrypt 암호화 인코딩 확인 테스트`() {
        val password = "test"
        val encoder = BCryptPasswordEncoder()

        // 패스워드를 BCrypt로 암호화
        val encodedPassword = encoder.encode(password)

        // 암호화된 패스워드가 원본 패스워드와 일치하는지 확인
        assertTrue(encoder.matches(password, encodedPassword))
    }
}