package com.project.wakuwaku.redis

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@DataRedisTest
@Testcontainers
class RedisTest {
    companion object {
        private val logger = LoggerFactory.getLogger(RedisTest::class.java)

        @Container
        val redisContainer: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:latest"))
                .withExposedPorts(6379)
                .withReuse(true)
                .apply {
                    withLogConsumer(Slf4jLogConsumer(logger))
                }

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            println("Redis Host: ${redisContainer.host}, Port: ${redisContainer.getMappedPort(6379)}") // 디버깅 목적
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }
        }
    }

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Test
    fun `Given string value, when set in Redis, then should find the same value`() {
        val key = "testKey"
        val value = "testValue"

        println("Redis Host: ${redisContainer.host}, Port: ${redisContainer.getMappedPort(6379)}")

        // RedisTemplate을 사용하여 값을 설정하고 검색합니다.
        redisTemplate.opsForValue().set(key, value)
        val result = redisTemplate.opsForValue().get(key)

        assertEquals(value, result)
    }
}