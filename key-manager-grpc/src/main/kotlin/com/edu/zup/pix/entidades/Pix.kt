package com.edu.zup.pix.entidades

import com.edu.zup.TipoChave
import com.edu.zup.TipoConta
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Introspected
class Pix(
    @field:NotBlank
    @Column(unique = true,nullable = false,length = 40)
    var chave:String?,
    @Column(nullable = false)
    val tipoChave: TipoChave?,
    @Embedded
    var cliente: Cliente?,
    val tipoConta: TipoConta?
    ){
    @Id
    @Column(length = 40)
    val id: UUID? = UUID.randomUUID()
    val timestamp:LocalDateTime = LocalDateTime.now()
}