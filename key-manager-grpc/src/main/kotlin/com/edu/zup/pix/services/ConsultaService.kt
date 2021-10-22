package com.edu.zup.pix.services

import com.edu.zup.ConsultaChaveGrpcRequest
import com.edu.zup.ConsultaChaveGrpcResponse
import com.edu.zup.common.exceptions.GrpcValidationException
import com.edu.zup.pix.client.BcbClient
import com.edu.zup.pix.entidades.Pix
import com.edu.zup.pix.repository.PixRepository
import com.edu.zup.pix.validations.ValidaConsulta
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.time.ZoneOffset
import java.util.*

class ConsultaService(
    val request: ConsultaChaveGrpcRequest?,
    val responseObserver: StreamObserver<ConsultaChaveGrpcResponse>?,
    val bcbClient: BcbClient,
    val pixRepository: PixRepository
) {
    fun consulta() {
        try{
            defineTipoPesquisa()
        }catch (e:GrpcValidationException){
            responseObserver?.onError(e.status?.asRuntimeException())
        }
    }
    fun defineTipoPesquisa(){
        when(request?.filtroCase?.name){
             "PIXID"->buscaPeloPixId()
            else->buscaPelaChave()
        }
    }

    private fun buscaPeloPixId() {
        ValidaConsulta().peloPixId(request?.pixId)
        if(!checaPropriedade(UUID.fromString(request?.pixId?.pixId))) return

        val databaseResponse = pixRepository.findById(UUID.fromString(request?.pixId?.pixId))
        if(databaseResponse.isEmpty){
            responseObserver?.onError(Status.NOT_FOUND.withDescription("Não foi encontrado uma conta vinculada à essa chave pix").asRuntimeException())
            return
        }
        val retorno = responseBuilders(databaseResponse.get())?.build()

        responseObserver?.onNext(retorno)
        responseObserver?.onCompleted()
    }

    private fun responseBuilders(databaseResponse: Pix): ConsultaChaveGrpcResponse.Builder? {
        val cliente = databaseResponse.cliente
        val instituicao = cliente?.instituicao
        val titular = cliente?.titular
        val instant = databaseResponse.timestamp.toInstant(ZoneOffset.UTC)
        val criadaEm = Timestamp.newBuilder().setSeconds(instant.epochSecond).setNanos(instant.nano).build()

        val conta = ConsultaChaveGrpcResponse
            .newBuilder().chaveBuilder
            .contaBuilder
            .setAgencia(cliente?.agencia)
            .setInstituicao(instituicao?.nomeInstituicao)
            .setCpfDoTitular(titular?.cpf)
            .setNomeDoTitular(titular?.nome)
            .setNumeroDaConta(cliente?.numero)
            .setTipoConta(cliente?.tipo)
            .build()

        val chave = ConsultaChaveGrpcResponse.newBuilder().chaveBuilder
            .setTipoChave(databaseResponse.tipoChave)
            .setChave(databaseResponse.chave)
            .setCriadaEm(criadaEm)
            .setConta(conta)
            .build()

        val response = ConsultaChaveGrpcResponse.newBuilder()
            .setIdentificadorCliente(titular?.idTitular)
            .setPixId(databaseResponse.id.toString())
            .setChave(chave)
        return response
    }

    private fun buscaPelaChave() {
        ValidaConsulta().pelaChave(request?.valorChave)

    }

    fun checaPropriedade(pixId: UUID):Boolean{
        try{
            val databaseResponse = pixRepository.findById(pixId)
            if(databaseResponse.isEmpty){
                responseObserver!!.onError(
                    Status.NOT_FOUND.
                    withDescription("Chave Pix não encontrada no sistema").
                    asRuntimeException())
                return false
            }
            else {
                return databaseResponse.get().cliente?.titular?.idTitular == request?.pixId?.identificadorCliente
            }
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
