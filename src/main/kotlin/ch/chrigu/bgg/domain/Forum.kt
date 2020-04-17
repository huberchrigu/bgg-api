package ch.chrigu.bgg.domain

import java.time.OffsetDateTime

data class Forum(val id: String, val title: String, val numOfThreads: Int, val lastPostDate: OffsetDateTime?)
