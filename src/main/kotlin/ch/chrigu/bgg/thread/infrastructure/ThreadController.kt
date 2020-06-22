package ch.chrigu.bgg.thread.infrastructure

import ch.chrigu.bgg.thread.api.ThreadService
import ch.chrigu.bgg.thread.api.ThreadsPerBoardGame
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import javax.validation.Valid
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/threads")
class ThreadController(private val threadService: ThreadService) {
    @GetMapping("/newest")
    fun getThreads(@RequestParam user: String, since: Duration?): Flux<ThreadsPerBoardGame> {
        return threadService.findNewestThreads(user, since)
    }

    @GetMapping("/newest/obj")
    fun getThreads(@Valid params: Mono<ThreadRequest>): Flux<ThreadsPerBoardGame> = params.flatMapMany { threadService.findNewestThreads(it.user!!, it.since) }

    data class ThreadRequest(@field:NotNull val user: String?, val since: Duration = Duration.ofHours(1))
}
