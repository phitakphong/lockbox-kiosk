package com.lockboxth.lockboxkiosk.http.model.pudo

import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail

data class PudoReceiverPaymentConfirmResponse(
    val total_amount: Float,
    val amount_pay: Float,
    val service_fee: Float,
    val parcel_cod: Float,
    val time_use_hour: Int,
    val time_use_min: Int,
    val time_use: Int,
    val time_over: Int,
    val details: Map<String, LockerCalculateDetail>,
    val payment_method: String,
    val cod_fee: Float,
    val discount_total: Float,
    val discount_member: Float,
    val discount_promo: Float,
    val promotion: Float,
//    val discount_list: Float,
    val payment_method_display: String,
    val remark: String
)



