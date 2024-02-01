package com.project.wakuwaku.mongo

import org.bson.Document
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.annotation.DirtiesContext

//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataMongoTest
@ExtendWith(SpringExtension::class)
class MongoDbEmbeddedTest @Autowired constructor(
        val mongoTemplate: MongoTemplate
) {

    @BeforeEach
    fun setUp() {
        mongoTemplate.dropCollection("collection")
    }

    @AfterEach
    fun tearDown() {
        mongoTemplate.dropCollection("collection")
    }

    @Test
    fun `내부 MongoDb 확인 테스트`() {
        // given
        val objectToSave = Document().append("key", "value")

        // when
        mongoTemplate.save(objectToSave, "collection")

        // then
        assertThat(mongoTemplate.findAll(Document::class.java, "collection"))
                .extracting("key")
                .containsOnly("value")
    }

    @Test
    fun `테스트 시 내부 MongoDb 독립 여부 테스트`() {

        // then
        assertThat(mongoTemplate.findAll(Document::class.java, "collection"))
                .extracting("key")
                .isEmpty()
    }
}
