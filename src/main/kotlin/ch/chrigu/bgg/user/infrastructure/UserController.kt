package ch.chrigu.bgg.user.infrastructure

import ch.chrigu.bgg.boardgame.domain.BoardGameId
import ch.chrigu.bgg.user.domain.User
import ch.chrigu.bgg.user.api.CreateUserDto
import ch.chrigu.bgg.user.api.IgnoreGameDto
import ch.chrigu.bgg.user.api.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@RequestBody @Valid user: CreateUserDto): Mono<User> {
        return userService.create(user)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/ignore")
    fun ignoreGame(@PathVariable userId: String, @RequestBody @Valid game: IgnoreGameDto): Mono<User> {
        return userService.ignoreGame(userId, game)
    }

    @DeleteMapping("/{userId}/ignore/{boardGameId}")
    fun ignoreGame(@PathVariable userId: String, @PathVariable boardGameId: BoardGameId): Mono<User> {
        return userService.removeIgnoredGame(userId, boardGameId)
    }
}
