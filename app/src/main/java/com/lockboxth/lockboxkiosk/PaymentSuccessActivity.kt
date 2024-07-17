package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.http.model.cp.CpPickupAck
import com.lockboxth.lockboxkiosk.http.model.locker.LockerFinishRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderFinishRequest
import com.lockboxth.lockboxkiosk.http.repository.CpRepository
import com.lockboxth.lockboxkiosk.http.repository.LockerRepository
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import com.lockboxth.lockboxkiosk.service.hardware.HardwareService
import kotlinx.android.synthetic.main.activity_payment_success.*
import java.util.*


class PaymentSuccessActivity : BaseActivity() {

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
        var using = false
    }

    private var hardwareService: HardwareService? = HardwareService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        val lockerNo = intent.getStringExtra("locker_no")
        val lockerCommands = intent.getStringExtra("locker_commands")

        val showSubtitle = intent.getBooleanExtra("showSubtitle", true)

        if (!lockerNo.isNullOrEmpty()) {
            when (appPref.currentTransactionType) {
                TransactionType.GO_IN,
                TransactionType.ADIDAS_DROP -> {
                    tvLockerNo.text = getString(R.string.success_hint).replace("XXXXX", lockerNo)
                }

                TransactionType.PUDO_COURIER_PICKUP -> {
                    tvLockerNo.text = getString(R.string.rec2).replace("XXXXX", lockerNo)
                }

                else -> {
                    tvLockerNo.text = lockerNo
                }
            }
        } else {
            tvLockerNo.visibility = View.GONE
        }

        if (!showSubtitle) {
            tvOpenText.visibility = View.GONE
        }

        if (!lockerCommands.isNullOrEmpty()) {
            eventType = intent.getStringExtra("event_type")!!
            commands = Gson().fromJson(lockerCommands, JsonObject::class.java)

            readCommand = hashMapOf()
            commandOpens = ArrayList<String>()
            results = JsonObject()
            retryCount = 0

            using = true

            hardwareService?.setOnDataReceived { s ->
                if (!using) {
                    return@setOnDataReceived
                }
                Log.i("setOnDataReceived", "=============================================================================================")
                Log.i("setOnDataReceived", "$s >> isRead : $isReadStatus")
                if (isReadStatus) {
                    findSuccess = true

                    Log.i("setOnDataReceived", "read index $currentReadIndex of ${readCommand.keys.count()}")
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
                        Log.i("setOnDataReceived", "open $currentOpenIndex of ${commandOpens.count()}")
                        hardwareService!!.writeCommand(commandOpens[currentOpenIndex])
                    } else {
                        processRead()
                    }
                }
            }

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
            Log.i("setOnDataReceived", "open $currentOpenIndex of ${commandOpens.count()}")
            Handler(Looper.getMainLooper()).postDelayed({
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
            }, 3000)

        }

        when (appPref.currentTransactionType) {
            TransactionType.GO_IN -> {
                btnMore.visibility = View.VISIBLE
                btnNo.setOnClickListener {
                    appPref.currentTransactionId = null
                    onCancel()
                }
                btnYes.setOnClickListener {
                    val intent = Intent(this@PaymentSuccessActivity, RegisterActivity::class.java)
                    intent.putExtra("isGoOut", true)
                    startActivity(intent)
                    finish()
                }
            }

            TransactionType.PUDO_COURIER_PICKUP -> {
                if (appPref.outType == "qr") {
                    btnMore.visibility = View.VISIBLE
                    tvMore.text = getString(R.string.more2)
                    tvNo.text = getString(R.string.no2)
                    tvYes.text = getString(R.string.yes2)
                    btnYes.setOnClickListener {
                        val intent = Intent(this@PaymentSuccessActivity, QrCodeScannerActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            else -> {
                btnMore.visibility = View.GONE
            }
        }

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

            Log.d("setOnDataReceived", "currentReadIndex : $currentReadIndex")

            if ((commandOpens.isEmpty() || retryCount >= 3) && isReadStatus) {
                Log.i("setOnDataReceived", "send status")

                when (appPref.currentTransactionType) {
                    TransactionType.PUDO_RECEIVER -> {
                        val req = LockerFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, eventType, results)
                        PudoRepository.getInstance().receiverFinish(
                            req,
                            onSuccess = { },
                            onFailure = { error ->
                                showMessage(error.message!!)
                            }
                        )
                    }

                    TransactionType.PUDO_SENDER_WALKIN -> {
                        val req = LockerFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, eventType, results)
                        PudoRepository.getInstance().walkInCallback(
                            req,
                            onSuccess = { },
                            onFailure = { error ->
                                showMessage(error.message!!)
                            }
                        )
                    }

                    TransactionType.PUDO_CP_DROP -> {
                        val req = PudoSenderFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, "drop")
                        CpRepository.getInstance().senderFinish(
                            req,
                            onSuccess = { },
                            onFailure = { error ->
                                showMessage(error.message!!)
                            }
                        )
                    }

                    TransactionType.PUDO_CP_PICKUP -> {
                        val req = LockerFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, eventType, results)
                        CpRepository.getInstance().receiverFinish(
                            req,
                            onSuccess = { },
                            onFailure = { error ->
                                showMessage(error)
                            }
                        )
                    }

                    TransactionType.PUDO_COURIER_PICKUP -> {
                        Log.d("PUDO_COURIER_PICKUP", "PUDO_COURIER_PICKUP")
                    }

                    else -> {

                        if (eventType == "unlock_emergency") {
                            val txn = intent.getStringExtra("txn")
                            val req = LockerFinishRequest(appPref.kioskInfo!!.generalprofile_id, txn ?: "", eventType, results)
                            LockerRepository.getInstance().unlockEmergency(
                                req,
                                onSuccess = { },
                                onFailure = { error ->
                                    showMessage(error)
                                }
                            )
                        } else {
                            val req = LockerFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, eventType, results)
                            LockerRepository.getInstance().finish(
                                req,
                                onSuccess = { },
                                onFailure = { error ->
                                    showMessage(error)
                                }
                            )
                        }

                    }
                }

            } else {
                retryCount++
                Log.i("setOnDataReceived", "retry $retryCount")
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
            Log.d("setOnDataReceived", "read : $currentReadIndex ")
            val c = readCommand.keys.toList()[currentReadIndex]
            hardwareService!!.writeCommand(c)
        }
    }

    override fun onResume() {
        super.onResume()
        countDown()
    }

    override fun onDestroy() {
        hardwareService = null
        isReadStatus = false
        currentReadIndex = 0
        currentOpenIndex = 0
        commands = JsonObject()
        results = JsonObject()
        eventType = ""
        commandOpens.clear()
        readCommand.clear()
        retryCount = 0
        findSuccess = false
        using = false
        super.onDestroy()
    }

    private fun countDown() {
        val timeout = if (appPref.currentTransactionType == TransactionType.GO_IN) {
            30
        } else {
            10
        }
        setTimeoutSecond(timeout, null) {
            appPref.currentTransactionId = null
            onCancel()
        }
    }


}