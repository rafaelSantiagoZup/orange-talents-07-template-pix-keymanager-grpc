package com.edu.zup.pix.services

import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.pix.client.ItauClient
import com.edu.zup.pix.entidades.Cliente
import com.edu.zup.pix.dto.itau.request.ClienteRequest.Companion.toModel

class ItauService(val request: KeyManagerGrpcRequest,val contaClient: ItauClient) {

    fun buscaCliente(): Cliente {
        return contaClient.
                getClienteFromServer(request.identificadorCliente,request.tipoConta.name)
                .body().toModel()
    }
}