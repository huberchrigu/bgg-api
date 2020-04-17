package ch.chrigu.bgg.domain

import reactor.core.publisher.Flux

interface ForumRepository {
    fun findByBoardGame(boardGame: BoardGame): Flux<Forum>
}
