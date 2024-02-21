package com.project.wakuwaku.auth

import com.project.wakuwaku.auth.dto.KakaoProfile
import com.project.wakuwaku.auth.dto.KakaoToken
import com.project.wakuwaku.config.auth.JwtInfo
import com.project.wakuwaku.config.auth.JwtUtil
import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class KakaoService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {

    @Value("\${spring.security.oauth2.client.registration.kakao.client-id}")
    lateinit var secretKey: String

    fun getToken(code: String): KakaoToken {
        val parameters: MultiValueMap<String, String> = LinkedMultiValueMap()
        parameters.add("grant_type", "authorization_code")
        parameters.add("client_id", secretKey)
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

    fun kakaoLogin(profile: KakaoProfile): JwtInfo {
        val exist: Boolean = userRepository.existsById(profile.id.toString())

        val jwt: JwtInfo = if (!exist) {
            val user = Users(
                id = profile.id.toString(),
                password = "",
                userType = 2,
                email = profile.kakao_account.email,
                name = profile.kakao_account.name,
                nickname = profile.kakao_account.profile.nickname,
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
            )

            val saveUser = userRepository.save(user)

            jwtUtil.createJwt(saveUser)
        } else {
            val user = userRepository.findById(profile.id.toString()).get()
            jwtUtil.createJwt(user)
        }

        return jwt
    }
}