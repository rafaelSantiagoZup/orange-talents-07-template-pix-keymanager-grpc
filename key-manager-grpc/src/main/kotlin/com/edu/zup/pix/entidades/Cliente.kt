package com.edu.zup.pix.entidades

import com.edu.zup.TipoConta
import io.micronaut.core.annotation.Introspected
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Introspected
@Embeddable
class Cliente(
    @field:NotBlank
        @field:Enumerated(EnumType.STRING)
        val tipo: TipoConta?,
    @Embedded
        val instituicao: Instituicao,
    @field:NotBlank
        val agencia:String?,
    @field:NotBlank
        val numero:String,
    @Embedded
        val titular: Titular
        ){
        //equals, hashcode e toString
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Cliente) return false

                if (tipo != other.tipo) return false
                if (instituicao != other.instituicao) return false
                if (agencia != other.agencia) return false
                if (numero != other.numero) return false
                if (titular != other.titular) return false

                return true
        }

        override fun hashCode(): Int {
                var result = tipo?.hashCode() ?: 0
                result = 31 * result + instituicao.hashCode()
                result = 31 * result + (agencia?.hashCode() ?: 0)
                result = 31 * result + numero.hashCode()
                result = 31 * result + titular.hashCode()
                return result
        }

        override fun toString(): String {
                return "Cliente(tipo=$tipo, instituicao=$instituicao, agencia=$agencia, numero='$numero', titular=$titular)"
        }

}