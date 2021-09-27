package com.edu.zup.pix.dto.bcb.response

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
@JacksonXmlRootElement(localName = "DeletePixKeyRequest")
data class DeletePixResponse(
    @field:NotBlank
    val key:String?,
    @field:NotBlank
    val participant:String?,
    @field:NotNull
    val deletedAt:LocalDateTime?
)
