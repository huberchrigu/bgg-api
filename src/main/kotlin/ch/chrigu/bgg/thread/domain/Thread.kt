package ch.chrigu.bgg.thread.domain

data class Thread(val posts: List<Post>, val subject: String)
data class Post(val message: String, val user: String)