package com.edu.zup.pix.dto.bcb.request

import com.edu.zup.pix.dto.bcb.enums.TiposDeContaBCB
import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class ContaRequest(
    var participant:String?,
    @field:NotBlank
    val branch:String?,
    @field:NotBlank
    val accountNumber:String?,
    @Enumerated(EnumType.STRING)
    @field:NotNull
    val accountType: TiposDeContaBCB?
){
}
