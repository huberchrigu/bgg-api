package ch.chrigu.bgg.boardgame.domain

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Is not the board game entity itself, rather a board game in the collection of a certain player.
 */
data class BoardGame(val id: BoardGameId, val name: String, val collectionStatus: CollectionStatus) {
    @JsonIgnore
    fun isOfInterest(): Boolean {
        return collectionStatus == CollectionStatus.OWN || collectionStatus == CollectionStatus.WANT
    }
}

enum class CollectionStatus { OWN, WANT, NONE }
