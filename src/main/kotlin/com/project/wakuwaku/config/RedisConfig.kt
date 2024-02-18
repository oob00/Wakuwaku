package com.project.wakuwaku.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisConfig {
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> {
        val redisTemplate: RedisTemplate<String, Any> = RedisTemplate()

        redisTemplate.connectionFactory = connectionFactory

        // Key와 Value Serializer 설정
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = JdkSerializationRedisSerializer()

        // HashKey와 HashValue Serializer 설정
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = JdkSerializationRedisSerializer()
        return redisTemplate
    }
}