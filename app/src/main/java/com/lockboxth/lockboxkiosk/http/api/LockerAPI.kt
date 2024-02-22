package com.lockboxth.lockboxkiosk.http.api

import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.kiosk.KioskProfileRequest
import com.lockboxth.lockboxkiosk.http.model.locker.*
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyBookingResponse
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyFinishRequest
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.*

interface LockerAPI {

    @POST("kiosk/locker/general/in")
    fun generalLayoutIn(
        @Body req: KioskProfileRequest
    ): Flowable<Response<HttpResponse<LockerLayoutResponse>>>

    @POST("kiosk/storage/general/in/verify")
    fun confirmIn(
        @Body req: ConfirmSelectLockerRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/storage/general/in/calculate")
    fun calcTotal(
        @Body req: LockerCalculateRequest
    ): Flowable<Response<HttpResponse<LockerCalculateResponse>>>

    @POST("kiosk/storage/general/{type}/calculate")
    fun calcDetail(
        @Path("type") type: String,
        @Body req: LockerCalculateDetailRequest
    ): Flowable<Response<HttpResponse<LockerCalculateDetailResponse>>>

    @POST("kiosk/storage/general/{type}/checkout")
    fun checkout(
        @Path("type") type: String,
        @Body req: LockerCheckoutRequest
    ): Flowable<Response<HttpResponse<LockerCheckoutResponse>>>

    @POST("kiosk/storage/general/{type}/cancel")
    fun cancel(
        @Path("type") type: String,
        @Body req: CancelTransactionRequest
    ): Flowable<Response<HttpResponse<CancelTransactionResponse>>>

    @POST("kiosk/locker/general/out")
    fun generalLayoutOut(
        @Body req: LockerOutRequest
    ): Flowable<Response<HttpResponse<LockerLayoutResponse>>>

    @POST("kiosk/storage/general/out/verify")
    fun generalOutVerify(
        @Body req: ConfirmSelectLockerRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/storage/general/out/calculate")
    fun calcOut(
        @Body req: LockerCalculateRequest
    ): Flowable<Response<HttpResponse<LockerCalculateOutResponse>>>

    @POST("kiosk/storage/general/finish")
    fun finish(
        @Body req: LockerFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/personal/booking/storage/recheck")
    fun bookingRecheck(
        @Body req: LockerBookingRecheckRequest
    ): Flowable<Response<HttpResponse<LockerBookingRecheckResponse>>>


    @POST("kiosk/unlock/emergency")
    fun unlockEmergency(
        @Body req: LockerFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>


}








