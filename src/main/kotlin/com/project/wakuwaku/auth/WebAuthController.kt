package com.project.wakuwaku.auth

import com.project.wakuwaku.auth.dto.KakaoProfile
import com.project.wakuwaku.auth.dto.KakaoToken
import com.project.wakuwaku.config.auth.JwtUtil
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class WebAuthController(
    private val jwtUtil: JwtUtil,
    private val kakaoService: KakaoService,
    private val authService: AuthService
) {

    @GetMapping("/index")
    fun home(model: Model, auth: Authentication): String {
        val user = authService.getUserById(auth.name)
        val token = jwtUtil.createJwt(user)

        model.addAttribute("nickname", user.nickname)
        model.addAttribute("token", token)
        return "index"
    }

    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @GetMapping("/auth/kakao/callback")
    fun kakaoCallback(@RequestParam("code") code: String, ra: RedirectAttributes, model: Model): String {
        println("kakao code : ${code}")
        val token: KakaoToken = kakaoService.getToken(code)
        val profile: KakaoProfile = kakaoService.getUserInfo(token)

        println("kakao id : ${profile.id}")
        println("kakao token : ${token.access_token}")

        val jwt = kakaoService.kakaoLogin(profile.id.toString())

        println(jwt.accessToken)

        model.addAttribute("nickname", profile.kakao_account.profile.nickname)
        model.addAttribute("token", jwt)

        return "/index"
    }
}