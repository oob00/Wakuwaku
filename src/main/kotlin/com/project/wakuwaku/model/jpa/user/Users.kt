package com.project.wakuwaku.model.jpa.user

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Users(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val seq: Long = 0L,
    val id: String,
    var password: String,
    val userType: Int,  // wakuwaku=1, kakao=2
    email: String,
    name: String,
    nickname: String,
    @CreatedDate
    var createDt: LocalDateTime,
    @LastModifiedDate
    var updateDt: LocalDateTime
) {
    var name: String = name
        private set
    var nickname: String = nickname
        private set

    var email: String = email
        private set

    fun updateName(name: String) {
        this.name = name
    }
    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    fun updateEmail(email: String) {
        this.email = email
    }
}