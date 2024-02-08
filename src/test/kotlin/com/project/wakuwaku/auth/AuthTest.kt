package com.project.wakuwaku.auth

import com.project.wakuwaku.auth.dto.LoginDto
import com.project.wakuwaku.config.auth.CustomUserDetailService
import com.project.wakuwaku.config.auth.JwtInfo
import com.project.wakuwaku.config.auth.JwtUtil
import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.time.LocalDateTime
import java.util.*


@SpringBootTest
class AuthTest @Autowired constructor(
    private val authService: AuthService,
    private val customUserDetailService: CustomUserDetailService,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {

    @DisplayName("JWT 생성 테스트")
    @Test
    fun testCreateJwt() {
        val result = jwtUtil.createJwt("홍길동")

        Assertions.assertNotNull(result)
    }

    @DisplayName("로그인 성공 테스트(성공시 jwt 생성)")
    @Test
    fun testLoginSuccess() {
        val newUser = Users(
            id = "id",
            password = "\$2a\$12\$7iKkXT2drx7q5aqUMIYaKOfMGj5HrmlkU0UDocYpPrLRg3PP3gKBC",   // world 암호화
            userType = 1,
            name = "name",
            nickname = "testNickname",
            createDt = LocalDateTime.now(),
            updateDt = LocalDateTime.now()
        )

        userRepository.save(newUser)

        val result: JwtInfo = authService.login(LoginDto(newUser.id, "world"))
        assertEquals("id", jwtUtil.getClaim(result.accessToken)?.body?.get("userName"))
    }

    @DisplayName("로그인 실패 테스트(아이디가 존재하지 않는 경우)")
    @Test
    fun testLoginFail() {
        val exception = assertThrows<UsernameNotFoundException> {
            customUserDetailService.loadUserByUsername("new_id")
        }

        assertEquals("존재하지 않는 회원입니다.", exception.message)
    }
}