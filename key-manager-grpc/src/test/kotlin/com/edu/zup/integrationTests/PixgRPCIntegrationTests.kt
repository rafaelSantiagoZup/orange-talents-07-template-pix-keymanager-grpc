package com.edu.zup.integrationTests

import com.edu.zup.*
import com.edu.zup.pix.client.BcbClient
import com.edu.zup.pix.client.ItauClient
import com.edu.zup.pix.dto.bcb.enums.TipoDeDocumento
import com.edu.zup.pix.dto.bcb.enums.TiposDeChaveBCB
import com.edu.zup.pix.dto.bcb.enums.TiposDeContaBCB
import com.edu.zup.pix.dto.bcb.request.ClienteBcbRequest
import com.edu.zup.pix.dto.bcb.request.ContaRequest
import com.edu.zup.pix.dto.bcb.request.CreatePixKeyRequest
import com.edu.zup.pix.dto.bcb.request.DeletePixKeyRequest
import com.edu.zup.pix.dto.bcb.response.CreatePixResponse
import com.edu.zup.pix.dto.bcb.response.DeletePixResponse
import com.edu.zup.pix.entidades.Pix
import com.edu.zup.pix.repository.PixRepository
import com.edu.zup.pix.dto.itau.request.ClienteCheckRequest
import com.edu.zup.pix.dto.itau.request.ClienteRequest
import com.edu.zup.pix.dto.itau.request.ClienteRequest.Companion.toModel
import com.edu.zup.pix.dto.itau.request.InstituicaoRequest
import com.edu.zup.pix.dto.itau.request.TitularRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*

@MicronautTest(transactional = false)
class PixgRPCIntegrationTests {
    @Inject
    lateinit var grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub
    @Inject
    lateinit var itauClient: ItauClient
    @Inject
    lateinit var pixRepository: PixRepository
    @Inject
    lateinit var bcbClient: BcbClient

    lateinit var payload:KeyManagerGrpcRequest
    val tempo = LocalDateTime.now()
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
        Mockito.`when`((bcbClient.notificaBCB(CreatePixKeyRequest(TiposDeChaveBCB.CPF,"6628726061", ContaRequest("60701190","0001","212233",TiposDeContaBCB.CACC),
            ClienteBcbRequest(TipoDeDocumento.NATURAL_PERSON,"Alberto Tavares","06628726061")
        )))).thenReturn((HttpResponse.ok(CreatePixResponse(TiposDeChaveBCB.CPF,"6628726061", ContaRequest("60701190","0001","212233",TiposDeContaBCB.CACC),
            ClienteBcbRequest(TipoDeDocumento.NATURAL_PERSON,"Alberto Tavares","06628726061"), tempo))))
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
    @Test
    internal fun `deve excluir chave pix`(){
        val identificador = "0d1bb194-3c52-4e67-8c35-a93c0af9286p"
        val cliente = ClienteRequest(
            TipoConta.CONTA_CORRENTE,
            InstituicaoRequest("ITAÚ UNIBANCO S.A.","60701190"),
            "0001",
            "212233",
            TitularRequest(identificador,"Alberto Tavares","06628726061")
        ).toModel()
        val pix = Pix("11232143254",TipoChave.CPF,cliente,TipoConta.CONTA_CORRENTE)
        pixRepository.save(pix)

        Mockito.`when`((itauClient.checkClienteFromServer(identificador)))
            .thenReturn(
                HttpResponse.ok(
                    ClienteCheckRequest(
                        identificador,
                        "Alberto Tavares",
                        "06628726061",
                        InstituicaoRequest("ITAÚ UNIBANCO S.A.","60701190")
                    )
                )
            )
        val id = pix.id
        val bcbDeleteRequest = DeletePixKeyRequest(key = pix.chave)

        Mockito.`when`((bcbClient.deletaChaveBCB(pix.chave.toString(),bcbDeleteRequest)))
            .thenReturn(HttpResponse.ok(DeletePixResponse(pix.chave.toString(),"60701190", LocalDateTime.now())))

        val requestDelete = ExcuiChaveGrpcRequest
            .newBuilder()
            .setIdentificadorCliente(identificador)
            .setPixId(id.toString())
            .build()

        val response = grpcClient.exclui(requestDelete)

        val resposeFromDatabase = pixRepository.findById(id)

        with(response){
            assertEquals(id.toString(),response.pixId)
            assertEquals(requestDelete.identificadorCliente,response.identificadorCliente)
            assertEquals(true,resposeFromDatabase.isEmpty)
            assertEquals(response.pixId,requestDelete.pixId)
            assertEquals(response.identificadorCliente,requestDelete.identificadorCliente)
        }
    }
    @Test
    fun `nao deve excluir chave pix quando não houver sucesso no contato com bcb`(){
        val identificador = "0d1bb194-3c52-4e67-8c35-a93c0af9286p"
        val cliente = ClienteRequest(
            TipoConta.CONTA_CORRENTE,
            InstituicaoRequest("ITAÚ UNIBANCO S.A.","60701190"),
            "0001",
            "212233",
            TitularRequest(identificador,"Alberto Tavares","06628726061")
        ).toModel()
        val pix = Pix("11232143254",TipoChave.CPF,cliente,TipoConta.CONTA_CORRENTE)
        pixRepository.save(pix)

        Mockito.`when`((itauClient.checkClienteFromServer(identificador)))
            .thenReturn(
                HttpResponse.ok(
                    ClienteCheckRequest(
                        identificador,
                        "Alberto Tavares",
                        "06628726061",
                        InstituicaoRequest("ITAÚ UNIBANCO S.A.","60701190")
                    )
                )
            )
        val id = pix.id
        val bcbDeleteRequest = DeletePixKeyRequest(key = pix.chave)

        Mockito.`when`((bcbClient.deletaChaveBCB(pix.chave.toString(),bcbDeleteRequest)))
            .thenThrow(HttpClientResponseException::class.java)

        val requestDelete = ExcuiChaveGrpcRequest
            .newBuilder()
            .setIdentificadorCliente(identificador)
            .setPixId(id.toString())
            .build()

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.exclui(requestDelete)
        }

