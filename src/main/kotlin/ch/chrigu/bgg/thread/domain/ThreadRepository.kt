package ch.chrigu.bgg.thread.domain

import ch.chrigu.bgg.forum.domain.Forum
import reactor.core.publisher.Flux
import java.time.OffsetDateTime

interface ThreadRepository {
    fun findInForum(forum: Forum, lastUpdateSince: OffsetDateTime): Flux<Thread>
}
