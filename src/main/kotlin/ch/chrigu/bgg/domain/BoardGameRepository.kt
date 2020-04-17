package ch.chrigu.bgg.domain

import reactor.core.publisher.Flux

interface BoardGameRepository {
    fun findInUserCollection(user: String): Flux<BoardGame>
}
