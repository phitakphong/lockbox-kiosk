package com.lockboxth.lockboxkiosk.helpers

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lockboxth.lockboxkiosk.http.model.kiosk.MainMenuItem
import com.lockboxth.lockboxkiosk.http.model.kiosk.RegisterKioskResponse

class MyPrefs(val context: Context) : Prefs(context) {


    private val KIOSK_INFO = "kiosk_info"
    private val TRANS_ID = "trans_id"
    private val LOCKER_TRANS_TYPE = "trans_type"
    private val BOOKING_ID = "booking_id"
    private val VERIFY_TYPE = "verify_type"
    private val LANG = "lang"
    private val CONFIG = "config"

    private val gson: Gson by lazy { Gsoner.getInstance().gson }

    var kioskInfo: RegisterKioskResponse?
        get() {
            val json = prefs.getString(KIOSK_INFO, null)
            if (json.isNullOrEmpty()) {
                return null
            }
            return gson.fromJson(json, RegisterKioskResponse::class.java)
        }
        set(value) {
            if (value != null) {
                val json = gson.toJson(value)
                prefs.edit().putString(KIOSK_INFO, json).apply()
            } else {
                prefs.edit().remove(KIOSK_INFO).apply()
            }
        }

    var currentTransactionId: String?
        get() {
            return prefs.getString(TRANS_ID, null)
        }
        set(value) {
            if (value != null) {
                prefs.edit().putString(TRANS_ID, value).apply()
            } else {
                prefs.edit().remove(TRANS_ID).apply()
            }
        }

    var currentBookingId: Int?
        get() {
            val id = prefs.getInt(BOOKING_ID, 0)
            return if (id != 0) {
                id
            } else {
                null
            }
        }
        set(value) {
            if (value != null) {
                prefs.edit().putInt(BOOKING_ID, value).apply()
            } else {
                prefs.edit().remove(BOOKING_ID).apply()
            }
        }

    var currentVerifyType: String?
        get() {
            return prefs.getString(VERIFY_TYPE, null)
        }
        set(value) {
            if (value != null) {
                prefs.edit().putString(VERIFY_TYPE, value).apply()
            } else {
                prefs.edit().remove(VERIFY_TYPE).apply()
            }
        }

    var currentTransactionType: TransactionType?
        get() {
            val v = prefs.getString(LOCKER_TRANS_TYPE, null) ?: return null
            return TransactionType.valueOf(v)
        }
        set(value) {
            if (value != null) {
                prefs.edit().putString(LOCKER_TRANS_TYPE, value.toString()).apply()
            } else {
                prefs.edit().remove(LOCKER_TRANS_TYPE).apply()
            }
        }

    var currentLanguage: String
        get() {
            return prefs.getString(LANG, null) ?: "th"
        }
        set(value) {
            prefs.edit().putString(LANG, value).apply()
        }

    var currentParcelType: ParcelType?
        get() {
            val v = prefs.getString("PARCEL_TYPE", null) ?: return null
            return ParcelType.valueOf(v)
        }
        set(value) {
            if (value != null) {
                prefs.edit().putString("PARCEL_TYPE", value.toString()).apply()
            } else {
                prefs.edit().remove("PARCEL_TYPE").apply()
            }
        }

    var currentUserType: UserTypeType?
        get() {
            val v = prefs.getString("USER_TYPE", null) ?: return null
            return UserTypeType.valueOf(v)
        }
        set(value) {
            if (value != null) {
                prefs.edit().putString("USER_TYPE", value.toString()).apply()
            } else {
                prefs.edit().remove("USER_TYPE").apply()
            }
        }

    var currentConfig: List<MainMenuItem>?
        get() {
            val json = prefs.getString(CONFIG, null)
            if (json.isNullOrEmpty()) {
                return null
            }
            val itemType = object : TypeToken<List<MainMenuItem>>() {}.type
            return gson.fromJson<List<MainMenuItem>>(json, itemType)
        }
        set(value) {
            if (value != null) {
                val json = gson.toJson(value)
                prefs.edit().putString(CONFIG, json).apply()
            } else {
                prefs.edit().remove(CONFIG).apply()
            }
        }
}

enum class ParcelType {
    SENDER, RECEIVER
}


enum class UserTypeType {
    USER, OFFICER, PARTNER
}


enum class TransactionType {
    IN, OUT,
    TOPUP,
    BOOKING,
    PUDO_SENDER, PUDO_SENDER_WALKIN, PUDO_COURIER_PICKUP, PUDO_COURIER_SENDER, PUDO_RECEIVER, PUDO_CP_DROP, PUDO_CP_PICKUP,ADIDAS_DROP, ADIDAS_PICKUP,
    GO_IN, GO_OUT;
}