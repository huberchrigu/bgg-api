package ch.chrigu.bgg.core.infrastructure.client

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.function.client.*

@Configuration
@Profile("!mock-web-client")
class WebClientConfig(private val bggApiProperties: BggApiProperties) {

    @Bean
    fun webClient() = WebClient.builder()
            .baseUrl(bggApiProperties.url)
            .build()
}
