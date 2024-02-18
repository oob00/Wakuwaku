package com.project.wakuwaku.auth

import com.project.wakuwaku.auth.dto.KakaoProfile
import com.project.wakuwaku.auth.dto.KakaoToken
import com.project.wakuwaku.auth.dto.LoginDto
import com.project.wakuwaku.config.auth.JwtInfo
import com.project.wakuwaku.model.jpa.user.UserRepository
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class KakaoService(
    private val userRepository: UserRepository,
    private val authService: AuthService
) {

    fun getToken(code: String): KakaoToken {
        val parameters: MultiValueMap<String, String> = LinkedMultiValueMap()
        parameters.add("grant_type", "authorization_code")
        parameters.add("client_id", "1fded70225514d5d76e262cfa79e75bc")
        parameters.add("redirect_uri", "http://localhost:8080/auth/kakao/callback")
        parameters.add("code", code)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.accept = listOf(MediaType.APPLICATION_JSON)


        val restTemplate = RestTemplate()
        val httpEntity = HttpEntity(parameters, headers)


        val response: ResponseEntity<KakaoToken> = restTemplate.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            httpEntity,
            KakaoToken::class.java
        )

        return response.body as KakaoToken
    }

    fun getUserInfo(token: KakaoToken): KakaoProfile {
        val parameters: MultiValueMap<String, String> = LinkedMultiValueMap()

        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer " + token.access_token)
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED


        val restTemplate = RestTemplate()
        val httpEntity = HttpEntity(parameters, headers)


        val response: ResponseEntity<KakaoProfile> = restTemplate.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST,
            httpEntity,
            KakaoProfile::class.java
        )

        return response.body as KakaoProfile
    }

    fun kakaoLogin(id: String): JwtInfo {
        val exist: Boolean = userRepository.existsById(id)

        val jwt: JwtInfo
        jwt = if (!exist) {
            TODO("회원 가입")
            authService.login(LoginDto(id, "pw"))
        } else {
            authService.login(LoginDto(id, "pw"))
        }

        return jwt
    }
}