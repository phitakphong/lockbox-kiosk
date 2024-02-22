package com.lockboxth.lockboxkiosk.http.repository

import android.annotation.SuppressLint
import android.util.Log
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.NetService
import com.lockboxth.lockboxkiosk.http.api.KioskAPI
import com.lockboxth.lockboxkiosk.http.model.kiosk.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class KioskRepository {

    companion object {
        private var instance: KioskRepository? = null
            get() {
                if (field == null) {
                    synchronized(KioskRepository::class.java) {
                        if (field == null) {
                            field = KioskRepository()
                        }
                    }
                }
                return field
            }

        @JvmName("getInstance1")
        fun getInstance(): KioskRepository {
            return instance!!
        }
    }

    fun register(req: RegisterKiosk, onSuccess: (RegisterKioskResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(KioskAPI::class.java)
        api.register(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess(it.body()!!.info)
                    } else {
                        val message = "${it.body()!!.code} : ${it.body()!!.message}"
                        Log.d("API Response Error", message)
                        onFailure("an error occurred, please try again later.")
                    }
                } else {
                    val error = Util.handleResponseErrorCode(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun clearTransaction(req: ClearTransactionRequest, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(KioskAPI::class.java)
        api.clearTransaction(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess()
                    } else {
                        val message = "${it.body()!!.code} : ${it.body()!!.message}"
                        Log.d("API Response Error", message)
                        onFailure("an error occurred, please try again later.")
                    }
                } else {
                    val error = Util.handleResponseErrorCode(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun ads(req: KioskProfileRequest, onSuccess: (ads: KioskAdsResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(KioskAPI::class.java)
        api.ads(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess(it.body()!!.info)
                    } else {
                        val message = "${it.body()!!.code} : ${it.body()!!.message}"
                        Log.d("API Response Error", message)
                        onFailure("an error occurred, please try again later.")
                    }
                } else {
                    val error = Util.handleResponseErrorCode(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun monitorScreen(req: KioskMonitorRequest, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(KioskAPI::class.java)
        api.monitorScreen(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess()
                    } else {
                        val message = "${it.body()!!.code} : ${it.body()!!.message}"
                        Log.d("API Response Error", message)
                        onFailure("an error occurred, please try again later.")
                    }
                } else {
                    val error = Util.handleResponseErrorCode(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun heartbeat(profileId: Long, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(KioskAPI::class.java)
        api.heartbeat(profileId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess()
                    } else {
                        val message = "${it.body()!!.code} : ${it.body()!!.message}"
                        Log.d("API Response Error", message)
                        onFailure("an error occurred, please try again later.")
                    }
                } else {
                    val error = Util.handleResponseErrorCode(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun config(profileId: Int, onSuccess: (ConfigResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(KioskAPI::class.java)
        api.config(profileId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess(it.body()!!.info)
                    } else {
                        val message = "${it.body()!!.code} : ${it.body()!!.message}"
                        Log.d("API Response Error", message)
                        onFailure("an error occurred, please try again later.")
                    }
                } else {
                    val error = Util.handleResponseErrorCode(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }
}