package com.lockboxth.lockboxkiosk.http.model.pudo

import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail

data class PudoWalkInPaymentConfirmResponse(
    val fee_data: PudoWalkInFeeData,
    val amount_total: Float,
    val amount_pay: Float,
    val discount_total: Float,
    val payment_method_display: String,
    val remark: String
)


data class PudoWalkInPaymentDetail(
    val detail: String,
    val fee: Float
)

data class PudoWalkInPaymentDiscount(
    val detail: String,
    val sum: Float
)

data class PudoWalkInFeeData(
    val sum_total: Float,
    val amount_pay: Float,
    val details: List<PudoWalkInPaymentDetail>,
    val discount_list: List<PudoWalkInPaymentDiscount>
)
