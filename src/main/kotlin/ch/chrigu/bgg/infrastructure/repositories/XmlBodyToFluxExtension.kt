package ch.chrigu.bgg.infrastructure.repositories

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

/**
 * Utility function because [XmlMapper] is not yet supported in reactive stack.
 */
object XmlBodyToFluxExtension {
    inline fun <reified T> WebClient.ResponseSpec.xmlBodyToFlux(): Flux<T> = bodyToMono(String::class.java)
            .map { it.replace("\t", "").replace("\n", "").replace("\r", "") }
            .flatMap { throwExceptionIfError(it) }
            .map { xmlMapper.readValue(it, T::class.java) }
            .flux()

    val xmlMapper: XmlMapper = XmlMapper.builder()
            .defaultUseWrapper(false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .addModule(KotlinModule())
            .build()

    /**
     * In case of a client error, BBG returns a OK, but with a body like:
     * ```
     * <errors>
     *     <error>
     *         <message>Invalid username specified</message>
     *     </error>
     * </errors>
     * ```
     */
    fun throwExceptionIfError(body: String): Mono<String> {
        return body.toMono().map { xmlMapper.readValue(it, BggErrors::class.java) }
                .flatMap { BggErrorException(it).toMono<BggErrorException>() }
                .map { body }
                .onErrorReturn({ it !is BggErrorException }, body)
    }

    data class BggErrors(val error: List<BggError>)
    data class BggError(val message: String)

    class BggErrorException(errors: BggErrors) : ResponseStatusException(HttpStatus.BAD_REQUEST, errors.error[0].message)
}
