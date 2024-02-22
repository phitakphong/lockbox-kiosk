package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoWalkinWayBillResponse(
    val print_count: Int,
    val print_limit: Int,
    val shipping_image: String
//    val shipping_detail: PudoWalkinWayBillShippingDetail
)

data class PudoWalkinWayBillShippingDetail(
    val tracking_number: String
)