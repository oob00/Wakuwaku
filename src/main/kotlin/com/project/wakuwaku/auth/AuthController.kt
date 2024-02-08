package com.project.wakuwaku.auth

import com.project.wakuwaku.auth.dto.LoginDto
import com.project.wakuwaku.config.auth.JwtInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<JwtInfo> {
        val token = authService.login(loginDto)

        return ResponseEntity.ok(token)
    }

}