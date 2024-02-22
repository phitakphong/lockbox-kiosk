package com.lockboxth.lockboxkiosk.http.repository

import android.annotation.SuppressLint
import android.util.Log
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.NetService
import com.lockboxth.lockboxkiosk.http.api.TopupAPI
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionRequest
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionResponse
import com.lockboxth.lockboxkiosk.http.model.topup.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class TopupRepository {

    companion object {
        private var instance: TopupRepository? = null
            get() {
                if (field == null) {
                    synchronized(TopupRepository::class.java) {
                        if (field == null) {
                            field = TopupRepository()
                        }
                    }
                }
                return field
            }

        @JvmName("getInstance1")
        fun getInstance(): TopupRepository {
            return instance!!
        }
    }

    fun verify(req: TopupVerifyRequest, onSuccess: (TopupVerifyResponse) -> Unit, onFailure: (HttpResponse<*>) -> Unit) {
        val api = NetService.getRetrofit().create(TopupAPI::class.java)
        api.verify(req)
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
                        onFailure(it.body()!!)
                    }
                } else {
                    val error = Util.handleResponseErrorObj(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure(HttpResponse(status = false, "ERROR", "an error occurred, please try again later.", {}))
            })
    }

    fun confirm(req: TopupConfirmRequest, onSuccess: (TopupConfirmResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(TopupAPI::class.java)
        api.confirm(req)
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

    fun checkout(req: TopupCheckoutRequest, onSuccess: (TopupCheckoutResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(TopupAPI::class.java)
        api.checkout(req)
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

    fun cancel(req: CancelTransactionRequest, onSuccess: (CancelTransactionResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(TopupAPI::class.java)
        api.cancel(req)
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