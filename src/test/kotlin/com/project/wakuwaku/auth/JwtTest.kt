package com.project.wakuwaku.auth

import com.project.wakuwaku.config.auth.JwtUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class JwtTest {

    @DisplayName("JWT 생성 테스트")
    @Test
    fun testCreateJwt() {
        val result = JwtUtil().createJwt("홍길동")

        Assertions.assertNotNull(result)
    }
}