package ch.chrigu.bgg.core.infrastructure.client

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bgg.api")
class BggApiProperties {
    var url = "https://api.geekdo.com/xmlapi2"
}
