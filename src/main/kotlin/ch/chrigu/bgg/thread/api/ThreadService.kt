package ch.chrigu.bgg.thread.api

import ch.chrigu.bgg.boardgame.domain.BoardGame
import ch.chrigu.bgg.boardgame.domain.BoardGameRepository
import ch.chrigu.bgg.forum.domain.ForumRepository
import ch.chrigu.bgg.thread.domain.Thread
import ch.chrigu.bgg.thread.domain.ThreadRepository
import ch.chrigu.bgg.user.domain.User
import ch.chrigu.bgg.user.domain.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.*

@Service
class ThreadService(private val boardGameRepository: BoardGameRepository, private val forumRepository: ForumRepository, private val threadRepository: ThreadRepository,
                    private val userRepository: UserRepository) {
    fun findNewestThreads(forUser: String, lastUpdateSince: Duration?): Flux<ThreadsPerBoardGame> {
        return userRepository.findById(forUser)
                .flatMap { updateLastRead(it).map { _ -> it } }
                .defaultIfEmpty(User(forUser, LocalDateTime.now().minusHours(1)))
                .flatMapMany { findNewestThreadsForUser(it, lastUpdateSince) }
    }

    private fun updateLastRead(user: User): Mono<User> {
        return user.updateLastRead().let { userRepository.save(it) }
    }

    private fun findNewestThreadsForUser(forUser: User, lastUpdateSince: Duration?): Flux<ThreadsPerBoardGame> {
        val since = if (lastUpdateSince == null)
            forUser.lastRead.atZone(ZoneId.systemDefault())
        else
            ZonedDateTime.now().minus(lastUpdateSince)
        return boardGameRepository.findInUserCollection(forUser.name)
                .filter { boardGame -> boardGame.isOfInterest() && !forUser.ignoreGames.contains(boardGame.id) }
                .checkpoint("Board games of interest")
                .flatMap { getThreadsForBoardGame(it, since) }
    }

    private fun getThreadsForBoardGame(boardGame: BoardGame, since: ZonedDateTime): Mono<ThreadsPerBoardGame> {
        return forumRepository.findByBoardGame(boardGame)
                .filter { it.numOfThreads > 0 && it.lastPostDate?.isAfter(since) ?: false }
                .checkpoint("Forums with recent threads")
                .flatMap { forum -> threadRepository.findInForum(forum, since) }
                .collectList()
                .filter { it.isNotEmpty() }
                .map { ThreadsPerBoardGame(boardGame, it) }
    }
}

data class ThreadsPerBoardGame(val boardGame: BoardGame, val threads: List<Thread>)
