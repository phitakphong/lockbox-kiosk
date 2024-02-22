package com.lockboxth.lockboxkiosk.http.model.locker

import com.google.gson.JsonObject

data class LockerFinishRequest(
    var generalprofile_id: Int,
    var txn: String,
    var event_type: String,
    var locker_results: JsonObject
)
