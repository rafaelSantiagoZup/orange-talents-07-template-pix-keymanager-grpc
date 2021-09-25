package com.edu.zup.unityTests

import com.edu.zup.*
import com.edu.zup.pix.client.ItauClient
import com.edu.zup.pix.requests.ClienteRequest
import com.edu.zup.pix.requests.InstituicaoRequest
import com.edu.zup.pix.requests.TitularRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.Assert.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

@MicronautTest
class PixgRPCUnityTests {
    @Inject
    lateinit var grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub
    @Inject
    lateinit var itauClient: ItauClient

    @Test
    internal fun `deve retornar erro quando recebe cpf com formato errado`(){
        val request =  KeyManagerGrpcRequest
            .newBuilder()
            .setIdentificadorCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoChave(TipoChave.CPF)
            .setValorChave("123")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(request)
        }
        println(error.toString())
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("O CPF DEVE SER NO FORMATO 11111111111",status.description)
        }
    }
    @Test
    internal fun `deve retornar erro quando recebe email com formato errado`(){
        val request =  KeyManagerGrpcRequest
            .newBuilder()
            .setIdentificadorCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoChave(TipoChave.email)
            .setValorChave("123")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(request)
        }
        println(error.toString())
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("Formato de email inválido",status.description)
        }
    }
    @Test
    internal fun `deve retornar erro quando recebe telefone com formato errado`(){
        val request =  KeyManagerGrpcRequest
            .newBuilder()
            .setIdentificadorCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoChave(TipoChave.telefone_celular)
            .setValorChave("123")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(request)
        }
        println(error.toString())
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("Formato de telefone inválido",status.description)
        }
    }
    @Test
    internal fun `deve retornar chave aleatória`() {
        val request =  KeyManagerGrpcRequest
            .newBuilder()
            .setIdentificadorCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoChave(TipoChave.chave_aleatoria)
            .setValorChave("123")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        `when`((itauClient.getClienteFromServer(request.identificadorCliente,request.tipoConta.name)))
            .thenReturn(
                HttpResponse.ok(ClienteRequest(
                TipoConta.CONTA_CORRENTE,
                InstituicaoRequest("ITAÚ UNIBANCO S.A.","60701190"),
                "0001",
                "212233",
                TitularRequest(request.identificadorCliente,"Alberto Tavares","06628726061")
            )))
        val response = grpcClient.cadastra(request)
        with(response){
            assertTrue(pixId.matches("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}".toRegex()))
            assertEquals(request.identificadorCliente.toString(),identificadorCliente.toString())
        }
    }
    @Test
    internal fun `deve retornar erro quando recebe erro do client`(){
        val request =  KeyManagerGrpcRequest
            .newBuilder()
            .setIdentificadorCliente("0d1bb194-3c52-4e67-8c35-a93c0a")
            .setTipoChave(TipoChave.CPF)
            .setValorChave("12312343234")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        `when`((itauClient.getClienteFromServer(request.identificadorCliente,request.tipoConta.name)))
            .thenThrow(HttpClientResponseException::class.java)
        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(request)
        }
        println(error.toString())
        with(error){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("Não foi encontrado um cliente com esse id",status.description)
        }
    }
    @Test
    internal fun `deve retornar erro quando cliente não existir`(){
        val request = ExcuiChaveGrpcRequest
            .newBuilder()
            .setIdentificadorCliente("0d1bb194-3c52-4e67-8c35-a93c0af9286p")
            .setPixId("ab56adf4-d641-4402-9365-6fb2ed0459ef")
            .build()
        `when`((itauClient.checkClienteFromServer(request.identificadorCliente)))
            .thenThrow(HttpClientResponseException::class.java)
        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.exclui(request)
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("Cliente não encontrado!",status.description)
        }
    }
    @MockBean(ItauClient::class)
    fun itauClientBean():ItauClient{
       return mock(ItauClient::class.java,Mockito.RETURNS_DEEP_STUBS)
    }
    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub{
            return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}