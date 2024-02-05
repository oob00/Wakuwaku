package com.project.wakuwaku.auth

import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.time.LocalDateTime
import java.util.*


@SpringBootTest
class AuthTest @Autowired constructor(
    private val authService: AuthService,
    private val userRepository: UserRepository
) {
    @DisplayName("로그인 성공 테스트")
    @Test
    fun testLoginSuccess() {
        val newUser = Users(
            id = "id",
            password = "pw",
            userType = 1,
            name = "name",
            nickname = "testNickname",
            createDt = LocalDateTime.now(),
            updateDt = LocalDateTime.now()
        )

        userRepository.save(newUser)

        val result: UserDetails = authService.loadUserByUsername("id")

        assertEquals("id", result.username)
    }

    @DisplayName("로그인 실패 테스트(아이디가 존재하지 않는 경우)")
    @Test
    fun testLoginFail() {
        val exception = assertThrows<UsernameNotFoundException> {
            authService.loadUserByUsername("new_id")
        }

        assertEquals("존재하지 않는 회원입니다.", exception.message)
    }
}