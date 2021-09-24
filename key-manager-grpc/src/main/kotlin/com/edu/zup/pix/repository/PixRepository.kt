package com.edu.zup.pix.repository

import com.edu.zup.pix.entidades.Pix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface PixRepository:CrudRepository<Pix,UUID> {
    fun existsByChave(chave:String):Boolean
}