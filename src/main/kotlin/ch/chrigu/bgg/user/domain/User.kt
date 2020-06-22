package ch.chrigu.bgg.user.domain

import ch.chrigu.bgg.boardgame.domain.BoardGameId
import ch.chrigu.bgg.core.domain.DomainCheck
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class User(@field:Id val name: String, val lastRead: LocalDateTime = LocalDateTime.now(), val ignoreGames: List<BoardGameId> = emptyList()) {
    fun updateLastRead() = update(LocalDateTime.now())

    fun ignoreGame(boardGameId: BoardGameId): User {
        DomainCheck.precondition(!ignoreGames.contains(boardGameId)) { "Game with ID $boardGameId is already ignored" }
        return update(lastRead, ignoreGames + boardGameId)
    }

    fun removeIgnoredGame(boardGameId: BoardGameId): User {
        DomainCheck.precondition(ignoreGames.contains(boardGameId)) { "Game with ID $boardGameId is not in the ignore list" }
        return update(lastRead, ignoreGames - boardGameId)
    }

    private fun update(newLastRead: LocalDateTime = lastRead, newIgnoreGames: List<BoardGameId> = ignoreGames) = User(name, newLastRead, newIgnoreGames)
}
