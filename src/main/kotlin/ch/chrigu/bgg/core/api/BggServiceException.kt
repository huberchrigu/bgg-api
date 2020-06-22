package ch.chrigu.bgg.core.api

import java.lang.RuntimeException

open class BggServiceException(message: String) : RuntimeException(message)
