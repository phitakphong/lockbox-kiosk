package com.lockboxth.lockboxkiosk.http.repository

import android.annotation.SuppressLint
import android.util.Log
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.NetService
import com.lockboxth.lockboxkiosk.http.api.PaymentAPI
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodRequest
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class PaymentRepository {

    companion object {
        private var instance: PaymentRepository? = null
            get() {
                if (field == null) {
                    synchronized(PaymentRepository::class.java) {
                        if (field == null) {
                            field = PaymentRepository()
                        }
                    }
                }
                return field
            }

        @JvmName("getInstance1")
        fun getInstance(): PaymentRepository {
            return instance!!
        }
    }

    fun paymentMethod(req: PaymentMethodRequest, onSuccess: (ArrayList<PaymentMethodResponse>) -> Unit, onFailure: (String) -> Unit) {
        val api = NetService.getRetrofit().create(PaymentAPI::class.java)
        api.paymentMethod(req)
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