        val resposeFromDatabase = pixRepository.findById(id)

        with(error){
            assertEquals(Status.NOT_FOUND.code,error.status.code)
            assertEquals("Não foi possível deletar essa chave pix",error.status.description)
            assertTrue(resposeFromDatabase.isPresent)
        }
    }
    @Test
    fun `nao deve criar chave pix quando não houver sucesso no contato com bcb`(){
        val identificador = "0d1bb194-3c52-4e67-8c35-a93c0af9286p"
        val cliente = ClienteRequest(
            TipoConta.CONTA_CORRENTE,
            InstituicaoRequest("ITAÚ UNIBANCO S.A.","60701190"),
            "0001",
            "212233",
            TitularRequest(identificador,"Alberto Tavares","06628726061")
        ).toModel()
        Mockito.`when`((itauClient.checkClienteFromServer(identificador)))
            .thenReturn(
                HttpResponse.ok(
                    ClienteCheckRequest(
                        identificador,
                        "Alberto Tavares",
                        "06628726061",
                        InstituicaoRequest("ITAÚ UNIBANCO S.A.","60701190")
                    )
                )
            )
        Mockito.`when`((bcbClient.notificaBCB(CreatePixKeyRequest(TiposDeChaveBCB.CPF,"06628726061", ContaRequest("60701190","0001","212233",TiposDeContaBCB.CACC),
            ClienteBcbRequest(TipoDeDocumento.NATURAL_PERSON,"Alberto Tavares","06628726061")
        )))).thenThrow(HttpClientResponseException::class.java)

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(payload)
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code,error.status.code)
            assertEquals("A chave pix nao pode ser cadastrada!",error.status.description)
        }
    }

    //Beans Mockados
    @MockBean(ItauClient::class)
    fun itauClientBean(): ItauClient {
        return Mockito.mock(ItauClient::class.java, Mockito.RETURNS_DEEP_STUBS)
    }
    @MockBean(BcbClient::class)
    fun bcbClientBean(): BcbClient {
        return Mockito.mock(BcbClient::class.java, Mockito.RETURNS_DEEP_STUBS)
    }
}