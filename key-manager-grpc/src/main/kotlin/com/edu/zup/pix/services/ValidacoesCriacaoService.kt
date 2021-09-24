package com.edu.zup.pix.services

import com.edu.zup.ExcuiChaveGrpcRequest
import com.edu.zup.ExcuiChaveGrpcResponse
import com.edu.zup.KeyManagerGrpcReply
import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.pix.client.ItauClient
import com.edu.zup.pix.repository.PixRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.util.*

class ValidacoesCriacaoService(val request: KeyManagerGrpcRequest?, val responseObserver: StreamObserver<KeyManagerGrpcReply>?) {
    fun checaFormatoCpf(cpf:String,responseObserver: StreamObserver<KeyManagerGrpcReply>?){
        if(!cpf.matches("^[0-9]{11}$".toRegex())){
            responseObserver!!.onError(Status.INVALID_ARGUMENT.withDescription("O CPF DEVE SER NO FORMATO 11111111111").asRuntimeException())
            return
        }
    }
    fun checaFormatoCelular(celular:String,responseObserver: StreamObserver<KeyManagerGrpcReply>?){
        if(!celular.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())){
            responseObserver!!.onError(Status.INVALID_ARGUMENT.withDescription("Formato de telefone inválido").asRuntimeException())
        }
        return
    }
    fun checaFormatoEmail(){
        if((request?.valorChave?.contains("@") == false) && request?.valorChave?.isNotEmpty()!!){
            responseObserver!!.onError(Status.INVALID_ARGUMENT.withDescription("Formato de email inválido").asRuntimeException())
        }
        return
    }
    fun checaChaveAleatoria(){
//        if(request?.valorChave.equals("")){
//            responseObserver!!.onError(Status.INVALID_ARGUMENT.withDescription("Quando tipo for chave aleatória, o valor da chave não deve ser preenchido").asRuntimeException())
//        }
//        return
    }

}