package ch.chrigu.bgg.boardgame.infrastructure

import ch.chrigu.bgg.boardgame.domain.BoardGame
import ch.chrigu.bgg.boardgame.domain.BoardGameId
import ch.chrigu.bgg.boardgame.domain.BoardGameRepository
import ch.chrigu.bgg.boardgame.domain.CollectionStatus
import ch.chrigu.bgg.core.infrastructure.client.XmlBodyToFluxExtension.xmlBodyToFlux
import ch.chrigu.bgg.core.infrastructure.client.XmlValue
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

@Repository
class DefaultBoardGameRepository(private val webClient: WebClient) : BoardGameRepository {
    private val collectionUrl = "/collection?username={username}"

    override fun findInUserCollection(user: String): Flux<BoardGame> {
        return webClient.get().uri(collectionUrl, user).retrieve()
                .xmlBodyToFlux<CollectionItems>()
                .flatMap { it.item.toFlux() }
                .map { toBoardGame(it) }
    }

    private fun toBoardGame(item: CollectionItem): BoardGame {
        return BoardGame(item.getBoardGameId(), item.name.value, item.status.toBoardGameStatus())
    }
}

@JacksonXmlRootElement(localName = "items")
data class CollectionItems(val item: List<CollectionItem> = emptyList())

data class CollectionItem(@JacksonXmlProperty(isAttribute = true) val objectid: String,
                          val name: XmlValue,
                          val status: CollectionItemStatus) {
    @JsonIgnore
    fun getBoardGameId() = BoardGameId(objectid)
}

data class CollectionItemStatus(@JacksonXmlProperty(isAttribute = true) val own: String,
                                @JacksonXmlProperty(isAttribute = true) val want: String,
                                @JacksonXmlProperty(isAttribute = true) val wanttoplay: String,
                                @JacksonXmlProperty(isAttribute = true) val wanttobuy: String,
                                @JacksonXmlProperty(isAttribute = true) val wishlist: String,
                                @JacksonXmlProperty(isAttribute = true) val preordered: String) {
    @JsonIgnore
    fun toBoardGameStatus(): CollectionStatus {
        return if (own == "1") CollectionStatus.OWN
        else if (want == "1" || wanttoplay == "1" || wanttobuy == "1" || wishlist == "1" || preordered == "1") CollectionStatus.WANT
        else CollectionStatus.NONE
    }
}
