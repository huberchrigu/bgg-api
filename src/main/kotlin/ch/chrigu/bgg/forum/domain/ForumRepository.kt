package ch.chrigu.bgg.forum.domain

import ch.chrigu.bgg.boardgame.domain.BoardGame
import reactor.core.publisher.Flux

interface ForumRepository {
    fun findByBoardGame(boardGame: BoardGame): Flux<Forum>
}
