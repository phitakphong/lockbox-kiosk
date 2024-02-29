package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.customdialog.ConfirmCancelPuduSendDialog
import com.lockboxth.lockboxkiosk.customdialog.CustomDialog
import com.lockboxth.lockboxkiosk.customdialog.ProgressDialog
import com.lockboxth.lockboxkiosk.customdialog.ReOpenFailDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.adidas.AdidasDropCallbackRequest
import com.lockboxth.lockboxkiosk.http.model.adidas.AdidasDropFinishRequest
import com.lockboxth.lockboxkiosk.http.model.cp.CpReOpenRequest
import com.lockboxth.lockboxkiosk.http.model.go.GoDropCallbackRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderCallbackRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderFinishRequest
import com.lockboxth.lockboxkiosk.http.repository.AdidasRepository
import com.lockboxth.lockboxkiosk.http.repository.CpRepository
import com.lockboxth.lockboxkiosk.http.repository.GoRepository
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import com.lockboxth.lockboxkiosk.service.hardware.HardwareService
import kotlinx.android.synthetic.main.activity_pudo_sender_order_confirm.*

class PudoSenderOrderConfirmActivity : BaseActivity() {

    private var currentState: String? = null
    private var isCreate = true

    companion object {
        var currentLockerName: String = ""
        var currentLockerNo: Int = 0
        var currentOpenCommand: String = ""
        var currentInQuireCommand: String = ""
        var isOpenCommand = true
        var findSuccess = false
        var currentTransactionId = ""
        var isChangeRequest = false
        var d: ProgressDialog? = null
        var using = false
    }

    var path = ""

