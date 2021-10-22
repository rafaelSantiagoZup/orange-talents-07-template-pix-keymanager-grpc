package com.edu.zup.pix.validations.responsabilities.criacao

import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.TipoChave
import com.edu.zup.common.exceptions.GrpcValidationException
import io.grpc.Status

class ValidaCelularCriacaoGrpc(proximo: ValidacoesCriacaoGrpc, request: KeyManagerGrpcRequest?) : ValidacoesCriacaoGrpc(proximo, request) {
    override fun valida(): ValidacoesCriacaoGrpc? {
        if(request?.tipoChave?.equals(TipoChave.telefone_celular) == true){
            if(request?.valorChave?.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())==false){
                throw GrpcValidationException("Formato de telefone inválido",Status.INVALID_ARGUMENT)
            }
        }
        return proximo!!.valida()
    }
}