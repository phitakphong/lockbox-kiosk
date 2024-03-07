package com.lockboxth.lockboxkiosk.http.repository

import android.annotation.SuppressLint
import android.util.Log
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.NetService
import com.lockboxth.lockboxkiosk.http.api.*
import com.lockboxth.lockboxkiosk.http.model.cp.*
import com.lockboxth.lockboxkiosk.http.model.go.*
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutResponse
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverCheckoutRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderFinishRequest
import com.lockboxth.lockboxkiosk.http.model.topup.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class GoRepository {

    companion object {
        private var instance: GoRepository? = null
            get() {
                if (field == null) {
                    synchronized(GoRepository::class.java) {
                        if (field == null) {
                            field = GoRepository()
                        }
                    }
                }
                return field
            }

        @JvmName("getInstance1")
        fun getInstance(): GoRepository {
            return instance!!
        }
    }

    fun goDropVerify(req: GoSenderVerifyRequest, onSuccess: (GoSenderVerifyResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goDropVerify(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goDropReceiverVerify(req: GoReceiverVerifyRequest, onSuccess: (GoDropVerifyResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goDropReceiverVerify(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goLockerLayout(req: GoLockerLayoutRequest, onSuccess: (GoLockerLayoutResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goLockerLayout(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goDropSelect(req: GoLockerSelectRequest, onSuccess: (GoLockerSelectResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goDropSelect(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goDropConfirm(req: GoInfoConfirmRequest, onSuccess: (GoInfoConfirmResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goDropConfirm(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goDropCallback(req: GoDropCallbackRequest, onSuccess: (GoDropCallbackResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goDropCallback(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goDropFinish(req: PudoSenderFinishRequest, onSuccess: (String, GoInfoConfirmResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goDropFinish(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    onSuccess(it.body()!!.code!!, it.body()!!.info)
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
        val api = NetService.getRetrofit().create(GoAPI::class.java)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goPickupVerify(req: GoPickupVerifyRequest, onSuccess: (GoPickupVerifyResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goPickupVerify(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goPickupConfirm(req: GoPickupConfirmRequest, onSuccess: (String, GoPickupConfirmResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goPickupConfirm(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess(it.body()!!.code!!, it.body()!!.info)
                    } else {
                        val message = "${it.body()!!.code} : ${it.body()!!.message}"
                        Log.d("API Response Error", message)
                        onFailure(it.body()!!.message!!)
                    }
                } else {
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goPickupCallback(req: GoPickupCallbackRequest, onSuccess: (GoPickupCallbackResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goPickupCallback(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goPickupFinish(req: GoPickupFinishRequest, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goPickupFinish(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun goPickupReOpen(req: GoReOpenRequest, onSuccess: (CpParcelResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.goPickupReOpen(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun pickupPaymentConfirm(req: GoPaymentConfirmRequest, onSuccess: (GoPaymentConfirmResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.pickupPaymentConfirm(req)
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
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun pickupCancel(req: GoPaymentCancelRequest, onSuccess: (GoPaymentCancelResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.pickupCancel(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    onSuccess(it.body()!!.info!!)
                } else {
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }

    fun pickupCheckout(req: PudoReceiverCheckoutRequest, onSuccess: (String, LockerCheckoutResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(GoAPI::class.java)
        api.pickupCheckout(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess(it.body()!!.code!!, it.body()!!.info)
                    } else {
                        val message = "${it.body()!!.code} : ${it.body()!!.message}"
                        Log.d("API Response Error", message)
                        onFailure(it.body()!!.message!!)
                    }
                } else {
                    val error = Util.handleResponseErrorMessage(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure("เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง")
            })
    }
}