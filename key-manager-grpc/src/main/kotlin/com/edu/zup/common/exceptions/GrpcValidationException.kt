package com.edu.zup.common.exceptions

import com.edu.zup.KeyManagerGrpcReply
import com.edu.zup.KeyManagerGrpcReplyOrBuilder
import com.edu.zup.common.exceptions.GrpcExceptions
import io.grpc.Status
import io.grpc.stub.StreamObserver

class GrpcValidationException(
    messageString: String?,
    status:Status?
) : GrpcExceptions(messageString,status) {}