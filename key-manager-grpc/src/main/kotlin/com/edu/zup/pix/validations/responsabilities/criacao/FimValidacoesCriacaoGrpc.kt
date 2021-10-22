package com.edu.zup.pix.validations.responsabilities.criacao

import com.edu.zup.KeyManagerGrpcRequest

class FimValidacoesCriacaoGrpc(proximo: ValidacoesCriacaoGrpc?, request: KeyManagerGrpcRequest?) : ValidacoesCriacaoGrpc(null, request) {
    override fun valida(): ValidacoesCriacaoGrpc? {
        return proximo?.valida()
    }
}