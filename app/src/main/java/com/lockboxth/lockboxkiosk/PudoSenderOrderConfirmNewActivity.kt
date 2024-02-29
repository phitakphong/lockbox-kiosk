package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderFinishRequest
import com.lockboxth.lockboxkiosk.http.repository.GoRepository
import kotlinx.android.synthetic.main.activity_pudo_sender_order_confirm_new.*
import kotlinx.android.synthetic.main.activity_pudo_sender_order_confirm_new.btnConfirm
import kotlinx.android.synthetic.main.activity_pudo_sender_order_confirm_new.tvCountdown

class PudoSenderOrderConfirmNewActivity : BaseActivity() {

    private var currentState: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_sender_order_confirm_new)

        btnSame.setOnClickListener {
            clearState()
            setOnClick(it.tag.toString())
        }

        btnBig.setOnClickListener {
            clearState()
            setOnClick(it.tag.toString())
        }

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        btnConfirm.setOnClickListener {

            val type = when (currentState) {
                "Same" -> "new_locker"
                "Big" -> "new_size"
                else -> ""
            }

            if (type.isNotEmpty()) {
                showProgressDialog()
                val req = PudoSenderFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, type)
                GoRepository.getInstance().goDropFinish(
                    req,
                    onSuccess = { code, resp ->
                        val cmd = Gson().fromJson(resp.locker_commands, JsonObject::class.java)
                        PudoSenderOrderConfirmActivity.currentLockerName = resp.locker_no
                        PudoSenderOrderConfirmActivity.currentLockerNo = cmd.keySet().first().toInt()
                        val jsonObj = cmd[PudoSenderOrderConfirmActivity.currentLockerNo.toString()].asJsonObject
                        PudoSenderOrderConfirmActivity.currentOpenCommand = jsonObj["open"].toString()
                        PudoSenderOrderConfirmActivity.currentInQuireCommand = jsonObj["inquire"].toString()
                        val intent = Intent(this@PudoSenderOrderConfirmNewActivity, PudoSenderOrderConfirmActivity::class.java)
                        hideProgressDialog()
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

        btnCancel.setOnClickListener {
            finish()
        }


    }

    private fun setOnClick(tag: String) {

        val colorWhite = ContextCompat.getColor(this, R.color.white)

        val background = when (tag) {
            "Same" -> ContextCompat.getDrawable(this, R.drawable.btn_item_blue)
            "Big" -> ContextCompat.getDrawable(this, R.drawable.btn_item_green)
            else -> {
                ContextCompat.getDrawable(this, R.drawable.card_item_white_border_gray_radius_16)
            }
        }
        val btnId = resources.getIdentifier("btn${tag}", "id", packageName)
        val tvId = resources.getIdentifier("tv${tag}", "id", packageName)

        val btn = findViewById<LinearLayoutCompat>(btnId)
        btn.background = background


        val tv = findViewById<TextView>(tvId)
        tv.setTextColor(colorWhite)

        currentState = tag

    }

    private fun clearState() {

        val colorBlack = ContextCompat.getColor(this, R.color.black)
        val defaultBackground = ContextCompat.getDrawable(this, R.drawable.card_item_white_border_gray_radius_16)

        btnSame.background = defaultBackground
        tvSame.setTextColor(colorBlack)

        btnBig.background = defaultBackground
        tvBig.setTextColor(colorBlack)

        currentState = null

    }

}