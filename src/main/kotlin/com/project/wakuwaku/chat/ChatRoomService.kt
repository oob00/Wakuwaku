package com.project.wakuwaku.chat

import com.project.wakuwaku.model.redis.chatroom.Chatroom
import jakarta.annotation.Resource
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import java.util.*


@Service
class ChatRoomService(private val redisTemplate: RedisTemplate<String, String>) {

    companion object {
        private const val CHAT_ROOMS = "CHAT_ROOM" // 채팅룸 저장
        private const val USER_COUNT = "USER_COUNT" // 채팅룸에 입장한 클라이언트수 저장
        private const val ENTER_INFO = "ENTER_INFO"// 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    }

    @Resource(name = "redisTemplate")
    private lateinit var hashOpsChatRoom: HashOperations<String, String, Chatroom>

    @Resource(name = "redisTemplate")
    private lateinit var hashOpsEnterInfo: HashOperations<String, String, String>

    @Resource(name = "redisTemplate")
    private lateinit var valueOps: ValueOperations<String, String>

    init {
        hashOpsChatRoom = redisTemplate.opsForHash<String, Chatroom>()
        hashOpsEnterInfo = redisTemplate.opsForHash<String, String>()
        valueOps = redisTemplate.opsForValue()
    }

    // 모든 채팅방 조회
    fun findAllRoom(): List<Chatroom> {
        return hashOpsChatRoom.values(CHAT_ROOMS)
    }

    // 특정 채팅방 조회
    fun findRoomById(id: String): Chatroom? {
        return hashOpsChatRoom.get(CHAT_ROOMS, id)
    }

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
    fun createChatRoom(name: String): Chatroom {
        val chatRoom: Chatroom = Chatroom(name)
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.roomId, chatRoom)
        return chatRoom
    }

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    fun setUserEnterInfo(sessionId: String, roomId: String) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId)
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    fun getUserEnterRoomId(sessionId: String): String {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId).toString()
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    fun removeUserEnterInfo(sessionId: String) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId)
    }

    // 채팅방 유저수 조회
    fun getUserCount(roomId: String): Long {
        var key : String = USER_COUNT + "_" + roomId;
        return valueOps.get(key)?.toLongOrNull() ?: 0L
    }

    // 채팅방에 입장한 유저수 +1
    fun plusUserCount(roomId: String): Long {
        val count: String? = valueOps.get(USER_COUNT + "_" + roomId)
        val updatedCount = count?.toLongOrNull()?.plus(1) ?: 1
        valueOps.set(USER_COUNT + "_" + roomId, updatedCount.toString())

        return updatedCount
    }

    // 채팅방에 입장한 유저수 -1
    fun minusUserCount(roomId: String): Long {
        val count: String? = valueOps.get(USER_COUNT + "_" + roomId)
        val updatedCount = count?.toLongOrNull()?.minus(1) ?: 0
        valueOps.set(USER_COUNT + "_" + roomId, updatedCount.toString())

        return updatedCount
    }

    /**
     * destination정보에서 roomId 추출
     */
    fun getRoomId(destination: String): String {
        val lastIndex = destination.lastIndexOf('/')
        return if (lastIndex != -1) destination.substring(lastIndex + 1)
        else ""
    }

}