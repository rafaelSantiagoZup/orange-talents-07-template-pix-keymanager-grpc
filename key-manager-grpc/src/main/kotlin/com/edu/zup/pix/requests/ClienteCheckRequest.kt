package com.edu.zup.pix.requests

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class ClienteCheckRequest(
    @field:NotBlank
    val id:String,
    @field:NotBlank
    val nome:String,
    @field:NotBlank
    val cpf:String,
    val instituicao: InstituicaoRequest
)