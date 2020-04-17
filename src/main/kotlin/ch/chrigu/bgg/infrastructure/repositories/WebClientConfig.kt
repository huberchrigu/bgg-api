package ch.chrigu.bgg.infrastructure.repositories

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.*

@Configuration
class WebClientConfig {
    private val baseUrl = "https://api.geekdo.com/xmlapi2"

    @Bean
    fun webClient() = WebClient.builder()
            .baseUrl(baseUrl)
            .build()
}
