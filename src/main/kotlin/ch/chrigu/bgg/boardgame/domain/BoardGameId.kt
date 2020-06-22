package ch.chrigu.bgg.boardgame.domain

import com.fasterxml.jackson.annotation.JsonValue

data class BoardGameId(@get:JsonValue val id: String) {
    override fun toString(): String {
        return id
    }
}
