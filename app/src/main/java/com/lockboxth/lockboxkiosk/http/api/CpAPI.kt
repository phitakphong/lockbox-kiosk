package com.lockboxth.lockboxkiosk.http.api

import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.cp.*
import com.lockboxth.lockboxkiosk.http.model.locker.LockerFinishRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderCallbackRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderCallbackSuccessResponse
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderConfirmResponse
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderFinishRequest

import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface CpAPI {


    @POST("kiosk/cp/courier/parcel")
    fun cpParcel(
        @Body req: CpParcelRequest
    ): Flowable<Response<HttpResponse<CpParcelResponse>>>


    @POST("kiosk/cp/courier/callback")
    fun senderCallbackSuccess(
        @Body req: PudoSenderCallbackRequest
    ): Flowable<Response<HttpResponse<CpCallbackResponse>>>

    @POST("kiosk/cp/courier/finish")
    fun senderFinishChange(
        @Body req: PudoSenderFinishRequest
    ): Flowable<Response<HttpResponse<PudoSenderConfirmResponse>>>

    @POST("kiosk/cp/courier/finish")
    fun senderFinish(
        @Body req: PudoSenderFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/cp/receiver/verify")
    fun receiverVerify(
        @Body req: CpVerifyRequest
    ): Flowable<Response<HttpResponse<CpVerifyResponse>>>

    @POST("kiosk/cp/receiver/finish")
    fun receiverFinish(
        @Body req: LockerFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/cp/courier/reopen")
    fun senderReOpen(
        @Body req: CpReOpenRequest
    ): Flowable<Response<HttpResponse<CpParcelResponse>>>

    @POST("kiosk/cp/receiver/ack")
    fun receiverAck(
        @Body req: CpPickupAck
    ): Flowable<Response<HttpResponse<Any>>>


    @POST("kiosk/cp/courier/prefix")
    fun prefix(
        @Body req: CpPrefixRequest
    ): Flowable<Response<HttpResponse<CpPrefixResponse>>>
}






