package com.lockboxth.lockboxkiosk.http.repository

import android.annotation.SuppressLint
import android.util.Log
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.NetService
import com.lockboxth.lockboxkiosk.http.api.*
import com.lockboxth.lockboxkiosk.http.model.adidas.*
import com.lockboxth.lockboxkiosk.http.model.cp.*
import com.lockboxth.lockboxkiosk.http.model.topup.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class AdidasRepository {

    companion object {
        private var instance: AdidasRepository? = null
            get() {
                if (field == null) {
                    synchronized(AdidasRepository::class.java) {
                        if (field == null) {
                            field = AdidasRepository()
                        }
                    }
                }
                return field
            }

        @JvmName("getInstance1")
        fun getInstance(): AdidasRepository {
            return instance!!
        }
    }

    fun dropVerify(req: AdidasVerifyRequest, onSuccess: (AdidasVerifyResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(AdidasAPI::class.java)
        api.dropVerify(req)
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

    fun dropCallback(req: AdidasDropCallbackRequest, onSuccess: (AdidasDropCallbackResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(AdidasAPI::class.java)
        api.dropCallback(req)
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

    fun dropFinish(req: AdidasDropFinishRequest, onSuccess: (String) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(AdidasAPI::class.java)
        api.dropFinish(req)
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

    fun dropReOpen(req: CpReOpenRequest, onSuccess: (CpParcelResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(AdidasAPI::class.java)
        api.dropReOpen(req)
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

    fun pickupVerify(req: AdidasVerifyRequest, onSuccess: (AdidasVerifyResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(AdidasAPI::class.java)
        api.pickupVerify(req)
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

    fun pickupCallback(req: AdidasPickupCallbackRequest, onSuccess: (AdidasDropCallbackResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(AdidasAPI::class.java)
        api.pickupCallback(req)
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

    fun pickupFinish(req: AdidasDropFinishRequest, onSuccess: (String) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(AdidasAPI::class.java)
        api.pickupFinish(req)
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

    fun pickupReOpen(req: CpReOpenRequest, onSuccess: (CpParcelResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(AdidasAPI::class.java)
        api.pickupReOpen(req)
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