package com.edu.zup.pix.dto.bcb.request

import com.edu.zup.pix.dto.bcb.enums.TipoDeDocumento
import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class ClienteBcbRequest (
    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: TipoDeDocumento?,
    @field:NotBlank
    val name:String?,
    @field:NotBlank
    val taxIdNumber:String?
)
