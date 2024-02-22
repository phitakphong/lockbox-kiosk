package com.lockboxth.lockboxkiosk.http.api

import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodRequest
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodResponse
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.*
import kotlin.collections.ArrayList

interface PaymentAPI {

    @POST("kiosk/storage/payment_method")
    fun paymentMethod(
        @Body req: PaymentMethodRequest
    ): Flowable<Response<HttpResponse<ArrayList<PaymentMethodResponse>>>>

}

