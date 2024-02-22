package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail

data class PudoReceiverConfirmPaymentResponse(
    val total_amount: Float,
    val amount_pay: Float,
    val service_fee: Float,
    val parcel_cod: Float?,
    val time_use_hour: Int,
    val time_use_min: Int,
    val time_use: Int,
    val time_over: Int,
    val details: Map<String, LockerCalculateDetail>
)


data class PudoReceiverConfirmWithoutPaymentResponse(
    val event_type: String,
    val transaction_id: String,
    val locker_no: String,
    val locker_commands: JsonObject
)
