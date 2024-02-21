package com.project.wakuwaku.auth

import com.project.wakuwaku.auth.dto.LoginDto
import com.project.wakuwaku.auth.dto.RegistUserDto
import com.project.wakuwaku.config.auth.JwtInfo
import com.project.wakuwaku.config.auth.JwtUtil
import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService(
    private val jwtUtil: JwtUtil,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val userRepository: UserRepository
) {
    fun register(dto: RegistUserDto): Boolean {
        val existUser = userRepository.existsById(dto.id)

        if (!existUser) {
            val encoder = BCryptPasswordEncoder()

            val user = Users(
                id = dto.id,
                password = encoder.encode(dto.password),
                userType = 1,
                email = dto.email,
                name = dto.name,
                nickname = dto.nickname,
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
            )

            userRepository.save(user)

            return true
        } else return false
    }

    fun login(dto: LoginDto): JwtInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(dto.id, dto.pw)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        val user = userRepository.findById(authentication.name).get()

        return jwtUtil.createJwt(user)
    }

    fun getUserById(id: String): Users {
        return userRepository.findById(id).orElseThrow()
    }
}