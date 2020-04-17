package ch.chrigu.bgg.infrastructure.repositories

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

/**
 * Workaround because `<attr>value</attr>` cannot be mapped to a class like `data class C(val attr: String)`
 */
class XmlValue {
    @JacksonXmlText
    lateinit var value: String
}
