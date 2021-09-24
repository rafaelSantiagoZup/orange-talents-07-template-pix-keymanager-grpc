package com.edu.zup.pix.services

import com.edu.zup.KeyManagerGrpcReply
import com.edu.zup.KeyManagerGrpcRequest
import io.grpc.Status
import io.grpc.stub.StreamObserver

class ValidacoesService(val request: KeyManagerGrpcRequest?, val responseObserver: StreamObserver<KeyManagerGrpcReply>?) {
    fun checaFormatoCpf(cpf:String,responseObserver: StreamObserver<KeyManagerGrpcReply>?){
        if(!cpf.matches("^[0-9]{11}$".toRegex())){
            responseObserver!!.onError(Status.INVALID_ARGUMENT.withDescription("CPF inválido").asRuntimeException())
            return
        }
    }
    fun checaFormatoCelular(celular:String,responseObserver: StreamObserver<KeyManagerGrpcReply>?){
        if(!celular.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())){
            responseObserver!!.onError(Status.INVALID_ARGUMENT.withDescription("Formato de telefone inválido").asRuntimeException())
        }
        return
    }
    fun checaFormatoEmail(email:String,responseObserver: StreamObserver<KeyManagerGrpcReply>?){
        if(!email.contains("@") && email != null){
            responseObserver!!.onError(Status.INVALID_ARGUMENT.withDescription("Formato de email inválido").asRuntimeException())
        }
        return
    }
    fun checaChaveAleatoria(chave:String,responseObserver: StreamObserver<KeyManagerGrpcReply>?){
        if(chave!=null || chave!=""){
            responseObserver!!.onError(Status.INVALID_ARGUMENT.withDescription("Quando tipo for chave aleatória, o valor da chave não deve ser preenchido").asRuntimeException())
        }
        return
    }

}