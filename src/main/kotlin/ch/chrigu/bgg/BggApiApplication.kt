package ch.chrigu.bgg

import ch.chrigu.bgg.core.infrastructure.client.BggApiProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(BggApiProperties::class)
class BggApiApplication

fun main(args: Array<String>) {
    runApplication<BggApiApplication>(*args)
}
