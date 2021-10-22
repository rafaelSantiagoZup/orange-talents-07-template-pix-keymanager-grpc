package com.edu.zup.pix.validations.responsabilities.criacao

import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.TipoChave
import com.edu.zup.common.exceptions.GrpcValidationException
import io.grpc.Status

class ValidaChaveAleatoriaCriacaoGrpc(proximo: ValidacoesCriacaoGrpc, request: KeyManagerGrpcRequest?) : ValidacoesCriacaoGrpc(proximo, request) {
    override fun valida(): ValidacoesCriacaoGrpc? {
        if(request?.tipoChave == TipoChave.chave_aleatoria && request?.valorChave?.isNotEmpty() == true){
            throw GrpcValidationException("Quando tipo for chave aleatória, o valor da chave não deve ser preenchido",Status.INVALID_ARGUMENT)
        }
        return proximo!!.valida()
    }
}