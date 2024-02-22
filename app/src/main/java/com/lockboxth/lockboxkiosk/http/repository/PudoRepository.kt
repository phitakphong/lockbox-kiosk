package com.lockboxth.lockboxkiosk.http.repository

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.helpers.Gsoner
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.NetService
import com.lockboxth.lockboxkiosk.http.api.CpAPI
import com.lockboxth.lockboxkiosk.http.api.PudoAPI
import com.lockboxth.lockboxkiosk.http.model.cp.CpParcelResponse
import com.lockboxth.lockboxkiosk.http.model.cp.CpReOpenRequest
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionRequest
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutRequest
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutResponse
import com.lockboxth.lockboxkiosk.http.model.locker.LockerFinishRequest
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodRequest
import com.lockboxth.lockboxkiosk.http.model.personal.ConfirmRegisterMemberRequest
import com.lockboxth.lockboxkiosk.http.model.personal.ConfirmRegisterMemberResponse
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyOtpRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class PudoRepository {

    companion object {
        private var instance: PudoRepository? = null
            get() {
                if (field == null) {
                    synchronized(PudoRepository::class.java) {
                        if (field == null) {
                            field = PudoRepository()
                        }
                    }
                }
                return field
            }

        @JvmName("getInstance1")
        fun getInstance(): PudoRepository {
            return instance!!
        }
    }

    fun senderVerify(req: PudoSenderVerifyRequest, onSuccess: (PudoSenderVerifyResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderVerify(req)
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

    fun senderIdentify(req: PudoVerifyIdCardRequest, onSuccess: (PudoParcelAddressResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderIdentify(req)
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

    fun senderConfirm(req: PudoSenderConfirmRequest, onSuccess: (PudoSenderConfirmResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderConfirm(req)
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

    fun senderCallbackSuccess(req: PudoSenderCallbackRequest, onSuccess: (PudoSenderCallbackSuccessResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
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

    fun senderWalkinCallbackSuccess(req: PudoSenderCallbackRequest, onSuccess: (PudoSenderCallbackSuccessResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderWalkinCallbackSuccess(req)
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

    fun senderCallbackChangeLocker(req: PudoSenderCallbackRequest, onSuccess: (PudoSenderConfirmResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderCallbackChangeLocker(req)
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

    fun senderWalkinCallbackChangeLocker(req: PudoSenderCallbackRequest, onSuccess: (PudoSenderConfirmResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderWalkinCallbackChangeLocker(req)
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

    fun senderFinish(req: PudoSenderFinishRequest, onSuccess: () -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderFinish(req)
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
                        onFailure(it.body()!!)
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

    fun senderWalkinFinish(req: PudoSenderFinishRequest, onSuccess: () -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderWalkinFinish(req)
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
                        onFailure(it.body()!!)
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
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
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

    fun senderWalkinFinishChange(req: PudoSenderFinishRequest, onSuccess: (PudoSenderConfirmResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderWalkinFinishChange(req)
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

    fun verifyMember(req: PudoVerifyMemberRequest, onSuccess: (PudoVerifyMemberResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.verifyMember(req)
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

    fun requestOtp(req: ConfirmRegisterMemberRequest, onSuccess: (ConfirmRegisterMemberResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.requestOtp(req)
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

    fun verifyOtp(req: VerifyOtpRequest, onSuccess: () -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.verifyOtp(req)
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
                        onFailure(HttpResponse(false, it.body()!!.code, it.body()!!.message, {}))
                    }
                } else {
                    val error = Util.handleResponseErrorObj(it.errorBody()!!.string())
                    onFailure(error)
                }
            }, {
                it.printStackTrace()
                onFailure(HttpResponse(false, "ERROR", "เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง", {}))
            })
    }

    fun walkInIdentify(req: PudoVerifyIdCardRequest, onSuccess: (String) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.walkInIdentify(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess(it.body()!!.warning_message ?: "-")
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

    fun walkInConsent(req: PudoWalkInConsentRequest, onSuccess: (PudoWalkInConsentResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.walkInConsent(req)
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

    fun shipments(req: PudoShipmentRequest, onSuccess: (PudoShipmentResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.shipments(req)
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

    fun selectCourier(req: PudoCourierSelectRequest, onSuccess: () -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.selectCourier(req)
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

    fun verifyCourier(req: PudoVerifyCourierRequest, onSuccess: (PudoVerifyCourierResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.verifyCourier(req)
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

    fun updateStatusCourier(req: PudoUpdateStatusCourierRequest, onSuccess: (PudoVerifyCourierResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.updateStatusCourier(req)
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

    fun courierParcel(req: PudoCourierParcelRequest, onSuccess: (PudoCourierParcelResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.courierParcel(req)
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

    fun courierCallback(req: PudoCourierCallbackRequest, onSuccess: (PudoCourierCallbackResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.courierCallback(req)
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

    fun receiverVerify(req: PudoReceiverVerifyRequest, onSuccess: (PudoReceiverVerifyResponse) -> Unit, onFailure: (HttpResponse<Any>) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
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

    fun receiverConfirm(
        req: PudoReceiverConfirmRequest,
        onSuccess: (String, PudoReceiverConfirmPaymentResponse?, PudoReceiverConfirmWithoutPaymentResponse?) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.receiverConfirm(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        val gson = Gsoner.getInstance().gson
                        val json = gson.toJson(it.body()!!.info)
                        val code = it.body()!!.code!!
                        if (code == "PAYMENT") {
                            val resp = gson.fromJson(json, PudoReceiverConfirmPaymentResponse::class.java)
                            onSuccess(code, resp, null)
                        } else {
                            val resp = gson.fromJson(json, PudoReceiverConfirmWithoutPaymentResponse::class.java)
                            onSuccess(code, null, resp)
                        }
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

    fun receiverPayment(
        req: PaymentMethodRequest,
        onSuccess: (PudoReceiverPaymentResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.receiverPayment(req)
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

    fun receiverPaymentConfirm(
        req: PudoReceiverPaymentConfirmRequest,
        onSuccess: (PudoReceiverPaymentConfirmResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.receiverPaymentConfirm(req)
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

    fun receiverCheckout(
        req: PudoReceiverCheckoutRequest,
        onSuccess: (LockerCheckoutResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.receiverCheckout(req)
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

    fun receiverCancel(
        path: String,
        req: CancelTransactionRequest,
        onSuccess: (PudoReceiverCancelResponse?) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.receiverCancel(path, req)
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

    fun receiverFinish(
        req: LockerFinishRequest,
        onSuccess: () -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
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

    fun bank(
        req: PudoBankRequest,
        onSuccess: (PudoBankResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.bank(req)
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

    fun addBank(
        req: PudoAddBankRequest,
        onSuccess: (PudoWalkInConsentResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.addBank(req)
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

    fun walkInParcelConfirm(
        req: PudoWalkInParcelConfirmRequest,
        onSuccess: (PudoRevieverResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.walkInParcelConfirm(req)
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

    fun provinces(
        req: PudoLocationRequest,
        onSuccess: (PudoLocationResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.provinces(req)
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

    fun subDistrict(
        req: PudoLocationRequest,
        onSuccess: (PudoLocationResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.subDistrict(req)
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

    fun district(
        req: PudoLocationRequest,
        onSuccess: (PudoLocationResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.district(req)
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

    fun addReceiver(
        req: PudoSubmitRecieverRequest,
        onSuccess: (PudoRevieverResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.addReceiver(req)
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

    fun walkinReceiverSelect(
        req: PudoSelectLocationTypeRequest,
        onSuccess: (PudoSelectLocationTypeResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.walkinReceiverSelect(req)
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

    fun walkinPickupConfirm(
        req: PudoWalkInPickupConfirmRequest,
        onSuccess: (PudoWalkInPickupConfirmResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)

        val obj = JsonObject()
        obj.addProperty("generalprofile_id", req.generalprofile_id)
        obj.addProperty("txn", req.txn)
        obj.addProperty("type", req.type)
        if (req.pickup_generalprofile_id != null) {
            obj.addProperty("pickup_generalprofile_id", req.pickup_generalprofile_id)
        }

        api.walkinPickupConfirm(obj)
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

    fun walkinShipmentSelect(
        req: PudoWalkInShipmentSelectRequest,
        onSuccess: (PudoWalkInAddressDetailResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.walkinShipmentSelect(req)
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

    fun walkInShipmentConfirm(
        req: PaymentMethodRequest,
        onSuccess: (PudoWalkinWayBillResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.shipmentConfirm(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess(it.body()!!.info)
//                        onSuccess()
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

    fun walkinConfirm(
        req: PudoReceiverPaymentConfirmRequest,
        onSuccess: (PudoWalkInPaymentConfirmResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.walkinConfirm(req)
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

    fun walkinCheckout(
        req: LockerCheckoutRequest,
        onSuccess: (LockerCheckoutResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.walkinCheckout(req)
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

    fun waybillWalkin(
        req: PudoWayBillRequest,
        onSuccess: (PudoWalkinWayBillResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.waybillWalkin(req)
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

    fun waybill(
        req: PudoWayBillRequest,
        onSuccess: (PudoWayBillResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.waybill(req)
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

    fun walkInCallback(
        req: LockerFinishRequest,
        onSuccess: () -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.walkInCallback(req)
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

    fun waybillWalkinScan(
        req: PudoWayBillScanRequest,
        onSuccess: (PudoReceiverPaymentResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.waybillWalkinScan(req)
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

    fun senderDetail(
        req: PudoSenderConfirmRequest,
        onSuccess: (PudoParcelAddressResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderDetail(req)
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

    fun senderWaybill(
        req: PudoWayBillRequest,
        onSuccess: (PudoWalkinWayBillResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.senderWaybill(req)
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

    fun waybillSenderScan(
        req: PudoWayBillScanRequest,
        onSuccess: (PudoSenderConfirmResponse) -> Unit,
        onFailure: (HttpResponse<Any>) -> Unit
    ) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.waybillSenderScan(req)
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

    fun senderReOpen(req: CpReOpenRequest, onSuccess: (CpParcelResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
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

    fun walkinReOpen(req: CpReOpenRequest, onSuccess: (CpParcelResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(PudoAPI::class.java)
        api.walkinReOpen(req)
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
