package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.core.os.HandlerCompat
import com.lockboxth.lockboxkiosk.customdialog.ConfirmSenderPrintDialog
import com.lockboxth.lockboxkiosk.customdialog.CustomDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoWalkinWayBillResponse
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoWayBillRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoWayBillScanRequest
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import com.lockboxth.lockboxkiosk.print.PrintUtil
import com.lockboxth.lockboxkiosk.service.hardware.HardwareService
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_pudo_walkin_waybill.*
import kotlinx.android.synthetic.main.activity_pudo_walkin_waybill.btnCancel
import kotlinx.android.synthetic.main.activity_pudo_walkin_waybill.camera
import kotlinx.android.synthetic.main.activity_pudo_walkin_waybill.tvCountdown
import kotlinx.android.synthetic.main.activity_rfid_payment.*
import java.io.ByteArrayOutputStream

class PudoWalkinWaybillActivity : BaseActivity() {

    private var hardwareService: HardwareService? = null
    private var currentQrCode: String? = null

    private var printInfo: PudoWalkinWayBillResponse? = null

    companion object {
        var base64: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_walkin_waybill)

        base64 = null

        btnCancel.setOnClickListener {
            onCancel()
        }

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        runOnUiThread {
            hardwareService = HardwareService.getInstance()
            hardwareService?.setOnDataReceived { s ->
                if (s.length >= 5) {
                    currentQrCode = s
                    scanQr()
                }
            }
        }

        if (appPref.currentTransactionType == TransactionType.PUDO_SENDER) {
            val camera = findViewById<CameraView>(R.id.camera)
            camera.setLifecycleOwner(this)
            camera.addCameraListener(object : CameraListener() {
                @SuppressLint("WrongThread")
                override fun onPictureTaken(result: PictureResult) {
                    super.onPictureTaken(result)
                    result.toBitmap { bmp ->
                        val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
                        val stream = ByteArrayOutputStream()
                        b.compress(Bitmap.CompressFormat.PNG, 90, stream)
                        val image = stream.toByteArray()
                        base64 = Util.toBase64(image)
                    }
                }
            })

            try {
                val timer = object : CountDownTimer(3000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        camera.takePicture()
                    }
                }
                timer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        layoutPrint.setOnClickListener {
            if (printInfo != null && printInfo!!.print_count <= printInfo!!.print_limit) {
                getWaybill()
            } else {
                CustomDialog.newInstance(R.layout.dialog_pudo_print2).apply {
                    onOkClickListener = {
                        onCancel()
                    }
                    timeoutCallback = {
                        onCancel()
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

        btnCancel.setOnClickListener {
            onCancel()
        }

        if (appPref.currentTransactionType == TransactionType.PUDO_SENDER) {
            ConfirmSenderPrintDialog.newInstance().apply {
                onOkClickListener = { printed ->
                    if (!printed) {
                        getWaybill()
                    }
                }
                timeoutCallback = {
                    onCancel()
                }
            }.run {
                try {
                    show(supportFragmentManager, "")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            walkInShipmentConfirm()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getWaybill() {
        showProgressDialog()
        CustomDialog.newInstance(R.layout.dialog_pudo_print2).apply {
            onOkClickListener = {

            }
            timeoutCallback = {

            }
        }.run {
            try {
                show(supportFragmentManager, "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val req = PudoWayBillRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!)
        if (appPref.currentTransactionType == TransactionType.PUDO_SENDER) {
            PudoRepository.getInstance().senderWaybill(
                req,
                onSuccess = { resp ->
                    printInfo = resp
                    handleShippingDetail()
                    hideProgressDialog()
                },
                onFailure = { err ->
                    hideProgressDialog()
                    showMessage(err.message ?: "-", timer = true, onAcceptClick = {
                        onCancel()
                    })
                }
            )
        } else {
            PudoRepository.getInstance().waybillWalkin(
                req,
                onSuccess = { resp ->
                    printInfo = resp
                    handleShippingDetail()
                    hideProgressDialog()
                },
                onFailure = { err ->
                    hideProgressDialog()
                    showMessage(err.message ?: "-", timer = true, onAcceptClick = {
                        onCancel()
                    })
                }
            )
        }


    }

    @SuppressLint("SetTextI18n")
    private fun walkInShipmentConfirm() {
        showProgressDialog()
        CustomDialog.newInstance(R.layout.dialog_pudo_print2).apply {
            onOkClickListener = {

            }
            timeoutCallback = {

            }
        }.run {
            try {
                show(supportFragmentManager, "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        showProgressDialog()
        val req = PaymentMethodRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!)
        PudoRepository.getInstance().walkInShipmentConfirm(
            req,
            onSuccess = { resp ->
                printInfo = resp
                handleShippingDetail()
                hideProgressDialog()
            },
            onFailure = { error ->
                showMessage(error.message!!)
                hideProgressDialog()
            }
        )

    }

    @SuppressLint("SetTextI18n")
    private fun handleShippingDetail() {
//        Log.d("QRR", printInfo!!.shipping_detail.tracking_number)
        tvTotalPrint.text = printInfo!!.print_count.toString()
        tvSummaryPrint.text = "(${printInfo!!.print_count}/${printInfo!!.print_limit})"
        PrintUtil.getInstance().printImage(printInfo!!.shipping_image)
    }

    private fun scanQr() {
        showProgressDialog()
        if (appPref.currentTransactionType == TransactionType.PUDO_SENDER) {
            val req = PudoWayBillScanRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, currentQrCode!!, base64)
            PudoRepository.getInstance().waybillSenderScan(
                req,
                onSuccess = { resp ->
                    hideProgressDialog()
                    PudoSenderOrderConfirmActivity.currentLockerName = resp.locker_no
                    PudoSenderOrderConfirmActivity.currentLockerNo = resp.locker_commands.keySet().first().toInt()
                    val jsonObj = resp.locker_commands[PudoSenderOrderConfirmActivity.currentLockerNo.toString()].asJsonObject
                    PudoSenderOrderConfirmActivity.currentOpenCommand = jsonObj["open"].toString()
                    PudoSenderOrderConfirmActivity.currentInQuireCommand = jsonObj["inquire"].toString()
                    val intent = Intent(this@PudoWalkinWaybillActivity, PudoSenderOrderConfirmActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = { error ->
                    showMessage(error.message!!)
                    hideProgressDialog()
                }
            )
        } else {
            val req = PudoWayBillScanRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, currentQrCode!!)
            PudoRepository.getInstance().waybillWalkinScan(
                req,
                onSuccess = { resp ->
                    hideProgressDialog()
                    val intent = Intent(this@PudoWalkinWaybillActivity, PaymentMethodActivity::class.java)
                    intent.putExtra("amount_pay", resp.amount_pay)
                    intent.putExtra("payment_method", ArrayList(resp.payment_method))
                    startActivity(intent)
                    finish()
                },
                onFailure = { error ->
                    showMessage(error.message!!)
                    hideProgressDialog()
                }
            )
        }

    }

    override fun onDestroy() {
        hardwareService = null
        super.onDestroy()
    }

}