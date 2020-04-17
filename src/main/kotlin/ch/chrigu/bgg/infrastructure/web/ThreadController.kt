package ch.chrigu.bgg.infrastructure.web

import ch.chrigu.bgg.service.ThreadService
import ch.chrigu.bgg.service.ThreadsPerBoardGame
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import javax.validation.Valid
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/threads")
class ThreadController(private val threadService: ThreadService) {
    @GetMapping("/newest")
    fun getThreads(@RequestParam user: String, @RequestParam(defaultValue = "PT1H") since: Duration): Flux<ThreadsPerBoardGame> {
        return threadService.findNewestThreads(user, since)
    }

    @PostMapping("/newest")
    fun addThread(@Valid body: ThreadRequest): Mono<ThreadRequest> {
        return body.toMono()
    }

    @GetMapping("/newest/obj")
    fun getThreads(@Valid params: Mono<ThreadRequest>): Flux<ThreadsPerBoardGame> = params.flatMapMany { threadService.findNewestThreads(it.user!!, it.since) }

    data class ThreadRequest(@field:NotNull val user: String?, val since: Duration = Duration.ofHours(1))
}
