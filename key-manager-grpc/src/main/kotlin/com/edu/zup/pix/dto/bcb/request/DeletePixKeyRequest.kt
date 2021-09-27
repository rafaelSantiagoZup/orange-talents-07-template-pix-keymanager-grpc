package com.edu.zup.pix.dto.bcb.request

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
@JacksonXmlRootElement(localName = "DeletePixKeyRequest")
class DeletePixKeyRequest(
    @field:NotBlank
    val key:String?
){
    val participant:String = "60701190"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeletePixKeyRequest) return false

        if (key != other.key) return false
        if (participant != other.participant) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key?.hashCode() ?: 0
        result = 31 * result + participant.hashCode()
        return result
    }

}
