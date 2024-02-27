package com.project.wakuwaku.friend

import com.project.wakuwaku.model.jpa.friend.Friend
import com.project.wakuwaku.model.jpa.friend.FriendRepository
import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class FriendTest @Autowired constructor(
    private val friendService: FriendService,
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository
) {

    @DisplayName("친구 목록 조회")
    @Test
    fun testGetFriendList() {
        val friends = listOf<Friend>(
            Friend(1L, "id", "friendId1", true, LocalDateTime.now(), LocalDateTime.now()),
            Friend(2L, "id", "friendId2", true, LocalDateTime.now(), LocalDateTime.now()),
            Friend(3L, "id", "friendId3", true, LocalDateTime.now(), LocalDateTime.now())
            )
        friendRepository.saveAll(friends)

        val users = listOf<Users>(
            Users(
                id = "friendId1",
                password = "\$2a\$12\$7iKkXT2drx7q5aqUMIYaKOfMGj5HrmlkU0UDocYpPrLRg3PP3gKBC",   // world 암호화
                userType = 1,
                email = "email@email.com",
                name = "name",
                nickname = "friendNickname1",
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
            ),
            Users(
                id = "friendId2",
                password = "\$2a\$12\$7iKkXT2drx7q5aqUMIYaKOfMGj5HrmlkU0UDocYpPrLRg3PP3gKBC",   // world 암호화
                userType = 1,
                email = "email@email.com",
                name = "name",
                nickname = "friendNickname2",
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
            ),
            Users(
                id = "friendId3",
                password = "\$2a\$12\$7iKkXT2drx7q5aqUMIYaKOfMGj5HrmlkU0UDocYpPrLRg3PP3gKBC",   // world 암호화
                userType = 1,
                email = "email@email.com",
                name = "name",
                nickname = "friendNickname3",
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
            )
        )

        userRepository.saveAll(users)

        val result = friendService.getFriendList("id")

        assertEquals(3, result.size)
        assertEquals("friendNickname1", result[0].friendNickname)
    }

    @DisplayName("친구 요청")
    @Test
    fun testRequestFriend() {
        val result = friendService.requestFriend("id", "friendId")

        assertEquals(true, result)
    }

    @DisplayName("받은 친구 요청 조회")
    @Test
    fun testGetFriendRequestReceived() {
        val friendRequest = listOf<Friend>(
            Friend(1L, "friend1", "id", false, LocalDateTime.now(), LocalDateTime.now()),
            Friend(2L, "friend2", "id", false, LocalDateTime.now(), LocalDateTime.now()),
            Friend(3L, "friend3", "id", false, LocalDateTime.now(), LocalDateTime.now())
        )

        friendRepository.saveAll(friendRequest)

        val users = listOf<Users>(
            Users(
                id = "friend1",
                password = "\$2a\$12\$7iKkXT2drx7q5aqUMIYaKOfMGj5HrmlkU0UDocYpPrLRg3PP3gKBC",   // world 암호화
                userType = 1,
                email = "email@email.com",
                name = "name",
                nickname = "friendNickname1",
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
            ),
            Users(
                id = "friend2",
                password = "\$2a\$12\$7iKkXT2drx7q5aqUMIYaKOfMGj5HrmlkU0UDocYpPrLRg3PP3gKBC",   // world 암호화
                userType = 1,
                email = "email@email.com",
                name = "name",
                nickname = "friendNickname2",
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
            ),
            Users(
                id = "friend3",
                password = "\$2a\$12\$7iKkXT2drx7q5aqUMIYaKOfMGj5HrmlkU0UDocYpPrLRg3PP3gKBC",   // world 암호화
                userType = 1,
                email = "email@email.com",
                name = "name",
                nickname = "friendNickname3",
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
            )
        )

        userRepository.saveAll(users)

        val result = friendService.getFriendRequestReceived("id")

        assertEquals(3, result.size)
        assertEquals("friendNickname1", result[0].friendNickname)
    }

    @DisplayName("친구 요청 수락")
    @Test
    fun testAcceptFriend() {
        friendRepository.save(
            Friend(
                id = "friend",
                friendId = "id",
                isFriend = false,
                createDt = LocalDateTime.now(),
                updateDt = LocalDateTime.now()
            )
        )

        val result = friendService.acceptFriend("id", "friend")

        assertEquals(true, result)
    }
}