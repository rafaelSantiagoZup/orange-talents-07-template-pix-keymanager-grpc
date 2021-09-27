package com.edu.zup.pix

import com.edu.zup.*
import com.edu.zup.pix.client.BcbClient
import com.edu.zup.pix.client.ItauClient
import com.edu.zup.pix.repository.PixRepository
import com.edu.zup.pix.services.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*


@Singleton
class KeyManagerGrpcEndpoint(@Inject val contaClient: ItauClient,
                             @Inject val pixRepository: PixRepository,
                             @Inject val itauClient: ItauClient,
                             @Inject val bcbClient: BcbClient
): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    override fun cadastra(request: KeyManagerGrpcRequest?, responseObserver: StreamObserver<KeyManagerGrpcReply>?) {
        val validation = ValidacoesCriacaoService(request, responseObserver)
        checaCondicoes(request, validation, responseObserver)
        chamaServices(request, responseObserver)
    }

    override fun exclui(request: ExcuiChaveGrpcRequest?, responseObserver: StreamObserver<ExcuiChaveGrpcResponse>?) {
        val validation = ValidacoesExclusaoService(request!!,itauClient,pixRepository,responseObserver)
        if(validation.checaPropriedade()){
            val deletePixService = DeletePixService(pixRepository,bcbClient,request,responseObserver)
            deletePixService.deletaPix()
        }
    }

    private fun chamaServices(
        request: KeyManagerGrpcRequest?,
        responseObserver: StreamObserver<KeyManagerGrpcReply>?
    ) {
        try {
            val cliente = ItauService(request!!,contaClient).buscaCliente()
            SavePixService(cliente, request, responseObserver,pixRepository).pixServices(bcbClient)
        } catch (e: HttpClientResponseException) {
            responseObserver?.onError(
                Status.NOT_FOUND
                    .withDescription("Não foi encontrado um cliente com esse id")
                    .asRuntimeException()
            )
            return
        } catch (e:StatusRuntimeException){
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
        validation: ValidacoesCriacaoService,
        responseObserver: StreamObserver<KeyManagerGrpcReply>?
    ) {
        when {
            request?.tipoChave == TipoChave.CPF -> validation.checaFormatoCpf(request.valorChave, responseObserver)
            request?.tipoChave == TipoChave.telefone_celular -> validation.checaFormatoCelular(
                request.valorChave,
                responseObserver
            )
            request?.tipoChave == TipoChave.email -> validation.checaFormatoEmail()
            else -> validation.checaChaveAleatoria()
        }
    }
}