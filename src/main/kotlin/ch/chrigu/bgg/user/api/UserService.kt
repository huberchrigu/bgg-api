package ch.chrigu.bgg.user.api

import ch.chrigu.bgg.boardgame.domain.BoardGameId
import ch.chrigu.bgg.user.domain.User
import ch.chrigu.bgg.user.domain.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Duration
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@Service
class UserService(private val userRepository: UserRepository) {
    fun create(user: CreateUserDto): Mono<User> {
        return userRepository.save(user.toUser())
    }

    fun ignoreGame(userId: String, game: IgnoreGameDto): Mono<User> {
        val user = getUser(userId)
        return user.map { it.ignoreGame(game.boardGameId!!) }.flatMap { userRepository.save(it) }
    }

    fun removeIgnoredGame(userId: String, boardGameId: BoardGameId): Mono<User> {
        val user = getUser(userId)
        return user.map { it.removeIgnoredGame(boardGameId) }.flatMap { userRepository.save(it) }
    }

    private fun getUser(userId: String) = userRepository.findById(userId)
            .switchIfEmpty { Mono.error(UserNotFoundException(userId)) }
}

data class IgnoreGameDto(@field:NotNull val boardGameId: BoardGameId?)

data class CreateUserDto(@field:NotNull val name: String?, val unreadSince: Duration = Duration.ofHours(1)) {
    fun toUser() = User(name!!, LocalDateTime.now().minus(unreadSince))
}
