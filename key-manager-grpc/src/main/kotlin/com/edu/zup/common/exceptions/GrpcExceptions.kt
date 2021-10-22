package com.edu.zup.common.exceptions

import com.edu.zup.KeyManagerGrpcReply
import com.edu.zup.KeyManagerGrpcReplyOrBuilder
import com.edu.zup.KeyManagerGrpcRequest
import com.google.protobuf.AbstractMessage
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.validation.ConstraintViolationException

abstract class GrpcExceptions(
    val messageString: String?,
    val status:Status?
):Exception(messageString) {
}