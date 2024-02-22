package com.lockboxth.lockboxkiosk.http.model.locker

import com.google.gson.JsonObject

data class LockerLayoutResponse(
    val txn: String,
    val cnt_available: Int,
    val cnt_use: Int,
    val cnt_book: Int,
    val lockers: ArrayList<LockerItem>,
    val locker_size_details: ArrayList<LockerSizeItem>,
    val locker_name: Map<String, String>?,
    val locker_transaction: Map<String, ArrayList<UseTransaction>>?
)

data class LockerItem(
    val level: Int,
    val slot_type: String,
    val slots: List<Any>
)

data class SlotItem(
    val locker_no: String,
    val block_use: Int,
    val size: String,
    val can_select: Boolean,
    val status: String
)

data class UseTransaction(
    val locker_no: String,
    val block_use: Int,
)


data class LockerSizeItem(
    val size: String,
    val count: Int,
)