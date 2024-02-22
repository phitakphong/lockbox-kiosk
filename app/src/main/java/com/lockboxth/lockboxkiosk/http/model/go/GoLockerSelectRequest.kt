package com.lockboxth.lockboxkiosk.http.model.go

import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail
import com.lockboxth.lockboxkiosk.http.model.locker.LockerItem
import com.lockboxth.lockboxkiosk.http.model.locker.LockerSizeItem
import com.lockboxth.lockboxkiosk.http.model.locker.UseTransaction

data class GoLockerSelectRequest(
    val generalprofile_id: Int,
    val txn: String,
    val block_use: Int
)