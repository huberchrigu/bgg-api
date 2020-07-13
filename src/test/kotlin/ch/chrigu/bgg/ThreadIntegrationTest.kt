package ch.chrigu.bgg

import ch.chrigu.bgg.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("mock-web-client")
@Import(WebClientTestConfig::class)
class ThreadIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Test
    fun `should get latest posts since fixed time span`() {
        webTestClient.get().uri("/threads/newest?since=PT30M&user=user")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$[0].boardGame.name").isEqualTo("The 7th Continent")
                .jsonPath("$[1].boardGame.name").isEqualTo("Arkham Horror: The Card Game")
                .jsonPath("$[2].boardGame.name").isEqualTo("Twilight Imperium (Fourth Edition)")
    }

    @Test
    fun `should get latest posts since last read`() {
        webTestClient.post().uri("/users").body(Mono.just(mapOf("name" to "user")), Map::class.java)
                .exchange()
                .expectStatus().isCreated

        val initialReadTime = getReadTime().block()

        webTestClient.get().uri("/threads/newest?user=user")
                .exchange()
                .expectStatus().isOk

        val newReadTime = getReadTime().block()
        assertThat(newReadTime).isAfter(initialReadTime)
    }

    private fun getReadTime(): Mono<LocalDateTime> {
        return reactiveMongoTemplate.findOne(Query(where("_id").`is`("user")), User::class.java, "user").map { it.lastRead }
    }
}