    private var hardwareService: HardwareService? = HardwareService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_sender_order_confirm)

        currentTransactionId = appPref.currentTransactionId ?: ""

        btnSenderConfirm.setOnClickListener {
            clearState()
            setOnClick(it.tag.toString())
        }

        btnSenderChange.setOnClickListener {
            clearState()
            setOnClick(it.tag.toString())
        }

        btnSenderCancel.setOnClickListener {
            clearState()
            setOnClick(it.tag.toString())
        }

        btnSenderNew.setOnClickListener {
            clearState()
            setOnClick(it.tag.toString())
        }

        using = true

        hardwareService?.setOnDataReceived { s ->
            if (!using) {
                return@setOnDataReceived
            }
            Log.d("OPENZ", "isOpenCommand : " + isOpenCommand.toString())
            if (!isOpenCommand) {
                findSuccess = true
                var isOpen = false
                try {
                    val status: String = s.substring(7, 11)
                    var arrStatus: CharArray = hardwareService!!.hex2binary(status)!!
                    arrStatus = hardwareService!!.reverse(arrStatus)!!

                    var block: Int = currentLockerNo - 1
                    block %= 8
                    isOpen = arrStatus[block] == '0'
                    Log.d("OPENZ", "isOpen : " + isOpen.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("OPENZ", "CATCH")
                }

                Log.d("OPENZ", "isChangeRequest : " + isChangeRequest.toString())

                if (!isChangeRequest) {
                    if (appPref.currentTransactionType == TransactionType.PUDO_CP_DROP) {
                        openSuccess(isOpen)
                    } else {
                        if (isOpen) {
                            openSuccess(true)
                        } else {
                            changeLocker()
                        }
                    }
                } else {
                    if (isOpen) {
                        CustomDialog.newInstance(R.layout.dialog_pudo_cp_alert).apply {
                            onOkClickListener = {

                            }
                            timeoutCallback = {

                            }
                        }.run {
                            Log.d("OPENZ", "hideProgressDialog()")
                            show(supportFragmentManager, "")
                        }
                    } else {
                        submit("change")
                    }
                }

            }
        }

        showProgressDialog(getString(R.string.search_locker))
        val cmdObj = JsonObject()
        cmdObj.addProperty("open", currentOpenCommand)
        cmdObj.addProperty("inquire", currentInQuireCommand)
        val obj = JsonObject()
        obj.add(currentLockerNo.toString(), cmdObj)
        openLocker(currentLockerName, obj)

        btnConfirm.setOnClickListener {

            Log.d("sss", currentState ?: "xxxx")

            when (currentState) {
                "Confirm" -> {
                    isChangeRequest = false
                    submit("drop")
                }
                "Change" -> {
                    isChangeRequest = true
                    isOpenCommand = false
                    findSuccess = false
                    hardwareService!!.writeCommand(currentInQuireCommand)
                    showProgressDialog(getString(R.string.search_locker))
                    Handler(Looper.getMainLooper()).postDelayed({
                        hideProgressDialog()
                        if (!findSuccess) {
                            showMessage(getString(R.string.error_ex))
                        }
                        clearState()
                    }, 1500)
                }
                "Cancel" -> {
                    val hideSub =
                        appPref.currentTransactionType == TransactionType.GO_IN || appPref.currentTransactionType == TransactionType.PUDO_CP_DROP || appPref.currentTransactionType == TransactionType.ADIDAS_DROP
                    ConfirmCancelPuduSendDialog.newInstance().apply {
                        hideSubtitle = hideSub
                        onOkClickListener = {
                            submit("cancel")
                        }
                        timeoutCallback = {
                            onCancel()
                        }
                    }.run {
                        show(supportFragmentManager, "")
                    }
                }
                "New" -> {
                    val intent = Intent(this@PudoSenderOrderConfirmActivity, PudoSenderOrderConfirmNewActivity::class.java)
                    startActivity(intent)
                }
                else -> {

                }
            }
        }

        when (appPref.currentTransactionType) {
            TransactionType.ADIDAS_DROP,
            TransactionType.PUDO_CP_DROP,
            TransactionType.GO_IN -> {
                btnSenderChange.visibility = View.GONE
            }
            else -> {
                btnSenderChange.visibility = View.VISIBLE
            }
        }

        when (appPref.currentTransactionType) {
            TransactionType.ADIDAS_DROP,
            TransactionType.GO_IN,
            TransactionType.PUDO_CP_DROP,
            TransactionType.PUDO_SENDER_WALKIN,
            TransactionType.PUDO_SENDER -> {
                layoutReopen.visibility = View.VISIBLE
            }
            else -> {
                layoutReopen.visibility = View.GONE
            }
        }

        when (appPref.currentTransactionType) {
            TransactionType.GO_IN -> {
                tvTitle.text = getString(R.string.put_loocker2)
                layoutHint.visibility = View.VISIBLE
            }
            else -> {
                layoutHint.visibility = View.GONE
                tvTitle.text = getString(R.string.put_loocker)
            }
        }

        when (appPref.currentTransactionType) {
            TransactionType.GO_IN -> {
                btnSenderNew.visibility = View.VISIBLE
            }
            else -> {
                btnSenderNew.visibility = View.GONE
            }
        }

        btnReOpen.setOnClickListener {
            reOpen()
        }
    }

    private fun setOnClick(tag: String) {

        val colorWhite = ContextCompat.getColor(this, R.color.white)

        val background = when (tag) {
            "Confirm" -> ContextCompat.getDrawable(this, R.drawable.btn_item_blue)
            "Change" -> ContextCompat.getDrawable(this, R.drawable.btn_item_yellow)
            "Cancel" -> ContextCompat.getDrawable(this, R.drawable.btn_item_red)
            "New" -> ContextCompat.getDrawable(this, R.drawable.btn_item_green)
            else -> {
                ContextCompat.getDrawable(this, R.drawable.card_item_white_border_gray_radius_16)
            }
        }
        val btnId = resources.getIdentifier("btnSender${tag}", "id", packageName)
        val imgId = resources.getIdentifier("imgSender${tag}", "id", packageName)
        val tvId = resources.getIdentifier("tvSender${tag}", "id", packageName)

        val btn = findViewById<LinearLayoutCompat>(btnId)
        btn.background = background

        val img = findViewById<ImageView>(imgId)
        img.setColorFilter(colorWhite)

        val tv = findViewById<TextView>(tvId)
        tv.setTextColor(colorWhite)

        currentState = tag

    }

    private fun clearState() {

        val colorBlack = ContextCompat.getColor(this, R.color.black)
        val defaultBackground = ContextCompat.getDrawable(this, R.drawable.card_item_white_border_gray_radius_16)

        btnSenderConfirm.background = defaultBackground
        imgSenderConfirm.setColorFilter(colorBlack)
        tvSenderConfirm.setTextColor(colorBlack)

        btnSenderChange.background = defaultBackground
        imgSenderChange.setColorFilter(colorBlack)
        tvSenderChange.setTextColor(colorBlack)

        btnSenderCancel.background = defaultBackground
        imgSenderCancel.setColorFilter(colorBlack)
        tvSenderCancel.setTextColor(colorBlack)

        btnSenderNew.background = defaultBackground
        imgSenderNew.setColorFilter(colorBlack)
        tvSenderNew.setTextColor(colorBlack)

        currentState = null

    }

    private fun openSuccess(isOpen: Boolean) {

        val result = JsonObject()
        result.addProperty(currentLockerNo.toString(), isOpen)

        if (appPref.currentTransactionType == TransactionType.PUDO_CP_DROP) {
            val req = PudoSenderCallbackRequest(
                appPref.kioskInfo!!.generalprofile_id,
                appPref.currentTransactionId ?: "",
                "cp_courier",
                result
            )
            CpRepository.getInstance().senderCallbackSuccess(
                req,
                onSuccess = { resp ->
                    hideProgressDialog()
                    currentLockerName = resp.locker_no
                    setTimeoutMinute(5, tvCountdown) {
                        submit("auto_drop")
                    }
                },
                onFailure = { error ->
                    hideProgressDialog()
                    handleError(error)
                }
            )
        } else if (appPref.currentTransactionType == TransactionType.GO_IN) {
            val req = GoDropCallbackRequest(
                generalprofile_id = appPref.kioskInfo!!.generalprofile_id,
                txn = appPref.currentTransactionId ?: "",
                locker_results = result
            )
            GoRepository.getInstance().goDropCallback(
                req,
                onSuccess = { resp ->
                    hideProgressDialog()
                    currentLockerName = resp.locker_no
                    setTimeoutMinute(5, tvCountdown) {
                        submit("auto_drop")
                    }
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error)
                }
            )
        } else if (appPref.currentTransactionType == TransactionType.ADIDAS_DROP) {
            val req = AdidasDropCallbackRequest(
                generalprofile_id = appPref.kioskInfo!!.generalprofile_id,
                txn = appPref.currentTransactionId ?: "",
                locker_results = result
            )
            AdidasRepository.getInstance().dropCallback(
                req,
                onSuccess = { resp ->
                    hideProgressDialog()
                    currentLockerName = resp.locker_no
                    setTimeoutMinute(5, tvCountdown) {
                        submit("auto_drop")
                    }
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error)
                }
            )
        } else {
            val req = PudoSenderCallbackRequest(
                appPref.kioskInfo!!.generalprofile_id,
                appPref.currentTransactionId ?: "",
                appPref.currentTransactionType.toString().lowercase(),
                result
            )
            if (appPref.currentTransactionType == TransactionType.PUDO_SENDER_WALKIN) {
                PudoRepository.getInstance().senderWalkinCallbackSuccess(
                    req,
                    onSuccess = { resp ->
                        hideProgressDialog()
                        if (!resp.have_extra_size) {
                            btnSenderChange.isEnabled = false
                            btnSenderChange.isClickable = false
                            btnSenderChange.background = ContextCompat.getDrawable(this, R.drawable.card_item_white_border_gray_radius_16_bisable)
                            tvSenderChange.setTextColor(ContextCompat.getColor(this, R.color.black))
                            imgSenderChange.setColorFilter(ContextCompat.getColor(this, R.color.black))
                        }
                        setTimeoutMinute(5, tvCountdown) {
                            submit("auto_drop")
                        }
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        handleError(error)
                    }
                )
            } else {
                PudoRepository.getInstance().senderCallbackSuccess(
                    req,
                    onSuccess = { resp ->
                        hideProgressDialog()
                        if (!resp.have_extra_size) {
                            btnSenderChange.isEnabled = false
                            btnSenderChange.isClickable = false
                            btnSenderChange.background = ContextCompat.getDrawable(this, R.drawable.card_item_white_border_gray_radius_16_bisable)
                            tvSenderChange.setTextColor(ContextCompat.getColor(this, R.color.black))
                            imgSenderChange.setColorFilter(ContextCompat.getColor(this, R.color.black))
                        }
                        setTimeoutMinute(5, tvCountdown) {
                            submit("auto_drop")
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

    private fun submit(type: String) {

        try {
            val req = PudoSenderFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, type)
            if (type == "change") {
                showProgressDialog(getString(R.string.search_locker))

                if (appPref.currentTransactionType == TransactionType.PUDO_CP_DROP) {
                    CpRepository.getInstance().senderFinishChange(
                        req,
                        onSuccess = { resp ->
                            openLocker(resp.locker_no, resp.locker_commands)
                        },
                        onFailure = { error ->
                            showMessage(error.message!!)
                            hideProgressDialog()
                        }
                    )
                } else {
                    if (appPref.currentTransactionType == TransactionType.PUDO_SENDER_WALKIN) {
                        PudoRepository.getInstance().senderWalkinFinishChange(
                            req,
                            onSuccess = { resp ->
                                openLocker(resp.locker_no, resp.locker_commands)
                            },
                            onFailure = { error ->
                                showMessage(error.message!!)
                                hideProgressDialog()
                            }
                        )
                    } else {
                        PudoRepository.getInstance().senderFinishChange(
                            req,
                            onSuccess = { resp ->
                                openLocker(resp.locker_no, resp.locker_commands)
                            },
                            onFailure = { error ->
                                showMessage(error.message!!)
                                hideProgressDialog()
                            }
                        )
                    }
                }
            } else {
                showProgressDialog()
                if (appPref.currentTransactionType == TransactionType.PUDO_CP_DROP) {
                    CpRepository.getInstance().senderFinish(
                        req,
                        onSuccess = { code ->
                            hideProgressDialog()
                            if (code == "CANCEL") {
                                CustomDialog.newInstance(R.layout.dialog_confirm_cancel_success).apply {
                                    onOkClickListener = {
                                        goToMainActivity()
                                    }
                                    timeoutCallback = {
                                        onCancel()
                                    }
                                }.run {
                                    show(supportFragmentManager, "")
                                }
                            } else {
                                val intent = Intent(this@PudoSenderOrderConfirmActivity, PaymentSuccessActivity::class.java)
                                intent.putExtra("locker_no", currentLockerName)
                                intent.putExtra("showSubtitle", false)
                                startActivity(intent)
                                finish()
                            }

                        },
                        onFailure = { error ->
                            showMessage(error.message!!)
                            hideProgressDialog()
                        }
                    )
                } else if (appPref.currentTransactionType == TransactionType.ADIDAS_DROP) {
                    AdidasRepository.getInstance().dropFinish(
                        AdidasDropFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, type),
                        onSuccess = { code ->
                            hideProgressDialog()
                            if (code == "CANCEL") {
                                CustomDialog.newInstance(R.layout.dialog_confirm_cancel_success).apply {
                                    onOkClickListener = {
                                        goToMainActivity()
                                    }
                                    timeoutCallback = {
                                        onCancel()
                                    }
                                }.run {
                                    show(supportFragmentManager, "")
                                }
                            } else {
                                val intent = Intent(this@PudoSenderOrderConfirmActivity, PaymentSuccessActivity::class.java)
                                intent.putExtra("locker_no", currentLockerName)
                                intent.putExtra("showSubtitle", false)
                                startActivity(intent)
                                finish()
                            }
                        },
                        onFailure = { error ->
                            showMessage(error.message!!)
                            hideProgressDialog()
                        }
                    )
                } else if (appPref.currentTransactionType == TransactionType.GO_IN) {
                    GoRepository.getInstance().goDropFinish(
                        req,
                        onSuccess = { code, resp ->
                            hideProgressDialog()
                            if (code == "CANCEL") {
                                CustomDialog.newInstance(R.layout.dialog_confirm_cancel_success).apply {
                                    onOkClickListener = {
                                        goToMainActivity()
                                    }
                                    timeoutCallback = {
                                        onCancel()
                                    }
                                }.run {
                                    show(supportFragmentManager, "")
                                }
                            } else {
                                val intent = Intent(this@PudoSenderOrderConfirmActivity, PaymentSuccessActivity::class.java)
                                intent.putExtra("locker_no", currentLockerName)
                                intent.putExtra("showSubtitle", false)
                                startActivity(intent)
                                finish()
                            }
                        },
                        onFailure = { error ->
                            showMessage(error.message!!)
                            hideProgressDialog()
                        }
                    )
                } else {
                    if (appPref.currentTransactionType == TransactionType.PUDO_SENDER_WALKIN) {
                        PudoRepository.getInstance().senderWalkinFinish(
                            req,
                            onSuccess = {
                                hideProgressDialog()
                                when (type) {
                                    "cancel" -> {
                                        CustomDialog.newInstance(R.layout.dialog_confirm_cancel_pudo_success).apply {
                                            onOkClickListener = {
                                                goToMainActivity()
                                            }
                                            timeoutCallback = {
                                                onCancel()
                                            }
                                        }.run {
                                            show(supportFragmentManager, "")
                                        }
                                    }
                                    "refund" -> {
                                        CustomDialog.newInstance(R.layout.dialog_confirm_pudo_refund_success).apply {
                                            message = getString(R.string.refund_to_wallet)
                                            onOkClickListener = {
                                                goToMainActivity()
                                            }
                                            timeoutCallback = {
                                                onCancel()
                                            }
                                        }.run {
                                            show(supportFragmentManager, "")
                                        }
                                    }
                                    else -> {
                                        val intent = Intent(this@PudoSenderOrderConfirmActivity, PaymentSuccessActivity::class.java)
                                        intent.putExtra("locker_no", currentLockerName)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            },
                            onFailure = { error ->
                                showMessage(error.message ?: error.code ?: "-")
                                hideProgressDialog()
                            }
                        )
                    } else {
                        PudoRepository.getInstance().senderFinish(
                            req,
                            onSuccess = {
                                hideProgressDialog()
                                when (type) {
                                    "cancel" -> {
                                        CustomDialog.newInstance(R.layout.dialog_confirm_cancel_pudo_success).apply {
                                            onOkClickListener = {
                                                goToMainActivity()
                                            }
                                            timeoutCallback = {
                                                onCancel()
                                            }
                                        }.run {
                                            show(supportFragmentManager, "")
                                        }
                                    }
                                    "refund" -> {
                                        CustomDialog.newInstance(R.layout.dialog_confirm_pudo_refund_success).apply {
                                            message = getString(R.string.refund_to_wallet)
                                            onOkClickListener = {
                                                goToMainActivity()
                                            }
                                            timeoutCallback = {
                                                onCancel()
                                            }
                                        }.run {
                                            show(supportFragmentManager, "")
                                        }
                                    }
                                    else -> {
                                        val intent = Intent(this@PudoSenderOrderConfirmActivity, PaymentSuccessActivity::class.java)
                                        intent.putExtra("locker_no", currentLockerName)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            },
                            onFailure = { error ->
                                showMessage(error.message ?: error.code ?: "-")
                                hideProgressDialog()
                            }
                        )
                    }
                }
            }
        } catch (ex: Exception) {
            showMessage(getString(R.string.error_ex))
        }

    }

    private fun changeLocker() {
        val result = JsonObject()
        result.addProperty(currentLockerNo.toString(), false)
        val req = PudoSenderCallbackRequest(
            appPref.kioskInfo!!.generalprofile_id,
            currentTransactionId,
            appPref.currentTransactionType.toString().lowercase(),
            result
        )
        showProgressDialog(getString(R.string.search_locker))

        if (appPref.currentTransactionType == TransactionType.PUDO_SENDER_WALKIN) {
            PudoRepository.getInstance().senderWalkinCallbackChangeLocker(
                req,
                onSuccess = { resp ->
                    openLocker(resp.locker_no, resp.locker_commands)
                },
                onFailure = { error ->
                    hideProgressDialog()
                    handleError(error)
                }
            )
        } else {
            PudoRepository.getInstance().senderCallbackChangeLocker(
                req,
                onSuccess = { resp ->
                    openLocker(resp.locker_no, resp.locker_commands)
                },
                onFailure = { error ->
                    hideProgressDialog()
                    handleError(error)
                }
            )
        }

    }

    private fun handleError(error: HttpResponse<Any>) {
        when (error.code) {
            "REFUND" -> {
                CustomDialog.newInstance(R.layout.dialog_confirm_cancel_pudo_success).apply {
                    onOkClickListener = {
                        submit("refund")
                    }
                    timeoutCallback = {
                        onCancel()
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
            else -> {
                showMessage(error.message!!)
            }
        }
    }

    private fun openLocker(lockerName: String, command: JsonObject, reOpen: Boolean = false) {

        isChangeRequest = false
        currentLockerName = lockerName
        tvLockerNo.text = "${getString(R.string.get_locker_no)} ${lockerName}"
        currentLockerNo = command.keySet().first().toInt()
        val jsonObj = command[currentLockerNo.toString()].asJsonObject
        currentOpenCommand = jsonObj["open"].toString()
        currentInQuireCommand = jsonObj["inquire"].toString()
        isOpenCommand = true
        hardwareService!!.writeCommand(currentOpenCommand)
        Thread.sleep(500)
        isOpenCommand = false
        findSuccess = false
        hardwareService!!.writeCommand(currentInQuireCommand)

        Log.d("OPENZ", "openLocker : " + isOpenCommand.toString())

//        if (!reOpen) {
//            Handler(Looper.getMainLooper()).postDelayed({
//                if (!findSuccess) {
//                    hideProgressDialog()
//                    if (tvCountdown.text.toString() !== "00:00") {
//                        showMessage(getString(R.string.search_locker_fail), onAcceptClick = {
//                            appPref.currentTransactionId = null
//                            onCancel()
//                        })
//
//                    } else {
//                        showMessage(getString(R.string.search_locker_fail))
//                    }
//                }
//            }, 3000)
//        } else {
//            Handler(Looper.getMainLooper()).postDelayed({
//                if (!findSuccess) {
//                    hideProgressDialog()
//                    if (tvCountdown.text.toString() !== "00:00") {
//                        showMessage(getString(R.string.re_open_fail), onAcceptClick = {
//
//                        })
//                    } else {
//                        showMessage(getString(R.string.re_open_fail))
//                    }
//                }
//            }, 3000)
//        }

        hideProgressDialog()


    }

    private fun reOpen() {
        showProgressDialog()
        when (appPref.currentTransactionType) {
            TransactionType.PUDO_CP_DROP -> {
                CpRepository.getInstance().senderReOpen(
                    CpReOpenRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
                    onSuccess = { resp ->
                        openLocker(resp.locker_no, resp.locker_commands, true)
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        ReOpenFailDialog.newInstance(error).apply {
                            onOkClickListener = {
                                onCancel()
                            }
                            timeoutCallback = {
                                onCancel()
                            }
                        }.run {
                            Log.d("OPENZ", "hideProgressDialog()")
                            show(supportFragmentManager, "")
                        }
                    }
                )
            }
            TransactionType.GO_IN -> {
                GoRepository.getInstance().dropReOpen(
                    CpReOpenRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
                    onSuccess = { resp ->
                        openLocker(resp.locker_no, resp.locker_commands, true)
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        ReOpenFailDialog.newInstance(error).apply {
                            onOkClickListener = {
                                onCancel()
                            }
                            timeoutCallback = {
                                onCancel()
                            }
                        }.run {
                            Log.d("OPENZ", "hideProgressDialog()")
                            show(supportFragmentManager, "")
                        }
                    }
                )
            }
            TransactionType.PUDO_SENDER -> {
                PudoRepository.getInstance().senderReOpen(
                    CpReOpenRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
                    onSuccess = { resp ->
                        openLocker(resp.locker_no, resp.locker_commands, true)
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        ReOpenFailDialog.newInstance(error).apply {
                            onOkClickListener = {
                                onCancel()
                            }
                            timeoutCallback = {
                                onCancel()
                            }
                        }.run {
                            Log.d("OPENZ", "hideProgressDialog()")
                            show(supportFragmentManager, "")
                        }
                    }
                )
            }
            TransactionType.PUDO_SENDER_WALKIN -> {
                PudoRepository.getInstance().walkinReOpen(
                    CpReOpenRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
                    onSuccess = { resp ->
                        openLocker(resp.locker_no, resp.locker_commands, true)
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        ReOpenFailDialog.newInstance(error).apply {
                            onOkClickListener = {
                                onCancel()
                            }
                            timeoutCallback = {
                                onCancel()
                            }
                        }.run {
                            Log.d("OPENZ", "hideProgressDialog()")
                            show(supportFragmentManager, "")
                        }
                    }
                )
            }
            TransactionType.ADIDAS_DROP -> {
                AdidasRepository.getInstance().dropReOpen(
                    CpReOpenRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
                    onSuccess = { resp ->
                        openLocker(resp.locker_no, resp.locker_commands, true)
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        ReOpenFailDialog.newInstance(error).apply {
                            onOkClickListener = {
                                onCancel()
                            }
                            timeoutCallback = {
                                onCancel()
                            }
                        }.run {
                            Log.d("OPENZ", "hideProgressDialog()")
                            show(supportFragmentManager, "")
                        }
                    }
                )
            }
            else -> {

            }
        }

    }

    override fun onDestroy() {
        hardwareService = null
        Log.d("OPENZ", using.toString())

        currentLockerName = ""
        currentLockerNo = 0
        currentOpenCommand = ""
        currentInQuireCommand = ""
        isOpenCommand = true
        findSuccess = false
        currentTransactionId = ""
        isChangeRequest = false
        d = null
        using = false

        super.onDestroy()
    }


    override fun onPause() {
        timerPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (!isCreate) {
            timerResume()
        }
        isCreate = false
    }


}