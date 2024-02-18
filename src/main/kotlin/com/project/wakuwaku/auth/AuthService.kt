package com.project.wakuwaku.auth

import com.project.wakuwaku.auth.dto.LoginDto
import com.project.wakuwaku.config.auth.JwtInfo
import com.project.wakuwaku.config.auth.JwtUtil
import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtUtil: JwtUtil,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val userRepository: UserRepository
) {

    fun login(dto: LoginDto): JwtInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(dto.id, dto.pw)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        return jwtUtil.createJwt(authentication.name)
    }

    fun getUserById(id: String): Users {
        return userRepository.findById(id).orElseThrow()
    }
}