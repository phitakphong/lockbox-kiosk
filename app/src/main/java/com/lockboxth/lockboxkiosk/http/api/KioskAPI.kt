package com.lockboxth.lockboxkiosk.http.api

import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.kiosk.*
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface KioskAPI {

    @POST("kiosk/register")
    fun register(
        @Body req: RegisterKiosk
    ): Flowable<Response<HttpResponse<RegisterKioskResponse>>>

    @POST("kiosk/storage/clear")
    fun clearTransaction(
        @Body req: ClearTransactionRequest
    ): Flowable<Response<HttpResponse<Any>>>


    @POST("kiosk/ads")
    fun ads(
        @Body req: KioskProfileRequest
    ): Flowable<Response<HttpResponse<KioskAdsResponse>>>

    @POST("kiosk/monitor/screen")
    fun monitorScreen(
        @Body req: KioskMonitorRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @GET("kiosk/heartbeat/{generalprofile_id}")
    fun heartbeat(
        @Path("generalprofile_id") generalprofile_id: Long,
    ): Flowable<Response<HttpResponse<Any>>>

    @GET("kiosk/config/{generalprofile_id}")
    fun config(
        @Path("generalprofile_id") generalprofile_id: Int,
    ): Flowable<Response<HttpResponse<ConfigResponse>>>
}








