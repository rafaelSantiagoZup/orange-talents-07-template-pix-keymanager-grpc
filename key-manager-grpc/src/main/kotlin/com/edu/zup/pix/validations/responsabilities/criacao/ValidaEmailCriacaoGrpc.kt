package com.edu.zup.pix.validations.responsabilities.criacao

import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.TipoChave
import com.edu.zup.common.exceptions.GrpcValidationException
import io.grpc.Status

class ValidaEmailCriacaoGrpc(proximo: ValidacoesCriacaoGrpc, request: KeyManagerGrpcRequest?) : ValidacoesCriacaoGrpc(proximo, request) {
    override fun valida(): ValidacoesCriacaoGrpc? {
        if(request?.tipoChave?.equals(TipoChave.email) == true){
            if(request?.valorChave?.matches("/^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+\\.([a-z]+)?\$/i".toRegex())==false){
                throw GrpcValidationException("Formato de email inválido",Status.INVALID_ARGUMENT)
            }
        }
        return proximo!!.valida()
    }
}