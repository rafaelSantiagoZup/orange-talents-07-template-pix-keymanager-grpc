package com.edu.zup.pix.requests

import com.edu.zup.TipoConta
import com.edu.zup.pix.entidades.Cliente
import com.edu.zup.pix.requests.InstituicaoRequest.Companion.toModel
import com.edu.zup.pix.requests.TitularRequest.Companion.toModel
import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.ManyToOne
import javax.validation.constraints.NotBlank

@Introspected
data class ClienteRequest(
        @field:NotBlank
        @field:Enumerated(EnumType.STRING)
        val tipo:TipoConta?,
        @ManyToOne
        val instituicao: InstituicaoRequest,
        @field:NotBlank
        val agencia:String?,
        @field:NotBlank
        val numero:String,
        @ManyToOne
        val titular: TitularRequest
        ) {
        companion object{
                fun ClienteRequest.toModel(): Cliente {
                        return Cliente(tipo,instituicao.toModel(),agencia,numero,titular.toModel())
                }
        }
}