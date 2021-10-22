package com.edu.zup.pix

import com.edu.zup.*
import com.edu.zup.pix.client.BcbClient
import com.edu.zup.pix.client.ItauClient
import com.edu.zup.pix.repository.PixRepository
import com.edu.zup.pix.services.*
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*


@Singleton
class KeyManagerGrpcEndpoint(@Inject val itauClient: ItauClient,
                             @Inject val pixRepository: PixRepository,
                             @Inject val bcbClient: BcbClient
): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    override fun cadastra(request: KeyManagerGrpcRequest?, responseObserver: StreamObserver<KeyManagerGrpcReply>?) {
        SavePixService(request!!, responseObserver,pixRepository,bcbClient,itauClient).executaServices()
    }
    override fun exclui(request: ExcuiChaveGrpcRequest?, responseObserver: StreamObserver<ExcuiChaveGrpcResponse>?) {
        val validation = ValidacoesExclusaoService(itauClient,pixRepository,responseObserver)
        if(validation.checaPropriedade(UUID.fromString(request!!.pixId))){
            DeletePixService(pixRepository,bcbClient,request,responseObserver).deletaPix()
        }
    }

    override fun consulta(
        request: ConsultaChaveGrpcRequest?,
        responseObserver: StreamObserver<ConsultaChaveGrpcResponse>?
    ) {
        ConsultaService(request,responseObserver,bcbClient,pixRepository).consulta()
    }
}