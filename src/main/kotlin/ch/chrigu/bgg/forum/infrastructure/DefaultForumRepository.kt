package ch.chrigu.bgg.forum.infrastructure

import ch.chrigu.bgg.boardgame.domain.BoardGame
import ch.chrigu.bgg.core.infrastructure.client.XmlBodyToFluxExtension.xmlBodyToFlux
import ch.chrigu.bgg.forum.domain.Forum
import ch.chrigu.bgg.forum.domain.ForumRepository
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Repository
class DefaultForumRepository(private val webClient: WebClient) : ForumRepository {
    private val forumlistUrl = "/forumlist?id={id}&type=thing"

    override fun findByBoardGame(boardGame: BoardGame): Flux<Forum> {
        return webClient.get().uri(forumlistUrl, boardGame.id).retrieve()
                .xmlBodyToFlux<ForumListResponse>()
                .flatMap { it.forum.toFlux() }.map { toForum(it) }
    }

    private fun toForum(item: ForumItem): Forum {
        return Forum(item.id, item.title, item.numthreads, item.getLastPostDate())
    }
}

data class ForumListResponse(val forum: List<ForumItem>)
data class ForumItem(val id: String, val title: String, val numthreads: Int, val lastpostdate: Date?) {
    @JsonIgnore
    fun getLastPostDate(): ZonedDateTime? {
        return if (lastpostdate == null)
            null
        else
            ZonedDateTime.ofInstant(lastpostdate.toInstant(), ZoneId.systemDefault())
    }
}
