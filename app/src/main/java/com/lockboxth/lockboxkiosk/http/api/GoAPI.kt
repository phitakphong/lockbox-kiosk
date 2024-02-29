package com.lockboxth.lockboxkiosk.http.api

import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.cp.CpParcelResponse
import com.lockboxth.lockboxkiosk.http.model.cp.CpReOpenRequest
import com.lockboxth.lockboxkiosk.http.model.go.*
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionRequest
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutResponse
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverCheckoutRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderFinishRequest

import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface GoAPI {

    @POST("kiosk/go/drop/verify")
    fun goDropVerify(
        @Body req: GoSenderVerifyRequest
    ): Flowable<Response<HttpResponse<GoSenderVerifyResponse>>>

    @POST("kiosk/go/drop/receiver")
    fun goDropReceiverVerify(
        @Body req: GoReceiverVerifyRequest
    ): Flowable<Response<HttpResponse<GoDropVerifyResponse>>>

    @POST("kiosk/go/drop/locker")
    fun goLockerLayout(
        @Body req: GoLockerLayoutRequest
    ): Flowable<Response<HttpResponse<GoLockerLayoutResponse>>>

    @POST("kiosk/go/drop/select")
    fun goDropSelect(
        @Body req: GoLockerSelectRequest
    ): Flowable<Response<HttpResponse<GoLockerSelectResponse>>>

    @POST("kiosk/go/drop/confirm")
    fun goDropConfirm(
        @Body req: GoInfoConfirmRequest
    ): Flowable<Response<HttpResponse<GoInfoConfirmResponse>>>

    @POST("kiosk/go/drop/callback")
    fun goDropCallback(
        @Body req: GoDropCallbackRequest
    ): Flowable<Response<HttpResponse<GoDropCallbackResponse>>>

    @POST("kiosk/go/drop/finish")
    fun goDropFinish(
        @Body req: PudoSenderFinishRequest
    ): Flowable<Response<HttpResponse<GoInfoConfirmResponse>>>

    @POST("kiosk/go/drop/reopen")
    fun dropReOpen(
        @Body req: CpReOpenRequest
    ): Flowable<Response<HttpResponse<CpParcelResponse>>>

    @POST("kiosk/go/pickup/verify")
    fun goPickupVerify(
        @Body req: GoPickupVerifyRequest
    ): Flowable<Response<HttpResponse<GoPickupVerifyResponse>>>

    @POST("kiosk/go/pickup/confirm")
    fun goPickupConfirm(
        @Body req: GoPickupConfirmRequest
    ): Flowable<Response<HttpResponse<GoPickupConfirmResponse>>>

    @POST("kiosk/go/pickup/payment")
    fun pickupPaymentConfirm(
        @Body req: GoPaymentConfirmRequest
    ): Flowable<Response<HttpResponse<GoPaymentConfirmResponse>>>

    @POST("kiosk/go/pickup/checkout")
    fun pickupCheckout(
        @Body req: PudoReceiverCheckoutRequest
    ): Flowable<Response<HttpResponse<LockerCheckoutResponse>>>

    @POST("kiosk/go/pickup/cancel")
    fun pickupCancel(
        @Body req: GoPaymentCancelRequest
    ): Flowable<Response<HttpResponse<GoPaymentCancelResponse?>>>


    @POST("kiosk/go/pickup/callback")
    fun goPickupCallback(
        @Body req: GoPickupCallbackRequest
    ): Flowable<Response<HttpResponse<GoPickupCallbackResponse>>>

    @POST("kiosk/go/pickup/finish")
    fun goPickupFinish(
        @Body req: GoPickupFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/go/pickup/reopen")
    fun goPickupReOpen(
        @Body req: GoReOpenRequest
    ): Flowable<Response<HttpResponse<CpParcelResponse>>>

}






