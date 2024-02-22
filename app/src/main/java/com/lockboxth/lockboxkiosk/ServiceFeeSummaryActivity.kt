package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.adapter.ServiceFeeRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.go.GoInfoConfirmRequest
import com.lockboxth.lockboxkiosk.http.model.go.GoLockerSelectRequest
import com.lockboxth.lockboxkiosk.http.model.go.GoPickupConfirmRequest
import com.lockboxth.lockboxkiosk.http.model.go.GoPickupVerifyResponse
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateRequest
import com.lockboxth.lockboxkiosk.http.repository.GoRepository
import com.lockboxth.lockboxkiosk.http.repository.LockerRepository
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_locker_password.*
import kotlinx.android.synthetic.main.activity_service_fee_summary.*
import kotlinx.android.synthetic.main.activity_service_fee_summary.btnCancel
import kotlinx.android.synthetic.main.activity_service_fee_summary.btnConfirm
import kotlinx.android.synthetic.main.activity_service_fee_summary.camera
import kotlinx.android.synthetic.main.activity_service_fee_summary.recyclerView
import kotlinx.android.synthetic.main.activity_service_fee_summary.tvCountdown
import kotlinx.android.synthetic.main.activity_service_fee_summary.tvTitle
import java.io.ByteArrayOutputStream


class ServiceFeeSummaryActivity : BaseActivity() {

    companion object {
        var goOutSummary: GoPickupVerifyResponse? = null
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_fee_summary)

        btnCancel.setOnClickListener {
            onCancel()
        }

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        btnConfirm.setOnClickListener {
            when (appPref.currentTransactionType) {
                TransactionType.GO_IN -> {
                    showProgressDialog()
                    GoRepository.getInstance().goDropConfirm(
                        GoInfoConfirmRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
                        onSuccess = { resp ->
                            hideProgressDialog()
                            val cmd = Gson().fromJson(resp.locker_commands, JsonObject::class.java)
                            PudoSenderOrderConfirmActivity.currentLockerName = resp.locker_no
                            PudoSenderOrderConfirmActivity.currentLockerNo = cmd.keySet().first().toInt()
                            val jsonObj = cmd[PudoSenderOrderConfirmActivity.currentLockerNo.toString()].asJsonObject
                            PudoSenderOrderConfirmActivity.currentOpenCommand = jsonObj["open"].toString()
                            PudoSenderOrderConfirmActivity.currentInQuireCommand = jsonObj["inquire"].toString()
                            val intent = Intent(this@ServiceFeeSummaryActivity, PudoSenderOrderConfirmActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        onFailure = { err ->
                            hideProgressDialog()
                            showMessage(err)
                        }
                    )
                }
                TransactionType.GO_OUT -> {
                    showProgressDialog()
                    if (this.camera.isOpened) {
                        this.camera.takePicture()
                    } else {
                        submitGout()
                    }
                }
                else -> {
                    val intent = Intent(this@ServiceFeeSummaryActivity, PaymentMethodActivity::class.java)
                    intent.putExtra("price", tvTotal.text)
                    startActivity(intent)
                    finish()
                }
            }
        }

        layoutGo.visibility = View.GONE
        tvTotal0.visibility = View.GONE

        showProgressDialog()
        recyclerView.layoutManager = LinearLayoutManager(this@ServiceFeeSummaryActivity)
        when (appPref.currentTransactionType) {
            TransactionType.GO_IN -> {
                tvTitle0.text = getString(R.string.service_fee_summary)
                val blockUse = intent.getIntExtra("block_use", 0)
                layoutGo.visibility = View.VISIBLE
                GoRepository.getInstance().goDropSelect(
                    GoLockerSelectRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, blockUse),
                    onSuccess = { resp ->
                        hideProgressDialog()
                        recyclerView.adapter = ServiceFeeRecyclerAdapter(this@ServiceFeeSummaryActivity, resp.details, resp.time_limit)
                        if (resp.fee > 0) {
                            tvTotal0.visibility = View.VISIBLE
                            tvTotal0.text = Util.formatMoney(resp.fee)
                            tvTotal0.paintFlags = tvTotal0.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        }
                        tvTotal.text = Util.formatMoney(resp.amount)
                        tvSender.text = resp.sender_phone
                        tvReceiver.text = resp.receiver_phone
                        if (resp.is_ap) {
                            tvTitle.text = getString(R.string.ap_service)
                        } else {
                            tvTitle.text = getString(R.string.min_service).replace("HHH", resp.time_limit.toString())
                        }
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        showMessage(error)
                    }
                )
            }
            TransactionType.GO_OUT -> {
                hideProgressDialog()
                tvTitle0.text = getString(R.string.service_fee_summary)
                if (goOutSummary!!.is_ap) {
                    tvTitle.text = getString(R.string.ap_service)
                }
                recyclerView.adapter = ServiceFeeRecyclerAdapter(this@ServiceFeeSummaryActivity, goOutSummary!!.details, goOutSummary!!.time_over)
                tvTotal.text = Util.formatMoney(goOutSummary!!.amount)
                if (goOutSummary!!.fee > 0) {
                    tvTotal0.text = Util.formatMoney(goOutSummary!!.fee)
                    tvTotal0.paintFlags = tvTotal0.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tvTotal0.visibility = View.VISIBLE
                }
                val camera = findViewById<CameraView>(R.id.camera)
                camera.setLifecycleOwner(this)
                camera.addCameraListener(object : CameraListener() {
                    override fun onPictureTaken(result: PictureResult) {
                        super.onPictureTaken(result)
                        result.toBitmap { bmp ->
                            val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
                            val stream = ByteArrayOutputStream()
                            b.compress(Bitmap.CompressFormat.PNG, 90, stream)
                            val image = stream.toByteArray()
                            val base64 = Util.toBase64(image)
                            submitGout(base64)
                        }
                    }
                })
            }
            else -> {
                LockerRepository.getInstance().calcTotal(
                    LockerCalculateRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, "total", false),
                    onSuccess = { resp ->
                        hideProgressDialog()
                        recyclerView.adapter = ServiceFeeRecyclerAdapter(this@ServiceFeeSummaryActivity, resp.details, resp.time_limit)
                        tvTotal.text = Util.formatMoney(resp.amount)
                        tvTitle.text = getString(R.string.min_service).replace("HHH", resp.time_limit.toString())
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        showMessage(error)
                    }
                )
            }
        }


    }

    fun submitGout(base64: String = "") {
        GoRepository.getInstance().goPickupConfirm(
            GoPickupConfirmRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, base64),
            onSuccess = { resp ->
                hideProgressDialog()
                val cmdJson = Gson().toJson(resp.locker_commands)
                val intent = Intent(this@ServiceFeeSummaryActivity, CpConfirmOpenActivity::class.java)
                intent.putExtra("locker_no", resp.locker_no)
                intent.putExtra("locker_commands", cmdJson)
                intent.putExtra("event_type", resp.event_type)
                startActivity(intent)
                finish()
            },
            onFailure = { err ->
                hideProgressDialog()
                showMessage(err)
            }
        )
    }

    override fun onDestroy() {
        goOutSummary = null
        super.onDestroy()
    }
}