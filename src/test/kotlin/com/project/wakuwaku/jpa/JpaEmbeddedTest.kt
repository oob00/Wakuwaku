package com.project.wakuwaku.jpa

import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDateTime

@DataJpaTest
class JpaEmbeddedTest @Autowired constructor(
        val entityManager: TestEntityManager,
        val userRepository: UserRepository
) {

    @Test
    fun `회원 생성 테스트`() {
        val newUser = Users(
            id = "id",
            password = "pw",
            userType = 1,
            name = "name",
            nickname = "testNickname",
            createDt = LocalDateTime.now(),
            updateDt = LocalDateTime.now()
        )

        userRepository.save(newUser)
        assertNotNull(newUser.id)
    }

    @Test
    fun `회원 조회 테스트`() {
        val user = Users(
            id = "id",
            password = "pw",
            userType = 1,
            name = "name",
            nickname = "findTest",
            createDt = LocalDateTime.now(),
            updateDt = LocalDateTime.now()
        )
        entityManager.persist(user)
        entityManager.flush()
        val foundUser = userRepository.findById(user.seq).orElse(null)
        assertNotNull(foundUser)
        assertEquals(user.nickname, foundUser?.nickname)
    }

    @Test
    fun `회원 수정 테스트`() {
        val user = Users(
            id = "id",
            password = "pw",
            userType = 1,
            name = "name",
            nickname = "originalNickname",
            createDt = LocalDateTime.now(),
            updateDt = LocalDateTime.now()
        )
        entityManager.persist(user)
        entityManager.flush()

        user.updateNickname("updatedNickname")
        userRepository.save(user)

        val updatedUser = userRepository.findById(user.seq).orElse(null)
        assertNotNull(updatedUser)
        assertEquals("updatedNickname", updatedUser?.nickname)
    }

    @Test
    fun `회원 삭제 테스트`() {
        val user = Users(
            id = "id",
            password = "pw",
            userType = 1,
            name = "name",
            nickname = "toDelete",
            createDt = LocalDateTime.now(),
            updateDt = LocalDateTime.now()
        )
        entityManager.persist(user)
        entityManager.flush()

        userRepository.delete(user)

        val deletedUser = userRepository.findById(user.seq).orElse(null)
        assertNull(deletedUser)
    }
}