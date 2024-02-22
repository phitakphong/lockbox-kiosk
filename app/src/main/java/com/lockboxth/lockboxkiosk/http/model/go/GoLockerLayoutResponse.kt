package com.lockboxth.lockboxkiosk.http.model.go

import com.lockboxth.lockboxkiosk.http.model.locker.LockerItem
import com.lockboxth.lockboxkiosk.http.model.locker.LockerSizeItem
import com.lockboxth.lockboxkiosk.http.model.locker.UseTransaction

data class GoLockerLayoutResponse(
    val cnt_available: Int,
    val cnt_use: Int,
    val cnt_book: Int,
    val lockers: ArrayList<LockerItem>,
    val locker_size_details: ArrayList<LockerSizeItem>
)