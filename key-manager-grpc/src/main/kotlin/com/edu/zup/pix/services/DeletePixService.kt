package com.edu.zup.pix.services

import com.edu.zup.ExcuiChaveGrpcRequest
import com.edu.zup.ExcuiChaveGrpcResponse
import com.edu.zup.pix.client.BcbClient
import com.edu.zup.pix.dto.bcb.request.DeletePixKeyRequest
import com.edu.zup.pix.repository.PixRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.util.*

class DeletePixService(
    val pixRepository: PixRepository,
    val bcbClient: BcbClient,
    val request: ExcuiChaveGrpcRequest,
    val responseObserver: StreamObserver<ExcuiChaveGrpcResponse>?
) {
    @Throws(StatusRuntimeException::class)
    fun deletaPix(){
        val response = excuiChaveGrpcResponse()
        try{
            val registroFromDatabase = pixRepository.findById(UUID.fromString(request.pixId))
            if(registroFromDatabase.isEmpty){
                responseObserver?.onError(
                    Status.NOT_FOUND
                        .withDescription("Não foi encontrado um cliente com esse id")
                        .asRuntimeException()
                )
                return
            }
            val deleteRequest  = DeletePixKeyRequest(registroFromDatabase.get().chave)

            val responseBcbClient = bcbClient.deletaChaveBCB(registroFromDatabase.get().chave.toString(),deleteRequest)

            if(responseBcbClient.status.code.equals(200)){
                pixRepository.deleteById(UUID.fromString(request.pixId))

                responseObserver?.onNext(response)
                responseObserver?.onCompleted()
                return
            }
        }catch (e: StatusRuntimeException){
            responseObserver?.onError(
                Status.NOT_FOUND
                    .withDescription("Não foi encontrado um cliente com esse id")
                    .asRuntimeException()
            )
            return
        }catch (e:HttpClientResponseException){
            responseObserver?.onError(
                Status.NOT_FOUND
                    .withDescription("Não foi possível deletar essa chave pix")
                    .asRuntimeException()
            )
            return
        }


    }

    private fun excuiChaveGrpcResponse() =
        ExcuiChaveGrpcResponse.newBuilder().setIdentificadorCliente(request.identificadorCliente)
            .setPixId(this.request.pixId).build()
}