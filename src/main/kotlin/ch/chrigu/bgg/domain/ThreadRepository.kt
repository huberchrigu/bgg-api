package ch.chrigu.bgg.domain

import reactor.core.publisher.Flux
import java.time.OffsetDateTime

interface ThreadRepository {
    fun findInForum(forum: Forum, lastUpdateSince: OffsetDateTime): Flux<Thread>
}
