package ch.chrigu.bgg.service

import ch.chrigu.bgg.domain.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.OffsetDateTime

@Service
class ThreadService(private val boardGameRepository: BoardGameRepository, private val forumRepository: ForumRepository, private val threadRepository: ThreadRepository) {
    fun findNewestThreads(forUser: String, lastUpdateSince: Duration): Flux<ThreadsPerBoardGame> {
        val since = OffsetDateTime.now().minus(lastUpdateSince)
        return boardGameRepository.findInUserCollection(forUser)
                .filter { boardGame -> boardGame.isOfInterest() }
                .checkpoint("Board games of interest")
                .flatMap { getThreadsForBoardGame(it, since) }
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
