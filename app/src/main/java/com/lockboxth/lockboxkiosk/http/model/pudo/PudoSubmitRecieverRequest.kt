package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoSubmitRecieverRequest(
    val generalprofile_id: Int,
    val txn: String,
    val fname: String,
    val lname: String,
    val phone: String,
    val address1: String,
    var province_id: Int,
    var amphur_id: Int,
    var district_id: Int
)

