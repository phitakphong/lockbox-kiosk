package com.lockboxth.lockboxkiosk.http.model.go


data class GoPaymentConfirmResponse(
    val total_amount: Float,
    val time_limit: Float,
    val details: Map<String, GoPaymentConfirmDetail>,
    val payment_method: String,
    val amount_pay: Float,
    val discount_total: Float,
    val discount_member: Float,
    val discount_promo: Float,
    val discount_list: ArrayList<GoPaymentConfirmDiscount>,
    val payment_method_display: String,
    val remark: String?
)

data class GoPaymentConfirmDiscount(
    val detail: String,
    val sum: Float
)

data class GoPaymentConfirmDetail(
    val parcel_cnt: Int,
    val rate_hour: Int,
    val rate_day: Int,
    val sum: Int,
    val time_use_hour: Int,
    val time_use_min: Int,
    val time_over: Int

)
