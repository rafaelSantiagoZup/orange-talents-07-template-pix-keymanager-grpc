package com.edu.zup.pix.validations.responsabilities.criacao

import com.edu.zup.KeyManagerGrpcRequest

abstract class ValidacoesCriacaoGrpc(protected val proximo: ValidacoesCriacaoGrpc?, request: KeyManagerGrpcRequest?) {
    val request = request

    abstract fun valida(): ValidacoesCriacaoGrpc?
}