package com.lockboxth.lockboxkiosk.http.api

import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionRequest
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionResponse
import com.lockboxth.lockboxkiosk.http.model.topup.*
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TopupAPI {

    @POST("kiosk/topup/verify")
    fun verify(
        @Body req: TopupVerifyRequest
    ): Flowable<Response<HttpResponse<TopupVerifyResponse>>>

    @POST("kiosk/topup/confirm")
    fun confirm(
        @Body req: TopupConfirmRequest
    ): Flowable<Response<HttpResponse<TopupConfirmResponse>>>

    @POST("kiosk/topup/checkout")
    fun checkout(
        @Body req: TopupCheckoutRequest
    ): Flowable<Response<HttpResponse<TopupCheckoutResponse>>>


    @POST("kiosk/topup/cancel")
    fun cancel(
        @Body req: CancelTransactionRequest
    ): Flowable<Response<HttpResponse<CancelTransactionResponse>>>

}








