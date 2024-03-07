package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.view.View
import com.lockboxth.lockboxkiosk.customdialog.PaymentTimeoutDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lockboxth.lockboxkiosk.http.model.go.GoPaymentCancelRequest
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionRequest
import com.lockboxth.lockboxkiosk.http.repository.GoRepository
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_qr_code_payment.*

class QrCodePaymentActivity : BaseActivity() {

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {

        allowOpenLocker = true

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_payment)

        val timer = intent.getIntExtra("timer", 0)
        val timeLimit = intent.getIntExtra("time_limit", 0)
        val paymentMethod = intent.getStringExtra("payment_method")
        val paymentMethodDisplay = intent.getStringExtra("payment_method_display")
        val amountPay = intent.getStringExtra("amount_pay")
        val qrcode = intent.getStringExtra("qrcode")

        if (timeLimit != 0) {
            tvTimeLimit.text = "***  ค่าบริการขั้นต่ำ $timeLimit ชั่วโมง  ***"
        }
        if (paymentMethodDisplay != null) {
            tvChannel.text = paymentMethodDisplay
        }
        if (amountPay != null) {
            tvTotal.text = "$amountPay บาท"
        }

        if (appPref.currentTransactionType == TransactionType.OUT || appPref.currentTransactionType == TransactionType.TOPUP) {
            tvTimeLimit.visibility = View.GONE
        }

        if (qrcode != null) {
            val imageByteArray: ByteArray = Base64.decode(qrcode.replace("data:image/png;base64,", ""), Base64.DEFAULT)
            Glide.with(this@QrCodePaymentActivity)
                .load(imageByteArray)
                .into(imgQrCode);
        }

        when (paymentMethod!!) {
            "wallet" -> {
                tvTimeLimit.visibility = View.GONE
                tvDesc.visibility = View.VISIBLE
                tvDesc.text = getString(R.string.use_app_scan_qr)
            }
            "ksher credit card" -> {
                tvDesc.visibility = View.VISIBLE
                tvDesc.text = getString(R.string.use_camera_payment)
            }
            "crash" -> {
                tvDesc.visibility = View.VISIBLE
                tvDesc.text = getString(R.string.please_payment)
            }
            else -> {
                tvTimeLimit.visibility = View.GONE
            }
        }

        btnBack.setOnClickListener {
            when (appPref.currentTransactionType) {
                TransactionType.PUDO_SENDER_WALKIN,
                TransactionType.PUDO_RECEIVER -> {
                    pudoBack("back")
                }
                TransactionType.GO_OUT -> {
                    onBackPressed()
                }
                else -> {
                    onBack(appPref.currentTransactionType!!) {
                        onBackPressed()
                    }
                }
            }
        }

        setTimeoutMinute(timer, tvCountdown, timeoutCallback = {
            when (appPref.currentTransactionType) {
                TransactionType.OUT,
                TransactionType.TOPUP,
                TransactionType.IN -> {
                    PaymentTimeoutDialog.newInstance().apply {
                        onOkClickListener = {
                            onBack(appPref.currentTransactionType!!, "timeout") {
                                onBackPressed()
                            }
                        }
                        timeoutCallback = {
                            onCancel()
                        }
                    }.run {
                        show(supportFragmentManager, "")
                    }
                }
                TransactionType.GO_OUT -> {
                    PaymentTimeoutDialog.newInstance().apply {
                        onOkClickListener = {
                            onTimeout()
                        }
                        timeoutCallback = {
                            onTimeout()
                        }
                    }.run {
                        show(supportFragmentManager, "")
                    }
                }
                else -> {
                    PaymentTimeoutDialog.newInstance().apply {
                        onOkClickListener = {
                            pudoBack("timeout")
                        }
                        timeoutCallback = {
                            onCancel()
                        }
                    }.run {
                        show(supportFragmentManager, "")
                    }
                }

            }
        })
    }

    private fun onTimeout() {
        showProgressDialog()
        when (appPref.currentTransactionType) {
            TransactionType.GO_OUT -> {
                GoRepository.getInstance().pickupCancel(
                    GoPaymentCancelRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, "timeout"),
                    onSuccess = {
                        hideProgressDialog()
                        goToMainActivity()
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        showMessage(error)
                    }
                )
            }
            else -> {
                GoRepository.getInstance().pickupCancel(
                    GoPaymentCancelRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, "timeout"),
                    onSuccess = {
                        hideProgressDialog()
                        goToMainActivity()
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        showMessage(error)
                    }
                )
            }
        }
    }

    private fun pudoBack(eventType: String) {
        showProgressDialog()

        val path = when (appPref.currentTransactionType) {
            TransactionType.PUDO_RECEIVER -> {
                "receiver"
            }
            else -> {
                "walkin"
            }
        }
        PudoRepository.getInstance().receiverCancel(
            path,
            CancelTransactionRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, eventType),
            onSuccess = { resp ->
                if (resp != null) {
                    val cmdJson = Gson().toJson(resp.locker_commands)
                    openLocker(resp.locker_no!!, resp.event_type!!, cmdJson)
                } else {
                    if (eventType == "timeout") {
                        goToMainActivity()
                    } else {
                        onBackPressed()
                    }
                }
            },
            onFailure = { error ->
                hideProgressDialog()
                if (error.code == "NO_PAY") {
                    if (appPref.currentTransactionType == TransactionType.PUDO_SENDER_WALKIN) {
                        goToMainActivity()
                    } else {
                        onBackPressed()
                    }
                } else {
                    showMessage(error.message!!)
                }
            }
        )
    }

}