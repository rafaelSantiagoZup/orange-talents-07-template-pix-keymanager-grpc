package com.edu.zup.pix.validations

import com.edu.zup.ConsultaChaveGrpcRequest
import com.edu.zup.common.exceptions.GrpcValidationException
import io.grpc.Status

class ValidaConsulta() {
    fun peloPixId(pixId: ConsultaChaveGrpcRequest.FiltroPorPixId?) {
        if(pixId?.identificadorCliente == "" || pixId?.pixId == ""){
            throw GrpcValidationException("IdentificadorCliente e pixId são obrigatórios!",Status.INVALID_ARGUMENT)
        }
    }

    fun pelaChave(valorChave: String?) {
        if(valorChave?.length!! > 77) {
            throw GrpcValidationException("A chave deve possuir no máximo 77 caracteres",Status.INVALID_ARGUMENT)
        }
    }

}
