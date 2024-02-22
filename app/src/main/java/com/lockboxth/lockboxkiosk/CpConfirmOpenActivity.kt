package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.customdialog.ReOpenFailDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.http.model.adidas.AdidasDropFinishRequest
import com.lockboxth.lockboxkiosk.http.model.adidas.AdidasPickupCallbackRequest
import com.lockboxth.lockboxkiosk.http.model.cp.CpPickupAck
import com.lockboxth.lockboxkiosk.http.model.cp.CpReOpenRequest
import com.lockboxth.lockboxkiosk.http.model.go.GoPickupCallbackRequest
import com.lockboxth.lockboxkiosk.http.model.go.GoPickupFinishRequest
import com.lockboxth.lockboxkiosk.http.model.go.GoReOpenRequest
import com.lockboxth.lockboxkiosk.http.model.locker.LockerFinishRequest
import com.lockboxth.lockboxkiosk.http.repository.AdidasRepository
import com.lockboxth.lockboxkiosk.http.repository.CpRepository
import com.lockboxth.lockboxkiosk.http.repository.GoRepository
import com.lockboxth.lockboxkiosk.service.hardware.HardwareService
import kotlinx.android.synthetic.main.activity_cp_confirm_open.*
import java.util.ArrayList
import java.util.HashMap

class CpConfirmOpenActivity : BaseActivity() {

    private lateinit var lockerNo: String
    private lateinit var lockerCommands: String
    private lateinit var eventType: String

    companion object {
        var isReadStatus = false
        var currentReadIndex = 0;
        var currentOpenIndex = 0;
        var commands = JsonObject()
        var results = JsonObject()
        var eventType: String = ""
        var commandOpens = ArrayList<String>()
        var readCommand: HashMap<String, ArrayList<String>> = hashMapOf()
        var retryCount = 0
        var findSuccess = false
    }

