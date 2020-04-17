package ch.chrigu.bgg.infrastructure.repositories

import ch.chrigu.bgg.domain.Forum
import ch.chrigu.bgg.domain.Post
import ch.chrigu.bgg.domain.Thread
import ch.chrigu.bgg.domain.ThreadRepository
import ch.chrigu.bgg.infrastructure.repositories.XmlBodyToFluxExtension.xmlBodyToFlux
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@Repository
class DefaultThreadRepository(private val webClient: WebClient) : ThreadRepository {
    private val forumUrl = "/forum?id={id}"
    private val threadUrl = "/thread?id={id}&minarticledate={minDate}"
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun findInForum(forum: Forum, lastUpdateSince: OffsetDateTime): Flux<Thread> {
        return webClient.get().uri(forumUrl, forum.id).retrieve()
                .xmlBodyToFlux<ForumResponse>()
                .flatMap { it.threads!!.toFlux() }
                .filter { it.getLastPostDate().isAfter(lastUpdateSince) }
                .flatMap { findInThread(it, lastUpdateSince) }
    }

    private fun findInThread(threadItem: ThreadItem, since: OffsetDateTime): Flux<Thread> {
        return webClient.get().uri(threadUrl, threadItem.id, since.withOffsetSameInstant(ZoneOffset.ofHours(-5)).format(dateTimeFormatter)).retrieve()
                .xmlBodyToFlux<ThreadResponse>()
                .map { toThread(it) }
    }

    private fun toThread(threadResponse: ThreadResponse): Thread {
        return Thread(threadResponse.articles!!.map { toPost(it) }, threadResponse.subject)
    }

    private fun toPost(articleItem: ArticleItem): Post {
        return Post(articleItem.body.value, articleItem.username)
    }

}

class ForumResponse {
    @JacksonXmlElementWrapper(localName = "threads")
    @JacksonXmlProperty(localName = "thread")
    var threads: List<ThreadItem>? = null
        get() = field ?: emptyList()
}

data class ThreadItem(val id: String, val lastpostdate: Date) {
    @JsonIgnore
    fun getLastPostDate(): OffsetDateTime = OffsetDateTime.ofInstant(lastpostdate.toInstant(), ZoneId.systemDefault())
}

data class ThreadResponse(val subject: String) {
    @JacksonXmlElementWrapper(localName = "articles")
    @JacksonXmlProperty(localName = "article")
    val articles: List<ArticleItem>? = null
        get() = field ?: emptyList()
}

data class ArticleItem(val username: String, val editdate: Date, val body: XmlValue)
