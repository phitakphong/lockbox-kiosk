package com.lockboxth.lockboxkiosk.http.api

import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.personal.*
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.*

interface PersonalAPI {

    @POST("kiosk/personal/general/in/verify")
    fun verifyIn(
        @Body req: VerifyInPersonalRequest
    ): Flowable<Response<HttpResponse<VerifyInPersonalResponse>>>


    @GET("kiosk/pdpa/{lang}")
    fun contentPDPA(
        @Path("lang") lang: String
    ): Flowable<Response<HttpResponse<PDPAContentResponse>>>

    @POST("kiosk/personal/register/member/confirm")
    fun confirmRegisterMember(
        @Body req: ConfirmRegisterMemberRequest
    ): Flowable<Response<HttpResponse<ConfirmRegisterMemberResponse>>>

    @POST("kiosk/personal/register/member/verify")
    fun verifyOtp(
        @Body req: VerifyOtpRequest
    ): Flowable<Response<HttpResponse<VerifyOtpResponse>>>

    @POST("kiosk/personal/general/in/finish")
    fun verifyFinish(
        @Body req: VerifyFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/personal/general/out/verify")
    fun verifyOut(
        @Body req: VerifyOutPersonalRequest
    ): Flowable<Response<HttpResponse<VerifyOutPersonalResponse>>>

    @POST("kiosk/personal/general/out/finish")
    fun verifyOut(
        @Body req: VerifyFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/personal/booking/storage/verify")
    fun verifyBooking(
        @Body req: VerifyBookingRequest
    ): Flowable<Response<HttpResponse<VerifyBookingResponse>>>

    @POST("kiosk/personal/booking/storage/finish")
    fun bookingFinish(
        @Body req: VerifyBookingFinishRequest
    ): Flowable<Response<HttpResponse<VerifyBookingFinishResponse>>>

}

