package ch.chrigu.bgg.core.domain

object DomainCheck {
    fun precondition(state: Boolean, errorMessage: () -> String) {
        if (!state) {
            throw BggDomainException(errorMessage())
        }
    }
}
