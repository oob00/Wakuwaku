package com.project.wakuwaku.friend

import com.project.wakuwaku.friend.dto.FriendDto
import com.project.wakuwaku.model.jpa.friend.Friend
import com.project.wakuwaku.model.jpa.friend.FriendRepository
import com.project.wakuwaku.model.jpa.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class FriendService(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getFriendList(id: String): List<FriendDto> {
        val friendEntities: List<Friend> = friendRepository.findAllByIdAndIsFriend(id, true)

        val friends: List<FriendDto> = friendEntities.map { fe ->
            FriendDto(
                friendId = fe.friendId,
                friendNickname = userRepository.findById(fe.friendId).get().nickname
            )
        }

        return friends
    }

    fun requestFriend(id: String, friendId: String): Boolean {
        return try {
            friendRepository.save(
                Friend(
                    id = id,
                    friendId = friendId,
                    isFriend = false,
                    createDt = LocalDateTime.now(),
                    updateDt = LocalDateTime.now()
                )
            )

            true
        } catch (e: Exception) {
            log.error("친구 추가 요청 에러 : " + e.message)
            false
        }
    }

    fun getFriendRequestReceived(id: String): List<FriendDto> {
        val friendEntities: List<Friend> = friendRepository.findAllByFriendIdAndIsFriend(id, false)

        val receivedRequest: List<FriendDto> = friendEntities.map { fe ->
            FriendDto(
                friendId = fe.id,
                friendNickname = userRepository.findById(fe.id).get().nickname
            )
        }

        return receivedRequest
    }

    fun acceptFriend(id: String, friendId: String): Boolean {
        return try {
            val friend = friendRepository.findById(friendId)
            friend.updateIsFriend(true)
            friendRepository.save(friend)

            friendRepository.save(
                Friend(
                    id = id,
                    friendId = friendId,
                    isFriend = true,
                    createDt = LocalDateTime.now(),
                    updateDt = LocalDateTime.now()
                )
            )

            true
        } catch (e: Exception) {
            log.error("친구 요청 수락 에러 : " + e.message)
            false
        }
    }
}