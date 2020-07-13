package ch.chrigu.bgg

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@TestConfiguration
class WebClientTestConfig {
    @Bean
    fun webClient(@Value("\${wiremock.server.port}") wiremockPort: String) = WebClient.builder()
            .baseUrl("http://localhost:${wiremockPort}")
            .build()
}
