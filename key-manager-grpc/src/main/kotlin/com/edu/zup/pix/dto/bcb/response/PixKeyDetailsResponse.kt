package com.edu.zup.pix.dto.bcb.response

import com.edu.zup.pix.dto.bcb.enums.TiposDeChaveBCB
import com.edu.zup.pix.dto.bcb.request.ClienteBcbRequest
import com.edu.zup.pix.dto.bcb.request.ContaRequest
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
@JacksonXmlRootElement(localName = "PixKeyDetailsResponse")
data class PixKeyDetailsResponse(
    @Enumerated(EnumType.STRING)
    @field:NotNull
    val keyType: TiposDeChaveBCB?,
    @field:NotBlank
    val key:String?,
    @field:NotNull
    val bankAccount: ContaRequest?,
    @field:NotNull
    val owner: ClienteBcbRequest?,
    @field:NotNull
    val createdAt: LocalDateTime
)
