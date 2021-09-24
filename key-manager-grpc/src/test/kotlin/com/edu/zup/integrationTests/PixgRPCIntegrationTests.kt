package com.edu.zup.integrationTests

import com.edu.zup.KeyManagerGrpcRequest
import com.edu.zup.KeyManagerGrpcServiceGrpc
import com.edu.zup.TipoChave
import com.edu.zup.TipoConta
import com.edu.zup.pix.client.ItauClient
import com.edu.zup.pix.repository.PixRepository
import com.edu.zup.pix.requests.ClienteRequest
import com.edu.zup.pix.requests.InstituicaoRequest
import com.edu.zup.pix.requests.TitularRequest
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*

@MicronautTest(transactional = false)
class PixgRPCIntegrationTests {
    @Inject
    lateinit var grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub
    @Inject
    lateinit var itauClient: ItauClient
    @Inject
    lateinit var pixRepository: PixRepository

    lateinit var payload:KeyManagerGrpcRequest
    @BeforeEach
    internal open fun setup(){
        payload =  KeyManagerGrpcRequest
            .newBuilder()
            .setIdentificadorCliente("0d1bb194-3c52-4e67-8c35-a93c0af9284f")
            .setTipoChave(TipoChave.CPF)
            .setValorChave("06628726061")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        Mockito.`when`((itauClient.getClienteFromServer(payload.identificadorCliente, payload.tipoConta.name)))
            .thenReturn(
                HttpResponse.ok(
                    ClienteRequest(
                    TipoConta.CONTA_CORRENTE,
                    InstituicaoRequest("ITAÚ UNIBANCO S.A.","60701190"),
                    "0001",
                    "212233",
                    TitularRequest(payload.identificadorCliente,"Alberto Tavares","06628726061")
                )
                ))
    }
    @AfterEach
    internal fun tearDown(){
        pixRepository.deleteAll()
    }

    @Test
    internal fun `deve retornar chave pix salva`(){
        var response = grpcClient.cadastra(payload)
        var responseFromServer = pixRepository.findById(UUID.fromString(response.pixId))
        with(response){
            assertEquals(response.pixId.toString(),responseFromServer.get().id.toString())
        }
    }
    @Test
    internal fun `deve retornar os dados que foram salvos`(){
        var response = grpcClient.cadastra(payload)
        var responseFromServer = pixRepository.findById(UUID.fromString(response.pixId))
        with(response){
            assertEquals(response.pixId.toString(),responseFromServer.get().id.toString())
            assertEquals(payload.tipoConta.name,responseFromServer.get().tipoConta?.name)
            assertEquals(payload.valorChave.toString(),responseFromServer.get().chave.toString())
            assertEquals(payload.tipoChave.name,responseFromServer.get().tipoChave?.name)
        }
    }

    @MockBean(ItauClient::class)
    fun itauClientBean(): ItauClient {
        return Mockito.mock(ItauClient::class.java, Mockito.RETURNS_DEEP_STUBS)
    }
}