package ch.chrigu.bgg.domain

import com.fasterxml.jackson.annotation.JsonIgnore

data class BoardGame(@field:JsonIgnore val id: String, val name: String, val collectionStatus: CollectionStatus) {
    @JsonIgnore
    fun isOfInterest(): Boolean {
        return collectionStatus == CollectionStatus.OWN || collectionStatus == CollectionStatus.WANT
    }
}

enum class CollectionStatus { OWN, WANT, NONE }
