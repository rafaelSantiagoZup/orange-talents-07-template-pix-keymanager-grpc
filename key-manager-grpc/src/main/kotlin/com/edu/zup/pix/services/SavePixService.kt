package com.edu.zup.pix.services

import com.edu.zup.KeyManagerGrpcReply
import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.TipoChave
import com.edu.zup.pix.repository.PixRepository
import com.edu.zup.pix.entidades.Cliente
import com.edu.zup.pix.entidades.Pix
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import java.util.*
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.ValidationException

@Validated
class SavePixService(
    @Valid
    val cliente: Cliente,
    val request: KeyManagerGrpcRequest,
    val responseObserver: StreamObserver<KeyManagerGrpcReply>?,
    val pixRepository: PixRepository
    ) {

    fun pixServices(){
        var pix = Pix(request.valorChave, request.tipoChave, cliente, request.tipoConta)
        if (request.tipoChave == TipoChave.chave_aleatoria) {
            pix.chave = UUID.randomUUID().toString()
        }
        if (checaExistenciaDeChavePix(pix)) return
        if (persistePix(pix)) return
        criaResponse(pix, responseObserver!!)
    }

    private fun criaResponse(
        pix: Pix,
        responseObserver: StreamObserver<KeyManagerGrpcReply>
    ) {
        val response =
            KeyManagerGrpcReply.newBuilder()
                .setIdentificadorCliente(request.identificadorCliente.toString())
                .setPixId(pix.id.toString())
                .build()
        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }

    @Transactional
    fun persistePix(@Valid pix: Pix): Boolean {
        try {
            pixRepository.save(pix)
        } catch (e: ConstraintViolationException) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Algum campo está errado ou vazio!")
                    .asRuntimeException()
            )
            return true
        } catch (e: ValidationException) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Algum campo está errado ou vazio!")
                    .asRuntimeException()
            )
            return true
        }
        return false
    }

    private fun checaExistenciaDeChavePix(@Valid pix: Pix): Boolean {
        if (pixRepository.existsByChave(pix.chave!!)) {
            responseObserver?.onError(
                Status.ALREADY_EXISTS
                    .withDescription("Já existe um cadastro para essa chave pix!")
                    .asRuntimeException()
            )
            return true
        }
        return false
    }
}
