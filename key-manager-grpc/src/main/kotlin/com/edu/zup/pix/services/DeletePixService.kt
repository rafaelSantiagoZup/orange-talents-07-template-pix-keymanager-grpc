package com.edu.zup.pix.services

import com.edu.zup.pix.repository.PixRepository
import java.util.*

class DeletePixService(val pixRepository: PixRepository) {
    fun deletaPix(pixId:UUID){
        pixRepository.deleteById(pixId)
    }
}