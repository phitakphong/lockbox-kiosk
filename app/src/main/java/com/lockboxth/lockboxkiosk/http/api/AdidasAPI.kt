package com.lockboxth.lockboxkiosk.http.api

import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.adidas.*
import com.lockboxth.lockboxkiosk.http.model.cp.CpParcelResponse
import com.lockboxth.lockboxkiosk.http.model.cp.CpReOpenRequest

import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AdidasAPI {

    @POST("kiosk/borrow/drop/verify")
    fun dropVerify(
        @Body req: AdidasVerifyRequest
    ): Flowable<Response<HttpResponse<AdidasVerifyResponse>>>

    @POST("kiosk/borrow/drop/callback")
    fun dropCallback(
        @Body req: AdidasDropCallbackRequest
    ): Flowable<Response<HttpResponse<AdidasDropCallbackResponse>>>

    @POST("kiosk/borrow/drop/finish")
    fun dropFinish(
        @Body req: AdidasDropFinishRequest
    ): Flowable<Response<HttpResponse<AdidasDropFinishResponse>>>

    @POST("kiosk/borrow/drop/reopen")
    fun dropReOpen(
        @Body req: CpReOpenRequest
    ): Flowable<Response<HttpResponse<CpParcelResponse>>>

    @POST("kiosk/borrow/pickup/verify")
    fun pickupVerify(
        @Body req: AdidasVerifyRequest
    ): Flowable<Response<HttpResponse<AdidasVerifyResponse>>>

    @POST("kiosk/borrow/pickup/callback")
    fun pickupCallback(
        @Body req: AdidasPickupCallbackRequest
    ): Flowable<Response<HttpResponse<AdidasDropCallbackResponse>>>

    @POST("kiosk/borrow/pickup/finish")
    fun pickupFinish(
        @Body req: AdidasDropFinishRequest
    ): Flowable<Response<HttpResponse<AdidasDropFinishResponse>>>

    @POST("kiosk/borrow/pickup/reopen")
    fun pickupReOpen(
        @Body req: CpReOpenRequest
    ): Flowable<Response<HttpResponse<CpParcelResponse>>>


}






