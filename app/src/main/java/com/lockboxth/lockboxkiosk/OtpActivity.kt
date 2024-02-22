package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.http.model.personal.ConfirmRegisterMemberRequest
import com.lockboxth.lockboxkiosk.http.model.personal.ConfirmRegisterMemberResponse
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyOtpRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoConfirmRegisterMemberRequest
import com.lockboxth.lockboxkiosk.http.repository.PersonalRepository
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_otp.*

class OtpActivity : BaseActivity() {

    private var otpText: String = ""
    private var otpResponse: ConfirmRegisterMemberResponse? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        btnReset.setOnClickListener {
            otpText = ""
            displayText()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        requestOtp()

        for (i in 0..9) {
            val etId = "btn${i}"
            val resID = resources.getIdentifier(etId, "id", packageName)
            val btn = findViewById<View>(resID)
            btn.setOnClickListener {
                onNumberPress(i.toString())
            }
        }

        btnDelete.setOnClickListener {
            if (otpText.isNotEmpty()) {
                otpText = otpText.substring(0, otpText.length - 1)
            }
            displayText()
        }

        tvResendOtp.setOnClickListener {
            requestOtp()
        }

        btnConfirm.setOnClickListener {
            if (otpText.length == 6) {
                showProgressDialog()
                val req = VerifyOtpRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, otpText)
                when (appPref.currentTransactionType) {
                    TransactionType.PUDO_SENDER_WALKIN -> {
                        PudoRepository.getInstance().verifyOtp(
                            req,
                            onSuccess = {
                                hideProgressDialog()
                                setResult(RESULT_OK)
                                finish()
                            },
                            onFailure = { error ->
                                showMessage(error.message!!)
                                hideProgressDialog()
                            }
                        )
                    }
                    else -> {
                        PersonalRepository.getInstance().verifyOtp(
                            req,
                            onSuccess = {
                                hideProgressDialog()
                                setResult(RESULT_OK)
                                finish()
                            },
                            onFailure = { error ->
                                showMessage(error)
                                hideProgressDialog()
                            }
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun requestOtp() {
        tvResendOtp.visibility = View.GONE
        otpText = ""
        displayText()
        showProgressDialog()
        val req = ConfirmRegisterMemberRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, appPref.currentLanguage)
        if (appPref.currentTransactionType == TransactionType.PUDO_SENDER_WALKIN) {
            PudoRepository.getInstance().requestOtp(
                req,
                onSuccess = { resp ->
                    onOtpResponse(resp)
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error.message!!, onAcceptClick = {
                        finish()
                    })

                }
            )
        } else {
            PersonalRepository.getInstance().confirmRegisterMember(
                req,
                onSuccess = { resp ->
                    onOtpResponse(resp)
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error, onAcceptClick = {
                        finish()
                    })
                }
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onOtpResponse(resp: ConfirmRegisterMemberResponse) {
        otpResponse = resp
        tvPhoneDesc.text = String.format(getString(R.string.otp_phone), resp.phone)
        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000
                tvOtpCountDown.text = "$second ${getString(R.string.sec)}"
            }

            override fun onFinish() {
                tvOtpCountDown.text = "0 ${getString(R.string.sec)}"
                tvResendOtp.visibility = View.VISIBLE
            }
        }
        timer.start()
        hideProgressDialog()
    }

    private fun onNumberPress(number: String) {
        if (otpText.length < 6) {
            otpText += number
            displayText()
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun displayText() {

        val otp = otpText.toCharArray()
        for (i in 1..6) {
            val etId = "et${i}"
            val resID = resources.getIdentifier(etId, "id", packageName)
            val et = findViewById<EditText>(resID)
            et.setText("")

            if (otp.count() >= i) {
                et.setText("•")
            }

            println(i)
        }
    }


}