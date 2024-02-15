package com.project.wakuwaku.auth

import com.project.wakuwaku.auth.dto.KakaoToken
import com.project.wakuwaku.auth.dto.LoginDto
import org.apache.kafka.common.security.auth.Login
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class WebAuthController(
    private val kakaoService: KakaoService,
    private val authService: AuthService
) {

    @GetMapping("/index")
    fun home(): String {
        return "index"
    }

    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @GetMapping("/auth/kakao/callback")
    fun kakaoCallback(@RequestParam("code") code: String, ra: RedirectAttributes): String {
        println("kakao code : ${code}")
        val token: KakaoToken = kakaoService.getToken(code)
        val id: Long = kakaoService.getUserInfo(token)

        println("kakao id : ${id}")
        println("kakao token : ${token.access_token}")

        val jwt = kakaoService.kakaoLogin(id.toString())

        println(jwt.accessToken)

        return "index"
    }
}