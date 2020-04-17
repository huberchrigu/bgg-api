package ch.chrigu.bgg.infrastructure.web;

import ch.chrigu.bgg.service.ThreadService;
import ch.chrigu.bgg.service.ThreadsPerBoardGame;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@RestController
@RequestMapping("/java/threads")
public class JavaThreadController {
    private final ThreadService threadService;

    public JavaThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @GetMapping("/newest")
    Flux<ThreadsPerBoardGame> getThreads(@RequestParam String user, @RequestParam(defaultValue = "PT1H") Duration since) {
        return threadService.findNewestThreads(user, since);
    }

    @GetMapping("/newest/obj")
    Flux<ThreadsPerBoardGame> getThreads(@Valid ThreadRequest params) {
        return threadService.findNewestThreads(params.user, params.since);
    }

    static class ThreadRequest {
        @NotNull
        private final String user;
        @NotNull
        private final Duration since;

        public ThreadRequest(String user, Duration since) {
            this.user = user;
            this.since = since == null ? Duration.ofHours(1) : since;
        }
    }
}

