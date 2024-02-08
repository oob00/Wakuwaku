package com.project.wakuwaku.config.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class JwtUtil {

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

    fun getClaim(token: String): Jws<Claims>? {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
    }
}