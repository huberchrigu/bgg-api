package ch.chrigu.bgg

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BggApiApplication

fun main(args: Array<String>) {
    runApplication<BggApiApplication>(*args)
}