    private var hardwareService: HardwareService? = HardwareService.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cp_confirm_open)

        lockerNo = intent.getStringExtra("locker_no")!!
        lockerCommands = intent.getStringExtra("locker_commands")!!
        eventType = intent.getStringExtra("event_type")!!

        setTimeoutMinute(5, tvCountdown) {
            if (appPref.currentTransactionType == TransactionType.GO_OUT || appPref.currentTransactionType == TransactionType.ADIDAS_PICKUP) {
                submit("auto_pickup") {
                    goToMainActivity()
                }
            } else {
                submit("timeout") {
                    appPref.currentTransactionId = null
                    onCancel()
                }
            }
        }
        btnConfirm.setOnClickListener {
            submit("open_success") {
                val intent = Intent(this@CpConfirmOpenActivity, PaymentSuccessActivity::class.java)
                intent.putExtra("locker_no", lockerNo)
                startActivity(intent)
            }
        }

        btnNotOpen.setOnClickListener {
            submit("open_fail") {
                goToMainActivity()
            }
        }

        btnConfirm2.setOnClickListener {
            submit("pickup") {
                goToMainActivity()
            }
        }

        layoutReopen.visibility = View.GONE
        if (appPref.currentTransactionType == TransactionType.GO_OUT || appPref.currentTransactionType == TransactionType.ADIDAS_PICKUP) {
            layoutReopen.visibility = View.VISIBLE
        }

        btnReOpen.setOnClickListener {
            if (appPref.currentTransactionType == TransactionType.GO_OUT) {
                GoRepository.getInstance().goPickupReOpen(
                    GoReOpenRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
                    onSuccess = { resp ->
                        lockerNo = resp.locker_no
                        commands = resp.locker_commands
                        eventType = resp.event_type
                        open()
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
            } else if (appPref.currentTransactionType == TransactionType.ADIDAS_PICKUP) {
                AdidasRepository.getInstance().pickupReOpen(
                    CpReOpenRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
                    onSuccess = { resp ->
                        lockerNo = resp.locker_no
                        commands = resp.locker_commands
                        eventType = resp.event_type
                        open()
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
        }

        if (appPref.currentTransactionType == TransactionType.GO_OUT || appPref.currentTransactionType == TransactionType.ADIDAS_PICKUP) {
            layoutButton2.visibility = View.VISIBLE
            layoutButton1.visibility = View.GONE
            tvTitle3.visibility = View.GONE
            tvTitle.text = getString(R.string.open_success_title2)
            tvDesc.text = getString(R.string.parcel_no2)
            tvHead.text = getString(R.string.pick2)
        } else {
            layoutButton2.visibility = View.GONE
            layoutButton1.visibility = View.VISIBLE
            tvTitle3.visibility = View.VISIBLE
        }

        tvDesc.text = tvDesc.text.toString() + lockerNo

        hardwareService?.setOnDataReceived { s ->
            Log.i("setOnDataReceived", "=============================================================================================")
            Log.i("setOnDataReceived", "$s >> isRead : ${isReadStatus}")
            if (isReadStatus) {
                findSuccess = true

                Log.i("setOnDataReceived", "read index ${currentReadIndex} of ${readCommand.keys.count()}")
                Log.i("setOnDataReceived", "${readCommand.keys.size} < ${currentReadIndex}")
                if (readCommand.keys.size > currentReadIndex) {
                    val c = readCommand.keys.toList()[currentReadIndex]
                    val blocks = readCommand[c]
                    Log.i("setOnDataReceived", "blocks : ${TextUtils.join(",", blocks!!.toArray())}")
                    val status: String = s.substring(7, 11)
                    var arrStatus: CharArray = hardwareService!!.hex2binary(status)!!
                    arrStatus = hardwareService!!.reverse(arrStatus)!!

                    blocks.forEach { b ->
                        var block: Int = b.toInt() - 1
                        block %= 8
                        val isOpen = arrStatus[block] == '0'
                        results.addProperty(b, isOpen)
                        Log.i("setOnDataReceived", "block : $b >> isOpen : $isOpen")
                    }
                    currentReadIndex++
                    processRead()
                }
            } else {
                isReadStatus = false
                currentOpenIndex++

                if (currentOpenIndex < commandOpens.count()) {
                    Log.i("setOnDataReceived", "open ${currentOpenIndex} of ${commandOpens.count()}")
                    hardwareService!!.writeCommand(commandOpens[currentOpenIndex])
                } else {
                    processRead()
                }
            }
        }

        commands = Gson().fromJson(lockerCommands, JsonObject::class.java)

        open()


    }

    fun open() {
        showProgressDialog()
        readCommand = hashMapOf()
        commandOpens = ArrayList<String>()
        results = JsonObject()
        retryCount = 0

        commands.keySet().forEach { k ->
            val cmd = commands[k].asJsonObject!!
            val inquireCmd = cmd["inquire"].toString().replace("\"", "").trim()
            if (!readCommand.containsKey(inquireCmd)) {
                readCommand[inquireCmd] = arrayListOf()
            }
            readCommand[inquireCmd]!!.add(k)
            commandOpens.add(cmd["open"].toString().replace("\"", "").trim())
        }

        currentOpenIndex = 0
        currentReadIndex = 0
        isReadStatus = false

        hardwareService!!.writeCommand(commandOpens[currentOpenIndex])
        Log.i("setOnDataReceived", "open ${currentOpenIndex} of ${commandOpens.count()}")
        Handler(Looper.getMainLooper()).postDelayed({
            hideProgressDialog()
            if (!findSuccess) {
                val c = readCommand.keys.toList()[currentReadIndex]
                val blocks = readCommand[c]
                blocks!!.forEach { b ->
                    results.addProperty(b, false)
                    Log.i("setOnDataReceived", "block : $b >> isOpen : false")
                }
                currentReadIndex++
                retryCount = 3
                processRead()
            }
        }, 5000)


    }

    private fun processRead() {
        isReadStatus = true
        Log.d("setOnDataReceived", "processRead ${currentReadIndex} >= ${readCommand.keys.count() - 1}")
        if (currentReadIndex > readCommand.keys.count() - 1 && results.keySet().isNotEmpty()) {

            commandOpens.clear()
            results.keySet().forEach { k ->
                if (!results[k].asBoolean) {
                    val cmd = commands[k].asJsonObject!!
                    commandOpens.add(cmd["open"].toString().replace("\"", "").trim())
                }
            }

            Log.d("setOnDataReceived", "currentReadIndex : ${currentReadIndex}")

            if ((commandOpens.isEmpty() || retryCount >= 3) && isReadStatus) {
                Log.i("setOnDataReceived", "send status")

                if (appPref.currentTransactionType == TransactionType.GO_OUT) {
                    val req = GoPickupCallbackRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, eventType, results)
                    GoRepository.getInstance().goPickupCallback(
                        req,
                        onSuccess = { },
                        onFailure = { error ->
                            showMessage(error)
                        }
                    )
                } else if (appPref.currentTransactionType == TransactionType.ADIDAS_PICKUP) {
                    val req = AdidasPickupCallbackRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, eventType, results)
                    AdidasRepository.getInstance().pickupCallback(
                        req,
                        onSuccess = { },
                        onFailure = { error ->
                            showMessage(error)
                        }
                    )
                } else {
                    val req = LockerFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, eventType, results)
                    CpRepository.getInstance().receiverFinish(
                        req,
                        onSuccess = { },
                        onFailure = { error ->
                            showMessage(error)
                        }
                    )
                }

            } else {
                retryCount++
                Log.i("setOnDataReceived", "retry ${retryCount}")
                currentOpenIndex = 0
                currentReadIndex = 0
                isReadStatus = false
                results = JsonObject()
                if (currentOpenIndex < commandOpens.size) {
                    hardwareService!!.writeCommand(commandOpens[currentOpenIndex])
                } else {
                    retryCount = 3
                    val c = readCommand.keys.toList()[currentReadIndex]
                    val blocks = readCommand[c]
                    blocks!!.forEach { b ->
                        results.addProperty(b, false)
                        Log.i("setOnDataReceived", "block : $b >> isOpen : false")
                    }
                    currentReadIndex++
                    processRead()
                }
            }

        } else {
            Log.d("setOnDataReceived", "read : ${currentReadIndex} ")
            val c = readCommand.keys.toList()[currentReadIndex]
            hardwareService!!.writeCommand(c)
        }
    }


    private fun submit(submitType: String, callBack: () -> Unit) {
        showProgressDialog()
        if (appPref.currentTransactionType == TransactionType.GO_OUT) {
            GoRepository.getInstance().goPickupFinish(
                GoPickupFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, submitType),
                onSuccess = {
                    hideProgressDialog()
                    callBack.invoke()
                },
                onFailure = { err ->
                    hideProgressDialog()
                    showMessage(err)
                }
            )
        } else if (appPref.currentTransactionType == TransactionType.ADIDAS_PICKUP) {
            AdidasRepository.getInstance().pickupFinish(
                AdidasDropFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, submitType),
                onSuccess = {
                    hideProgressDialog()
                    callBack.invoke()
                },
                onFailure = { err ->
                    hideProgressDialog()
                    showMessage(err.message!!)
                }
            )
        } else {
            CpRepository.getInstance().receiverAck(
                CpPickupAck(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, submitType),
                onSuccess = {
                    hideProgressDialog()
                    callBack.invoke()
                },
                onFailure = { err ->
                    hideProgressDialog()
                    showMessage(err)
                }
            )
        }
    }

    override fun onDestroy() {
        hardwareService = null
        isReadStatus = false
        currentReadIndex = 0;
        currentOpenIndex = 0;
        commands = JsonObject()
        results = JsonObject()
        eventType = ""
        commandOpens.clear()
        readCommand.clear()
        retryCount = 0
        findSuccess = false
        super.onDestroy()
    }
}