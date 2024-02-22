package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoAddBankRequest(
    val generalprofile_id: Int,
    val txn: String,
    val bank_id: Int,
    val account_id: String,
    val account_name: String,
)

