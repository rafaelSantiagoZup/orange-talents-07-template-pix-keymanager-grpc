package com.edu.zup.pix.entidades

import io.micronaut.core.annotation.Introspected
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Introspected
@Embeddable
class Instituicao(
    @field:NotBlank
    @Column(nullable = false)
    val nomeInstituicao:String?,
    @field:NotBlank
    @Column(nullable = false)
    val ispb:String?) {
}