package com.edu.zup.pix.services

import com.edu.zup.ExcuiChaveGrpcRequest
import com.edu.zup.ExcuiChaveGrpcResponse
import com.edu.zup.pix.client.ItauClient
import com.edu.zup.pix.repository.PixRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.util.*

class ValidacoesExclusaoService(val client: ItauClient,
                                val pixRepository: PixRepository,
                                val responseObserver: StreamObserver<ExcuiChaveGrpcResponse>?) {
    fun checaPropriedade(pixId:UUID):Boolean{

        try{
            val databaseResponse = pixRepository.findById(pixId)
            if(databaseResponse.isEmpty){
                responseObserver!!.onError(
                    Status.NOT_FOUND.
                withDescription("Chave Pix não encontrada no sistema").
                asRuntimeException())
                return false
            }
            else return true
        }catch (e: HttpClientResponseException){
            responseObserver!!.onError(
                Status.NOT_FOUND.
            withDescription("Cliente não encontrado!").
            asRuntimeException()
            )
            return false
        }
    }
}