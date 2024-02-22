package com.lockboxth.lockboxkiosk.service.firebase

import com.lockboxth.lockboxkiosk.helpers.Contextor
import io.reactivex.*
import io.reactivex.internal.operators.observable.ObservableAny
import io.reactivex.subjects.PublishSubject

data class NotificationsData(
    val action: String,
    val locker_commands: String?,
    val money_change: String?,
    val locker_no: String?,
    val payment_status: String?,
    val event_type: String?,
    val txn: String?,
)
//data class NotificationsData(
//    val action: String,
//    val locker_commands: String?,
//    val money_change: String?,
//    val locker_no: String?,
//    val payment_status: String?,
//    val event_type: String?,
//    val transaction_id: String?,
//)
//
//{
//    "txn": "UL100230402123123",
//    "generalprofile_id": 1,
//    "event_type": "unlock_emergency",
//    "locker_commands": {
//        "5": {
//            "inquire": ":011000080001020010D4",
//            "open": ":010400060002F3"
//        },
//        "7": {
//            "inquire": ":011000080001020040A4",
//            "open": ":010400060002F3"
//        }
//    }
//}