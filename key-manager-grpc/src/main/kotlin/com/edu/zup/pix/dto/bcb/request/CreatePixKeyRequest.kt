package com.edu.zup.pix.dto.bcb.request

import com.edu.zup.pix.dto.bcb.enums.TiposDeChaveBCB
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Introspected
@JacksonXmlRootElement(localName = "CreatePixKeyRequest")
data class CreatePixKeyRequest(
    @Enumerated(EnumType.STRING)
    val keyType: TiposDeChaveBCB?,
    val key:String?,
    val bankAccount: ContaRequest?,
    val owner: ClienteBcbRequest?
)
