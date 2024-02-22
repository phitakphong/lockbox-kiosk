package com.lockboxth.lockboxkiosk.http.repository

import android.annotation.SuppressLint
import android.util.Log
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.NetService
import com.lockboxth.lockboxkiosk.http.api.CpAPI
import com.lockboxth.lockboxkiosk.http.api.LockerAPI
import com.lockboxth.lockboxkiosk.http.api.PudoAPI
import com.lockboxth.lockboxkiosk.http.api.TopupAPI
import com.lockboxth.lockboxkiosk.http.model.cp.*
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionRequest
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionResponse
import com.lockboxth.lockboxkiosk.http.model.locker.LockerFinishRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderCallbackRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderCallbackSuccessResponse
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderConfirmResponse
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderFinishRequest
import com.lockboxth.lockboxkiosk.http.model.topup.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class CpRepository {

    companion object {
        private var instance: CpRepository? = null
            get() {
                if (field == null) {
                    synchronized(CpRepository::class.java) {
                        if (field == null) {
                            field = CpRepository()
                        }
                    }
                }
                return field
            }

        @JvmName("getInstance1")
        fun getInstance(): CpRepository {
            return instance!!
        }
    }

    fun cpParcel(req: CpParcelRequest, onSuccess: (CpParcelResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(CpAPI::class.java)
        api.cpParcel(req)
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

    fun senderCallbackSuccess(req: PudoSenderCallbackRequest, onSuccess: (CpCallbackResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(CpAPI::class.java)
        api.senderCallbackSuccess(req)
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
                        onFailure(HttpResponse(false, it.body()!!.code, it.body()!!.message, {}))
                    }
                } else {
                    val error = Util.handleResponseErrorObj(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure(HttpResponse<Any>(false, "ERROR", "เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง", {}))
            })
    }

    fun senderFinishChange(req: PudoSenderFinishRequest, onSuccess: (PudoSenderConfirmResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(CpAPI::class.java)
        api.senderFinishChange(req)
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
                        onFailure(HttpResponse(false, it.body()!!.code, it.body()!!.message, {}))
                    }
                } else {
                    val error = Util.handleResponseErrorObj(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure(HttpResponse<Any>(false, "ERROR", "เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง", {}))
            })
    }

    fun senderFinish(req: PudoSenderFinishRequest, onSuccess: (String) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(CpAPI::class.java)
        api.senderFinish(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    onSuccess(it.body()!!.code!!)
                } else {
                    val error = Util.handleResponseErrorObj(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure(HttpResponse<Any>(false, "ERROR", "เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง", {}))
            })
    }

    fun receiverVerify(req: CpVerifyRequest, onSuccess: (CpVerifyResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(CpAPI::class.java)
        api.receiverVerify(req)
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
                        onFailure(HttpResponse(false, it.body()!!.code, it.body()!!.message, {}))
                    }
                } else {
                    val error = Util.handleResponseErrorObj(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure(HttpResponse<Any>(false, "ERROR", "เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง", {}))
            })
    }

    fun receiverFinish(req: LockerFinishRequest, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(CpAPI::class.java)
        api.receiverFinish(req)
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
                        onFailure(it.body()!!.message!!)
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

    fun senderReOpen(req: CpReOpenRequest, onSuccess: (CpParcelResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(CpAPI::class.java)
        api.senderReOpen(req)
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
                        onFailure(it.body()!!.message!!)
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

    fun receiverAck(req: CpPickupAck, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(CpAPI::class.java)
        api.receiverAck(req)
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
                        onFailure(it.body()!!.message!!)
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

    fun prefix(req: CpPrefixRequest, onSuccess: (CpPrefixResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(CpAPI::class.java)
        api.prefix(req)
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
                        onFailure(it.body()!!.message!!)
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