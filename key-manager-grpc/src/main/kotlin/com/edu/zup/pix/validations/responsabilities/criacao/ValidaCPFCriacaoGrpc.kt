package com.edu.zup.pix.validations.responsabilities.criacao

import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.TipoChave
import com.edu.zup.common.exceptions.GrpcValidationException
import io.grpc.Status

class ValidaCPFCriacaoGrpc(proximo: ValidacoesCriacaoGrpc, request: KeyManagerGrpcRequest?) : ValidacoesCriacaoGrpc(proximo, request) {

    override fun valida(): ValidacoesCriacaoGrpc? {
        if(request?.tipoChave?.equals(TipoChave.CPF) == true){
            if((request?.valorChave?.matches("^[0-9]{11}$".toRegex())) == false){
                throw GrpcValidationException("Formato de cpf invalido", Status.INVALID_ARGUMENT)
            }
        }
        return proximo!!.valida()
    }
}