package com.lockboxth.lockboxkiosk.http.model.locker

data class LockerCalculateDetailResponse(
    val service_fee: ServiceFeeDetail,
    val calculation: CalculationDetail
)

data class ServiceFeeDetail(
    val amount: Float,
    val discount: Float,
    val fee: Float,
    val time_limit: Int,
    val details: Map<String, LockerCalculateDetail>,
)

data class CalculationDetail(
    val amount_total: Float,
    val member_discount: Float,
    val promotion_discount: Float,
    val discount_total: Float,
    val amount_after_discount: Float,
    val payment_method: String,
    val remark: String,
    val promotion: ArrayList<Map<String, Any>>?,
    val member: Map<String, Any>?
)


