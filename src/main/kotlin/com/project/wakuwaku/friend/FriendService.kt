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

    fun requestFriend(id: String, friendId: String): String? {
        return try {
            val friend = friendRepository.save(
                Friend(
                    id = id,
                    friendId = friendId,
                    isFriend = false,
                    createDt = LocalDateTime.now(),
                    updateDt = LocalDateTime.now()
                )
            )

            friend.id
        } catch (e: Exception) {
            log.error("친구 추가 요청 에러 : " + e.message)
            null
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

    fun acceptFriend(id: String, friendId: String): String? {
        return try {
            // 요청자의 친구요청을 가져와서 친구 요청 수락 상태로 변경
            val request = friendRepository.findByIdAndFriendId(friendId, id)
            request.acceptFriend()
            friendRepository.save(request)

            // 요청자를 내 친구목록에 추가
            val myId = addRequesterToMyFriendList(request)

            myId
        } catch (e: Exception) {
            log.error("친구 요청 수락 에러 : " + e.message)
            null
        }
    }

    private fun addRequesterToMyFriendList(request: Friend): String {
        val accept = friendRepository.save(
            Friend(
                id = request.friendId,
                friendId = request.id,
                isFriend = true,
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
            )
        )

        return accept.id
    }
}