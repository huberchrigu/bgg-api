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
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class ThreadService(private val boardGameRepository: BoardGameRepository, private val forumRepository: ForumRepository, private val threadRepository: ThreadRepository,
                    private val userRepository: UserRepository) {
    fun findNewestThreads(forUser: String, lastUpdateSince: Duration?): Flux<ThreadsPerBoardGame> {
        return userRepository.findById(forUser)
                .flatMapMany { findNewestThreadsForUser(it, lastUpdateSince) }
                .switchIfEmpty { findNewestThreadsForUser(forUser, lastUpdateSince) }
    }

    private fun findNewestThreadsForUser(forUser: User, lastUpdateSince: Duration?): Flux<ThreadsPerBoardGame> {
        val since: OffsetDateTime = if (lastUpdateSince == null)
            forUser.lastRead.atOffset(ZoneOffset.UTC)
        else
            getStartTimeOrDefault(lastUpdateSince)
        return forUser.updateLastRead().let { userRepository.save(it) }
                .flatMapMany { boardGameRepository.findInUserCollection(it.name) }
                .filter { boardGame -> boardGame.isOfInterest() && !forUser.ignoreGames.contains(boardGame.id) }
                .checkpoint("Board games of interest")
                .flatMap { getThreadsForBoardGame(it, since) }
    }

    private fun findNewestThreadsForUser(forUser: String, lastUpdateSince: Duration?): Flux<ThreadsPerBoardGame> {
        val since = getStartTimeOrDefault(lastUpdateSince)
        return boardGameRepository.findInUserCollection(forUser)
                .filter { boardGame -> boardGame.isOfInterest() }
                .checkpoint("Board games of interest")
                .flatMap { getThreadsForBoardGame(it, since) }
    }

    private fun getStartTimeOrDefault(lastUpdateSince: Duration?): OffsetDateTime {
        val default = Duration.ofHours(1)
        return OffsetDateTime.now().minus(lastUpdateSince ?: default)
    }

    private fun getThreadsForBoardGame(boardGame: BoardGame, since: OffsetDateTime): Mono<ThreadsPerBoardGame> {
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
