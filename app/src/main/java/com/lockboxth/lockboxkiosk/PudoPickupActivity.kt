package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockboxth.lockboxkiosk.adapter.PudoPickupRecyclerAdapter
import com.lockboxth.lockboxkiosk.customdialog.ConfirmCourierAlertDialog
import com.lockboxth.lockboxkiosk.customdialog.ConfirmCourierPickupDialog
import com.lockboxth.lockboxkiosk.customdialog.ConfirmDialog
import com.lockboxth.lockboxkiosk.customdialog.CustomDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoUpdateStatusCourierRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoVerifyCourierResponse
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import com.lockboxth.lockboxkiosk.service.hardware.HardwareService
import kotlinx.android.synthetic.main.activity_pudo_pickup.*

class PudoPickupActivity : BaseActivity() {

    enum class State {
        OPEN, CONFIRM
    }

    private var currentState = State.OPEN

    companion object {
        var data: PudoVerifyCourierResponse? = null
        var currentIndex = 0
        var isOpenCommand = false
        var status: String = ""
        var hasData = false
        var using = false
    }

    private lateinit var adapter: PudoPickupRecyclerAdapter

    private var hardwareService: HardwareService? = HardwareService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_pickup)

        data = intent.getParcelableExtra("data")!!

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PudoPickupRecyclerAdapter(this, data!!.parcel_list)
        recyclerView.adapter = adapter

        tvTotal.text = data!!.count_parcel.toString()

        setTimeoutMinute(data!!.timer, tvCountdown) {
            onCancel()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        currentIndex = 0
        currentState == State.OPEN

        using = true

        btnConfirm.setOnClickListener {
            if (currentState == State.OPEN) {
                processNext()
                timerPause()
            } else {
                val messages = data!!.parcel_list.map { i -> "${i.locker_no} : ${i.status_text}" }
                val message = TextUtils.join("\n", messages)
                val intent = Intent(this@PudoPickupActivity, PaymentSuccessActivity::class.java)
                intent.putExtra("showSubtitle", false)
                intent.putExtra("locker_no", message)
                startActivity(intent)
                finish()
            }
        }

        updateStateUI()

        hardwareService?.setOnDataReceived { s ->
            if (using) {
                if (!isOpenCommand) {
                    val message = ""
                    try {
                        hasData = true
                        val openStatus: String = s.substring(7, 11)
                        var arrStatus: CharArray = hardwareService!!.hex2binary(openStatus)!!
                        arrStatus = hardwareService!!.reverse(arrStatus)!!
                        val item = data!!.parcel_list[currentIndex]
                        val currentLockerNo = item.locker_commands.keySet().first().toInt()
                        var block: Int = currentLockerNo - 1
                        block %= 8
                        val isOpen = arrStatus[block] == '0'
                        status = if (isOpen) {
                            "confirm"
                        } else {
                            "fail"
                        }
                        " Loker No. ${item.locker_no} / ${item.tracking_number}"
                        Log.d("", "${block} : ${status}")
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                    if (status == "confirm") {
                        ConfirmCourierPickupDialog.newInstance(message).apply {
                            onOkClickListener = {
                                updateStatus()
                            }
                            onCancelClickListener = {
                                status = "reject"
                                updateStatus()
                            }
                            timeoutCallback = {
//                            onCancel()
                            }
                        }.run {
                            try {
                                show(supportFragmentManager, "")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        CustomDialog.newInstance(R.layout.dialog_pudo_locker_error).apply {
                            onOkClickListener = {
                                updateStatus()
                            }
                            timeoutCallback = {
//                            onCancel()
                            }
                        }.run {
                            try {
                                show(supportFragmentManager, "")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openLocker() {
        showProgressDialog("กำลังเปิดตู้")
        val item = data!!.parcel_list[currentIndex]
        val currentLockerNo = item.locker_commands.keySet().first().toInt()
        val jsonObj = item.locker_commands[currentLockerNo.toString()].asJsonObject
        val currentOpenCommand = jsonObj["open"].toString()
        val currentInQuireCommand = jsonObj["inquire"].toString()
        isOpenCommand = true
        hardwareService!!.writeCommand(currentOpenCommand)
        Thread.sleep(500)
        isOpenCommand = false
        hasData = false
        hardwareService!!.writeCommand(currentInQuireCommand)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!hasData) {
                hideProgressDialog()
                status = "fail"
                CustomDialog.newInstance(R.layout.dialog_pudo_locker_error).apply {
                    onOkClickListener = {
                        updateStatus()
                    }
                    timeoutCallback = {
//                        onCancel()
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
        }, 3000)

    }

    private fun updateStatus() {
        if (currentIndex < data!!.parcel_list.size) {
            val item = data!!.parcel_list[currentIndex]
            val req = PudoUpdateStatusCourierRequest(
                appPref.kioskInfo!!.generalprofile_id,
                appPref.currentTransactionId!!,
                item.order_id,
                status
            )
            showProgressDialog()
            PudoRepository.getInstance().updateStatusCourier(
                req,
                onSuccess = { resp ->
                    data = resp
                    adapter.updateDataSource(resp.parcel_list)
                    hideProgressDialog()
                    currentIndex += 1
                    if (status == "confirm") {
                        ConfirmCourierAlertDialog.newInstance().apply { }.run {
                            show(supportFragmentManager, "")
                        }
                    }
                    if (currentIndex == data!!.parcel_list.size) {
                        currentState = State.CONFIRM
                        updateStateUI()
                    }
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error.message!!)
                }
            )
        }
    }

    private fun processNext() {
        if (currentIndex <= data!!.parcel_list.size - 1) {
            openLocker()
        }
    }

    private fun updateStateUI() {
        tvConfirmText.text = if (currentState == State.OPEN) {
            getString(R.string.open)
        } else {
            getString(R.string.success)
        }
    }

    override fun onDestroy() {
        hardwareService = null
        using = false
        super.onDestroy()
    }
}