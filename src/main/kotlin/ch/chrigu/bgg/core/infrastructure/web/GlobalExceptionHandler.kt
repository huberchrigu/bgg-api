package ch.chrigu.bgg.core.infrastructure.web

import ch.chrigu.bgg.core.domain.BggDomainException
import ch.chrigu.bgg.core.api.BggServiceException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ResponseStatusException
import java.lang.RuntimeException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(TypeMismatchException::class)
    fun handleTypeMismatchException(e: TypeMismatchException): HttpStatus {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value '${e.value}'", e)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(e: WebExchangeBindException): HttpStatus {
        throw object : WebExchangeBindException(e.methodParameter!!, e.bindingResult) {
            override val message = "${fieldError?.field} has invalid value '${fieldError?.rejectedValue}'"
        }
    }

    @ExceptionHandler(BggServiceException::class, BggDomainException::class)
    fun handleBggServiceException(e: RuntimeException): HttpStatus = throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
}
