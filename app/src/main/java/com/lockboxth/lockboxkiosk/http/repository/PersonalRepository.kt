package com.lockboxth.lockboxkiosk.http.repository

import android.annotation.SuppressLint
import android.util.Log
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.NetService
import com.lockboxth.lockboxkiosk.http.api.PersonalAPI
import com.lockboxth.lockboxkiosk.http.model.personal.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class PersonalRepository {

    companion object {
        private var instance: PersonalRepository? = null
            get() {
                if (field == null) {
                    synchronized(PersonalRepository::class.java) {
                        if (field == null) {
                            field = PersonalRepository()
                        }
                    }
                }
                return field
            }

        @JvmName("getInstance1")
        fun getInstance(): PersonalRepository {
            return instance!!
        }
    }

    fun verifyIn(req: VerifyInPersonalRequest, onSuccess: (VerifyInPersonalResponse) -> Unit, onFailure: (HttpResponse<*>) -> Unit) {
        val api = NetService.getRetrofit().create(PersonalAPI::class.java)
        api.verifyIn(req)
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
                onFailure(HttpResponse(status = false, code = "ERROR", message = "an error occurred, please try again later.", {}))
            })
    }

    fun contentPDPA(lang: String = "th", onSuccess: (PDPAContentResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(PersonalAPI::class.java)
        api.contentPDPA(lang)
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

    fun confirmRegisterMember(req: ConfirmRegisterMemberRequest, onSuccess: (ConfirmRegisterMemberResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(PersonalAPI::class.java)
        api.confirmRegisterMember(req)
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

    fun verifyOtp(req: VerifyOtpRequest, onSuccess: (VerifyOtpResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(PersonalAPI::class.java)
        api.verifyOtp(req)
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
                        if (it.body()!!.code!! == "OTP_FAIL") {
                            onFailure(it.body()!!.code!!)
                        } else {
                            onFailure(it.body()!!.message!!)
                        }
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

    fun verifyFinish(req: VerifyFinishRequest, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(PersonalAPI::class.java)
        api.verifyFinish(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess()
                    } else {
                        val message = "${it.body()!!.code} : ${it.body()!!.message}"
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

    fun verifyOut(req: VerifyOutPersonalRequest, onSuccess: (VerifyOutPersonalResponse) -> Unit, onFailure: (HttpResponse<*>) -> Unit) {
        val api = NetService.getRetrofit().create(PersonalAPI::class.java)
        api.verifyOut(req)
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
                onFailure(HttpResponse(status = false, code = "ERROR", message = "an error occurred, please try again later.", {}))
            })
    }

    fun verifyOut(req: VerifyFinishRequest, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(PersonalAPI::class.java)
        api.verifyOut(req)
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

    fun verifyBooking(req: VerifyBookingRequest, onSuccess: (VerifyBookingResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(PersonalAPI::class.java)
        api.verifyBooking(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess(it.body()!!.info)
                    } else {
                        onFailure(it.body()!!.code!!)
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

    fun bookingFinish(req: VerifyBookingFinishRequest, onSuccess: (VerifyBookingFinishResponse) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(PersonalAPI::class.java)
        api.bookingFinish(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    if (it.body()!!.status) {
                        onSuccess(it.body()!!.info)
                    } else {
                        onFailure(it.body()!!.code!!)
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