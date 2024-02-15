package com.project.wakuwaku.auth.dto

data class KakaoToken(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String,
    val refresh_token_expires_in: Int
) {
}