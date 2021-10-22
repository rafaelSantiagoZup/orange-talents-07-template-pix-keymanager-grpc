package com.edu.zup.pix.client

import com.edu.zup.pix.dto.bcb.response.DeletePixResponse
import com.edu.zup.pix.dto.bcb.request.CreatePixKeyRequest
import com.edu.zup.pix.dto.bcb.request.DeletePixKeyRequest
import com.edu.zup.pix.dto.bcb.response.CreatePixResponse
import com.edu.zup.pix.dto.bcb.response.PixKeyDetailsResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import javax.validation.Valid

@Client("\${client.contas.bcb}")
@Headers(
    Header(name="accept",value = "application/xml"),
    Header(name="Content-Type",value = "application/xml"))
interface BcbClient {

    @Post(produces = [MediaType.APPLICATION_XML],consumes = [MediaType.APPLICATION_XML])
    fun notificaBCB(@Body createPixKeyRequest: CreatePixKeyRequest):HttpResponse<CreatePixResponse>

    @Delete(value = "/{keys}",produces = [MediaType.APPLICATION_XML],consumes = [MediaType.APPLICATION_XML])
    fun deletaChaveBCB(@PathVariable keys:String, @Valid @Body request: DeletePixKeyRequest):HttpResponse<DeletePixResponse>

    @Get(value = "/{keys}",produces = [MediaType.APPLICATION_XML],consumes = [MediaType.APPLICATION_XML])
    fun buscaChavePix(@PathVariable keys:String):HttpResponse<PixKeyDetailsResponse>
}