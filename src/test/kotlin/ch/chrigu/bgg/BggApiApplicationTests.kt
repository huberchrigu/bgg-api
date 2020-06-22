package ch.chrigu.bgg

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@SpringBootTest
@AutoConfigureWebTestClient
class BggApiApplicationTests {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `should create user, allow adding and removing board game to ignore list`() {
        webTestClient.post().uri("/users").body(Mono.just(mapOf("name" to "user")), Map::class.java)
                .exchange()
                .expectStatus().isCreated

        webTestClient.post().uri("/users/user/ignore").body(Mono.just(mapOf("boardGameId" to "boardGameId")), Map::class.java)
                .exchange()
                .expectStatus().isCreated
                .expectBody().jsonPath("$.name").isEqualTo("user")
                .jsonPath("$.ignoreGames[0]").isEqualTo("boardGameId")


        webTestClient.delete().uri("/users/user/ignore/boardGameId")
                .exchange()
                .expectStatus().isOk
                .expectBody().jsonPath("$.ignoreGames").isEmpty
    }

    @Test
    fun `should not allow removing a board game from ignore list that is not there`() {
        webTestClient.post().uri("/users").body(Mono.just(mapOf("name" to "user2")), Map::class.java)
                .exchange()
                .expectStatus().isCreated

        webTestClient.delete().uri("/users/user2/ignore/bgId")
                .exchange()
                .expectStatus().isBadRequest
                .expectBody().jsonPath("$.message").isEqualTo("Game with ID bgId is not in the ignore list")
    }
}
