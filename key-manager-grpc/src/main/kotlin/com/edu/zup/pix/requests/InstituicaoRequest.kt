package com.edu.zup.pix.requests

import com.edu.zup.pix.entidades.Instituicao
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class InstituicaoRequest(
    @field:NotBlank
    val nome:String?,
    @field:NotBlank
    val ispb:String?) {
    companion object{
        fun InstituicaoRequest.toModel(): Instituicao {
            return Instituicao(nome,ispb)
        }
    }
}