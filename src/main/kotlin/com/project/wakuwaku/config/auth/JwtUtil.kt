package com.project.wakuwaku.config.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class JwtUtil(
    private val customUserDetailService: CustomUserDetailService
) {

    @Value("\${jwt.secret}")
    lateinit var secretKey: String

    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }

    fun createJwt(userName: String): JwtInfo {
        val accessToken = Jwts.builder()
            .claim("userName", userName)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + Duration.ofHours(1).toMillis()))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return JwtInfo("Bearer", accessToken)
    }

    fun resolveToken(request: HttpServletRequest): String? {
        return request.getHeader("Authorization")
    }

    fun getAuthentication(token: String): Authentication {
        val userDetails = customUserDetailService.loadUserByUsername(getClaim(token)["userName"].toString())
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getClaim(token: String): Claims {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaim(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}