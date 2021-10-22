package com.edu.zup.pix.services

import com.edu.zup.KeyManagerGrpcReply
import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.common.exceptions.GrpcValidationException
import com.edu.zup.pix.validations.responsabilities.criacao.*
import io.grpc.stub.StreamObserver

class ValidacoesCriacaoService(
    val request: KeyManagerGrpcRequest?,
    val responseObserver: StreamObserver<KeyManagerGrpcReply>?) {

    fun checaCondicoes():Boolean{
        try{

            val validacoes = ValidaCPFCriacaoGrpc(ValidaEmailCriacaoGrpc(ValidaCelularCriacaoGrpc(
                        ValidaChaveAleatoriaCriacaoGrpc(
                            FimValidacoesCriacaoGrpc(null, request),
                        request),
                        request),
                    request),
                request)
            validacoes.valida()
            return true
        }catch (e: GrpcValidationException){
            responseObserver?.
            onError(e.status?.withDescription(e.messageString)?.
            asRuntimeException())

            return false
        }
    }
}