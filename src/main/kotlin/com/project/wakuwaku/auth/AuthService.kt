package com.project.wakuwaku.auth

import com.project.wakuwaku.auth.dto.LoginDto
import com.project.wakuwaku.config.auth.JwtInfo
import com.project.wakuwaku.config.auth.JwtUtil
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtUtil: JwtUtil
) {

    fun login(dto: LoginDto): JwtInfo {
        return jwtUtil.createJwt(dto.id)
    }
}