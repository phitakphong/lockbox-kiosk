package com.lockboxth.lockboxkiosk.http.model.pudo


data class PudoWalkInShipmentSelectRequest(
    val generalprofile_id: Int,
    val txn: String,
    val shipment_id: Int
)
