package com.edu.zup.pix.dto.bcb.request

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
@JacksonXmlRootElement(localName = "DeletePixKeyRequest")
data class DeletePixKeyRequest(
    @field:NotBlank
    val key:String?
){
    val participant:String = "60701190"
}
