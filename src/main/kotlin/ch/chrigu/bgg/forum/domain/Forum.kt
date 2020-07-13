package ch.chrigu.bgg.forum.domain

import java.time.ZonedDateTime

data class Forum(val id: String, val title: String, val numOfThreads: Int, val lastPostDate: ZonedDateTime?)
