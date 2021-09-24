package com.edu.zup.pix

import com.edu.zup.KeyManagerGrpcReply
import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.KeyManagerGrpcServiceGrpc
import com.edu.zup.TipoChave
import com.edu.zup.pix.client.ItauClient
import com.edu.zup.pix.repository.PixRepository
import com.edu.zup.pix.services.ItauService
import com.edu.zup.pix.services.SavePixService
import com.edu.zup.pix.services.ValidacoesService
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory


@Singleton
class KeyManagerGrpcEndpoint(@Inject val contaClient: ItauClient,
                             @Inject val pixRepository: PixRepository
): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(KeyManagerGrpcEndpoint::class.java)

    override fun cadastra(request: KeyManagerGrpcRequest?, responseObserver: StreamObserver<KeyManagerGrpcReply>?) {
        val validation = ValidacoesService(request, responseObserver)
        checaCondicoes(request, validation, responseObserver)
        chamaServices(request, responseObserver)
    }

    private fun chamaServices(
        request: KeyManagerGrpcRequest?,
        responseObserver: StreamObserver<KeyManagerGrpcReply>?
    ) {
        try {
            val cliente = ItauService(request!!,contaClient).buscaCliente()
            SavePixService(cliente, request, responseObserver,pixRepository).pixServices()
        } catch (e: HttpClientResponseException) {
            logger.info(e.message)
            responseObserver?.onError(
                Status.NOT_FOUND
                    .withDescription("Não foi encontrado um cliente com esse id")
                    .asRuntimeException()
            )
            return
        }
    }

    private fun checaCondicoes(
        request: KeyManagerGrpcRequest?,
        validation: ValidacoesService,
        responseObserver: StreamObserver<KeyManagerGrpcReply>?
    ) {
        when {
            request?.tipoChave == TipoChave.CPF -> validation.checaFormatoCpf(request.valorChave, responseObserver)
            request?.tipoChave == TipoChave.telefone_celular -> validation.checaFormatoCelular(
                request.valorChave,
                responseObserver
            )
            request?.tipoChave == TipoChave.email -> validation.checaFormatoEmail(request.valorChave, responseObserver)
            else -> validation.checaChaveAleatoria(request!!.valorChave, responseObserver)
        }
    }
}