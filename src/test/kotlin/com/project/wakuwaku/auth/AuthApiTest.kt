package com.project.wakuwaku.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.wakuwaku.auth.dto.LoginDto
import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthApiTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var userRepository: UserRepository

    @BeforeAll
    fun beforeAll() {
        userRepository.save(Users(1L, "hello", "\$2a\$12\$7iKkXT2drx7q5aqUMIYaKOfMGj5HrmlkU0UDocYpPrLRg3PP3gKBC", 1, "email@email.com", "name", "nickname", LocalDateTime.now(), LocalDateTime.now()))
    }

    @DisplayName("로그인 API 테스트")
    @Test
    fun testLogin () {

        val loginDto = LoginDto("hello", "world")
        val loginDtoJson = objectMapper.writeValueAsString(loginDto)

        mockMvc.post("/api/auth/login")
        {
            contentType = MediaType.APPLICATION_JSON
            content = loginDtoJson
        }
            .andExpect {
                status { isOk() }
            }
            .andDo {
                print()
            }
    }
}