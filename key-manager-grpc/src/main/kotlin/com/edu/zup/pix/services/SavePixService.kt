package com.edu.zup.pix.services

import com.edu.zup.KeyManagerGrpcReply
import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.TipoChave
import com.edu.zup.TipoConta
import com.edu.zup.pix.client.BcbClient
import com.edu.zup.pix.client.ItauClient
import com.edu.zup.pix.dto.bcb.enums.TipoDeDocumento
import com.edu.zup.pix.dto.bcb.enums.TiposDeChaveBCB
import com.edu.zup.pix.dto.bcb.enums.TiposDeContaBCB
import com.edu.zup.pix.dto.bcb.request.ClienteBcbRequest
import com.edu.zup.pix.dto.bcb.request.ContaRequest
import com.edu.zup.pix.dto.bcb.request.CreatePixKeyRequest
import com.edu.zup.pix.entidades.Cliente
import com.edu.zup.pix.entidades.Pix
import com.edu.zup.pix.repository.PixRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.ValidationException

@Validated
class SavePixService(
    @Valid
    val request: KeyManagerGrpcRequest,
    val responseObserver: StreamObserver<KeyManagerGrpcReply>?,
    val pixRepository: PixRepository,
    val bcbClient: BcbClient,
    val itauClient: ItauClient
) {
    fun executaServices() {
        if(!ValidacoesCriacaoService(request,responseObserver).checaCondicoes()){
            return
        }
        try {
            val cliente = ItauService(request!!,itauClient).buscaCliente()
            pixServices(cliente)
        } catch (e: HttpClientResponseException) {
            responseObserver?.onError(
                Status.NOT_FOUND
                    .withDescription("Não foi encontrado um cliente com esse id")
                    .asRuntimeException()
            )
            return
        } catch (e: StatusRuntimeException){
            responseObserver?.onError(
                Status.NOT_FOUND
                    .withDescription("Não foi encontrado um cliente com esse id")
                    .asRuntimeException()
            )
            return
        }
    }
    fun pixServices(cliente: Cliente){
        var pix = Pix(request.valorChave, request.tipoChave, cliente, request.tipoConta)
        var pixRequestBCB = criaPixRequestBCB(pix)
        try {
            val pixResponseBCB = bcbClient.notificaBCB(pixRequestBCB).body()
            if (request.tipoChave == TipoChave.chave_aleatoria) {
                pix.chave = pixResponseBCB.key
            }
            if (checaExistenciaDeChavePix(pix)) return
            if (persistePix(pix)) return
            criaResponse(pix, responseObserver!!)
        }catch (e:HttpClientResponseException){
            responseObserver?.onError(
                Status.NOT_FOUND
                    .withDescription("A chave pix nao pode ser cadastrada!")
                    .asRuntimeException()
            )
        }
    }
    private fun criaPixRequestBCB(pix: Pix): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            converteTipoChaveFromPixItauRequest(pix.tipoChave!!),
            pix.chave,
            ContaRequest(
                        pix.cliente?.instituicao?.ispb,
                        pix.cliente?.agencia,
                        pix.cliente?.numero,
                        converteTipoContaBCBfromTipoContaItau(pix.tipoConta!!)),
            ClienteBcbRequest(
                TipoDeDocumento.NATURAL_PERSON!!,
                pix.cliente?.titular?.nome,
                pix.cliente?.titular?.cpf)
        )
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
    fun converteTipoChaveFromPixItauRequest(pixItauRequest:TipoChave): TiposDeChaveBCB {
        when{
            pixItauRequest == TipoChave.CPF -> return TiposDeChaveBCB.CPF
            pixItauRequest == TipoChave.email -> return TiposDeChaveBCB.EMAIL
            pixItauRequest == TipoChave.telefone_celular -> return TiposDeChaveBCB.PHONE
            else -> return TiposDeChaveBCB.RANDOM

        }
    }
    fun converteTipoContaBCBfromTipoContaItau(tipo: TipoConta): TiposDeContaBCB {
        when{
            tipo == TipoConta.CONTA_CORRENTE -> return TiposDeContaBCB.CACC
            else -> return TiposDeContaBCB.SVGS
        }
    }
}
