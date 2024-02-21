package com.project.wakuwaku.auth.dto

data class KakaoProfile(
    var id: Long,
    var connected_at: String,
    var kakao_account: KakaoAccount
) {
    data class KakaoAccount(
        var profile_nickname_needs_agreement: Boolean? = null,
        var profile_image_needs_agreement: Boolean? = null,
        var profile: Profile,
        var has_email: Boolean? = null,
        var email_needs_agreement: Boolean? = null,
        var is_email_valid: Boolean? = null,
        var is_email_verified: Boolean? = null,
        var email: String,
        var name: String
    ) {
        data class Profile(
            var nickname: String,
            var thumbnail_image_url: String? = null,
            var profile_image_url: String? = null,
            var is_default_image: Boolean? = null
        ) {
        }
    }
}
