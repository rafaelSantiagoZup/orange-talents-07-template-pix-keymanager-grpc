package com.edu.zup.pix.entidades

import io.micronaut.core.annotation.Introspected
import javax.annotation.MatchesPattern
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Introspected
@Embeddable
class Titular(
    @field:NotBlank
    @Column(nullable = false)
    val idTitular:String?,
    @field:NotBlank
    @Column(nullable = false)
    val nome:String?,
    @field:MatchesPattern("^[0-9]{11}$")
    @field:NotBlank
    @Column(nullable = false)
    val cpf:String?
)