package com.edu.zup.pix.client

import com.edu.zup.pix.requests.ClienteCheckRequest
import com.edu.zup.pix.requests.ClienteRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:9091/api/v1/clientes")
@Header(name = "accept",value ="application/json")
interface ItauClient {

    @Get("/{clienteId}/contas{?tipo}")
    fun getClienteFromServer(@PathVariable clienteId:String,@QueryValue tipo:String): HttpResponse<ClienteRequest>

    @Get("/{clienteId}")
    fun checkClienteFromServer(@PathVariable clienteId:String): HttpResponse<ClienteCheckRequest>
}