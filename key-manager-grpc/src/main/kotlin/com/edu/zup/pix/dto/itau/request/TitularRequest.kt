package com.edu.zup.pix.dto.itau.request

import com.edu.zup.pix.entidades.Titular
import io.micronaut.core.annotation.Introspected
import javax.annotation.MatchesPattern
import javax.persistence.Column
import javax.validation.constraints.NotBlank

@Introspected
class TitularRequest(
    @field:NotBlank
    @Column(nullable = false)
    val id:String?,
    @field:NotBlank
    @Column(nullable = false)
    val nome:String?,
    @field:MatchesPattern("^[0-9]{11}$")
    @field:NotBlank
    @Column(nullable = false)
    val cpf:String?
){
    companion object{
        fun TitularRequest.toModel(): Titular {
            return Titular(id,nome,cpf)
        }
    }
}