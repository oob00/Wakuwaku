package com.project.wakuwaku.model.jpa.friend

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Friend(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val seq: Long = 0L,
    val id: String,
    val friendId: String,
    isFriend: Boolean,
    @CreatedDate
    var createDt: LocalDateTime,
    @LastModifiedDate
    var updateDt: LocalDateTime
) {
    var isFriend: Boolean = isFriend
        private set

    fun acceptFriend(isFriend: Boolean) {
        this.isFriend = isFriend
    }
}