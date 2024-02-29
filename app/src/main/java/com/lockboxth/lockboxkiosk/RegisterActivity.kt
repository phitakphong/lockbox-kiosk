package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.customdialog.*
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.go.GoLockerSelectResponse
import com.lockboxth.lockboxkiosk.http.model.go.GoReceiverVerifyRequest
import com.lockboxth.lockboxkiosk.http.model.go.GoSenderVerifyRequest
import com.lockboxth.lockboxkiosk.http.model.locker.LockerBookingRecheckRequest
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyInPersonalRequest
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyOutPersonalRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoVerifyMemberRequest
import com.lockboxth.lockboxkiosk.http.model.topup.TopupVerifyRequest
import com.lockboxth.lockboxkiosk.http.repository.*
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity() {

    var dialCode: String = "+66"
    var countryCode: String = "th"
    var phoneNumber: String = ""
    var isResume = false

    var hasPdpa = false
    var verifyType = ""

    var isGoOut = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        Util.loadCountryFlag(countryCode, imgCountryFlag)

        selectCountryCode.setOnClickListener {
            val intent = Intent(this@RegisterActivity, SearchCountryCodeActivity::class.java)
            intent.putExtra("dial_code", this.dialCode)
            resultLauncher.launch(intent)
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        tvTitle2.visibility = View.GONE

        for (i in 0..9) {
            val etId = "btn${i}"
            val resID = resources.getIdentifier(etId, "id", packageName)
            val btn = findViewById<View>(resID)
            btn.setOnClickListener {
                onNumberPress(i.toString())
            }
        }

        btnReset.setOnClickListener {
            onNumberPress("")
        }

        btnDelete.setOnClickListener {
            if (phoneNumber.isNotEmpty()) {
                phoneNumber = phoneNumber.substring(0, phoneNumber.length - 1)
            }
            displayText()
        }

        initUiActivity()

    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            dialCode = data.getStringExtra("dial_code")!!
            countryCode = data.getStringExtra("code")!!
            Util.loadCountryFlag(countryCode, imgCountryFlag)
            tvDialCode.text = dialCode
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initUiActivity() {
        when (appPref.currentTransactionType) {
            TransactionType.IN -> {
                initIn()
            }
            TransactionType.OUT -> {
                initOut()
            }
            TransactionType.TOPUP -> {
                initTopup()
            }
            TransactionType.BOOKING -> {
                initBooking()
            }
            TransactionType.PUDO_SENDER_WALKIN -> {
                initPudoWalkin()
            }
            TransactionType.GO_IN -> {
                isGoOut = intent.getBooleanExtra("isGoOut", false)
                initGoIn()
            }
            else -> {}
        }

    }

    @SuppressLint("SetTextI18n")
    private fun initIn() {
        tvTitle.text = getString(R.string.require_phone_number)
        btnConfirm.setOnClickListener {
            val phoneNumber = getPhoneNumber()
            if (phoneNumber.isNotEmpty()) {
                showProgressDialog()
                PersonalRepository.getInstance().verifyIn(
                    VerifyInPersonalRequest(appPref.kioskInfo!!.generalprofile_id, phoneNumber, appPref.currentTransactionId!!),
                    onSuccess = { resp ->
                        hideProgressDialog()
                        verifyType = resp.verify_type
                        hasPdpa = resp.has_pdpa
                        if (!resp.is_member) {
                            ConfirmRegisterDialog.newInstance(
                                resId = R.layout.dialog_confirm_register
                            ).apply {
                                onOkClickListener = {
                                    val intent = Intent(this@RegisterActivity, OtpActivity::class.java)
                                    otpResultLauncher.launch(intent)
                                }
                                timeoutCallback = {
                                    onCancel()
                                }
                                onCancelClickListener = {
                                    val intent = Intent(this@RegisterActivity, ConsentActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }.run {
                                show(supportFragmentManager, "")
                            }
                        } else {
                            nextActivity()
                        }
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        handleError(error)
                    }
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initPudoWalkin() {
        tvTitle.text = getString(R.string.require_phone_number)
        btnConfirm.setOnClickListener {
            val phoneNumber = getPhoneNumber()
            if (phoneNumber.isNotEmpty()) {
                showProgressDialog()
                PudoRepository.getInstance().verifyMember(
                    PudoVerifyMemberRequest(appPref.kioskInfo!!.generalprofile_id, phoneNumber, appPref.currentLanguage),
                    onSuccess = { resp ->
                        hideProgressDialog()
                        hasPdpa = resp.has_pdpa
                        appPref.currentTransactionId = resp.txn
                        if (!resp.is_member) {
                            ConfirmRegisterDialog.newInstance(
                                resId = R.layout.dialog_confirm_register_pudo
                            ).apply {
                                onOkClickListener = {
                                    val intent = Intent(this@RegisterActivity, OtpActivity::class.java)
                                    otpResultLauncher.launch(intent)
                                }
                                timeoutCallback = {
                                    onCancel()
                                }
                                onCancelClickListener = {
                                    goToMainActivity()
                                }
                            }.run {
                                show(supportFragmentManager, "")
                            }
                        } else {
                            nextActivity()
                        }
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        handleError(error)
                    }
                )
            }
        }
    }

    private fun initBooking() {
        tvTitle.text = getString(R.string.registered_phone_number)
        btnConfirm.setOnClickListener {
            val phoneNumber = getPhoneNumber()
            if (phoneNumber.isNotEmpty()) {
                showProgressDialog()

                val req = LockerBookingRecheckRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, appPref.currentBookingId!!, phoneNumber)
                LockerRepository.getInstance().bookingRecheck(
                    req,
                    onSuccess = { resp ->
                        hideProgressDialog()
                        verifyType = resp.verify_type
                        hasPdpa = resp.has_pdpa
                        nextActivity()
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        handleError(error)
                    }
                )
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun initOut() {
        tvTitle.text = getString(R.string.registered_phone_number)
        btnConfirm.setOnClickListener {
            val phoneNumber = getPhoneNumber()
            if (phoneNumber.isNotEmpty()) {
                showProgressDialog()
                val req = VerifyOutPersonalRequest(appPref.kioskInfo!!.generalprofile_id, phoneNumber)
                PersonalRepository.getInstance().verifyOut(
                    req,
                    onSuccess = { resp ->
                        hideProgressDialog()
                        appPref.currentTransactionId = resp.txn
                        hasPdpa = true
                        verifyType = resp.verify_type
                        nextActivity()

                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        handleError(error)
                    }
                )
            }
        }
    }

    private fun initTopup() {
        tvTitle.text = getString(R.string.registered_phone_number)
        btnConfirm.setOnClickListener {
            val phoneNumber = getPhoneNumber()
            if (phoneNumber.isNotEmpty()) {
                showProgressDialog()
                val req = TopupVerifyRequest(appPref.kioskInfo!!.generalprofile_id, phoneNumber)
                TopupRepository.getInstance().verify(
                    req,
                    onSuccess = { resp ->
                        hideProgressDialog()
                        appPref.currentTransactionId = resp.txn
                        val intent = Intent(this@RegisterActivity, TopupActivity::class.java)
                        intent.putExtra("member", resp.member)
                        startActivity(intent)

                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        handleError(error)
                    }
                )
            }

        }
    }

    private fun initGoIn() {
        tvTitle2.text = getString(R.string.go_in)
        tvTitle2.visibility = View.VISIBLE
        tvTitle2.setTextColor(ContextCompat.getColor(this, R.color.sender))
        tvTitle0.text = getString(R.string.go_phone_number)
        if(isGoOut){
            tvTitle2.text = getString(R.string.go_out)
            tvTitle2.setTextColor(ContextCompat.getColor(this, R.color.receiver))
        }
        btnConfirm.setOnClickListener {
            showProgressDialog()
            if (!isGoOut) {
                GoRepository.getInstance().goDropVerify(
                    GoSenderVerifyRequest(appPref.kioskInfo!!.generalprofile_id, getPhoneNumber(), appPref.currentLanguage),
                    onSuccess = { resp ->
                        appPref.currentTransactionId = resp.txn
                        hideProgressDialog()
                        isGoOut = true
                        onNumberPress("")
                        tvTitle2.text = getString(R.string.go_out)
                        tvTitle2.setTextColor(ContextCompat.getColor(this, R.color.receiver))
                    },
                    onFailure = { err ->
                        hideProgressDialog()
                        showMessage(err)
                    }
                )
            } else {
                GoRepository.getInstance().goDropReceiverVerify(
                    GoReceiverVerifyRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, getPhoneNumber()),
                    onSuccess = { resp ->
                        hideProgressDialog()
                        if (resp.exist_locker) {

                            ServiceFeeSummaryActivity.goDropSummary = GoLockerSelectResponse(
                                sender_phone = resp.sender_phone!!,
                                receiver_phone = resp.receiver_phone!!,
                                is_ap = resp.is_ap!!,
                                amount = resp.amount!!,
                                discount = resp.discount!!,
                                fee = resp.fee!!,
                                time_limit = resp.time_limit!!,
                                details = resp.details!!
                            )

                            val intent = Intent(this@RegisterActivity, ServiceFeeSummaryActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this@RegisterActivity, SelectLockBoxActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    },
                    onFailure = { err ->
                        hideProgressDialog()
                        showMessage(err)
                    }
                )
            }


        }
    }

    var otpResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            nextActivity()
        }
    }

    private fun handleError(error: HttpResponse<*>) {

        when (error.code) {
            "PHONE_INVALID" -> {
                CustomDialog.newInstance(R.layout.dialog_require_phone_number).apply {
                    timeoutCallback = {
                        onCancel()
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
            "MEMBER_NOT_FOUND" -> {
                if (appPref.currentTransactionType == TransactionType.TOPUP) {
                    RequireRegisterDialog.newInstance().apply {
                        timeoutCallback = {
                            onCancel()
                        }
                    }.run {
                        show(supportFragmentManager, "")
                    }
                } else if (appPref.currentTransactionType == TransactionType.PUDO_CP_DROP || appPref.currentTransactionType == TransactionType.PUDO_CP_PICKUP) {
                    RequireRegister2Dialog.newInstance().apply {
                        timeoutCallback = {
                            onCancel()
                        }
                    }.run {
                        show(supportFragmentManager, "")
                    }
                } else {
                    RequireRegister3Dialog.newInstance().apply {
                        timeoutCallback = {
                            onCancel()
                        }
                    }.run {
                        show(supportFragmentManager, "")
                    }

                }
            }
            else -> {
                showMessage(error.message!!)
            }
        }

    }


    @JvmName("getPhoneNumber1")
    private fun getPhoneNumber(): String {
        if (phoneNumber.isEmpty()) {
            CustomDialog.newInstance(R.layout.dialog_require_phone_number).apply {
                onOkClickListener = { }
                timeoutCallback = {
                    onCancel()
                }
            }.run {
                show(supportFragmentManager, "")
            }
            return ""
        }

        if (countryCode.lowercase() == "th" && phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.substring(1)
        }

        return "$dialCode$phoneNumber"

    }

    private fun onNumberPress(number: String) {
        if (number.isEmpty()) {
            phoneNumber = ""
        } else {
            phoneNumber += number
        }
        displayText()
    }

    private fun displayText() {
        etPhoneNo.setText(phoneNumber)
    }

    private fun nextActivity() {

        appPref.currentVerifyType = verifyType
        if (hasPdpa) {
            when (appPref.currentTransactionType) {
                TransactionType.PUDO_SENDER_WALKIN -> {
                    val intent = Intent(this@RegisterActivity, PudoCardIdentifyActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    if (verifyType == "rfid") {
                        val intent = Intent(this@RegisterActivity, RfidVerifyActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@RegisterActivity, LockerPasswordActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        } else {
            val intent = Intent(this@RegisterActivity, ConsentActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onResume() {
        super.onResume()
        if (!isResume) {
            setTimeoutMinute(5, tvCountdown) {
                onCancel()
            }
            isResume = true
        } else {
            timerResume()
        }
    }

}
