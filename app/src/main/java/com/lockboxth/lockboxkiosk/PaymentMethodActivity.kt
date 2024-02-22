package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import androidx.recyclerview.widget.GridLayoutManager
import com.lockboxth.lockboxkiosk.adapter.PaymentMethodRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodRequest
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodResponse
import com.lockboxth.lockboxkiosk.http.repository.PaymentRepository
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_payment_method.*
import kotlinx.android.synthetic.main.activity_payment_method.btnCancel
import kotlinx.android.synthetic.main.activity_payment_method.btnConfirm
import kotlinx.android.synthetic.main.activity_payment_method.tvCountdown


class PaymentMethodActivity : BaseActivity() {

    lateinit var adapter: PaymentMethodRecyclerAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        autoHideKeyboard = false

        attachKeyboardListeners()

        recyclerView.layoutManager = GridLayoutManager(this@PaymentMethodActivity, 5)

        showProgressDialog()

        val req = PaymentMethodRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!)
        when (appPref.currentTransactionType) {
            TransactionType.PUDO_SENDER_WALKIN -> {
                val amount = intent.getFloatExtra("amount_pay", 0f)
                val paymentMethod: ArrayList<PaymentMethodResponse> = intent.getParcelableArrayListExtra("payment_method")!!
                tvPrice.text = Util.formatMoney(amount)
                adapter = PaymentMethodRecyclerAdapter(this@PaymentMethodActivity, paymentMethod)
                recyclerView.adapter = adapter
                hideProgressDialog()
            }
            TransactionType.PUDO_RECEIVER -> {
                PudoRepository.getInstance().receiverPayment(
                    req,
                    onSuccess = { resp ->
                        tvPrice.text = Util.formatMoney(resp.amount_pay)
                        adapter = PaymentMethodRecyclerAdapter(this@PaymentMethodActivity, resp.payment_method)
                        recyclerView.adapter = adapter
                        hideProgressDialog()
                    },
                    onFailure = { error ->
                        showMessage(error.message!!)
                        hideProgressDialog()
                    }
                )
            }
            else -> {
                val price = intent.getStringExtra("price")!!
                tvPrice.text = price
                PaymentRepository.getInstance().paymentMethod(
                    req,
                    onSuccess = { resp ->
                        adapter = PaymentMethodRecyclerAdapter(this@PaymentMethodActivity, resp)
                        recyclerView.adapter = adapter
                        hideProgressDialog()
                    },
                    onFailure = { error ->
                        showMessage(error)
                        hideProgressDialog()
                    }
                )
            }
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        btnConfirm.setOnClickListener {
            if (adapter.selectedItem == null) {
                showMessage(getString(R.string.please_select_method))
                return@setOnClickListener
            }
            val intent = when (appPref.currentTransactionType) {
                TransactionType.PUDO_SENDER_WALKIN -> {
                    Intent(this@PaymentMethodActivity, PudoWalkInPaymentSummaryActivity::class.java)
                }
                else -> {
                    Intent(this@PaymentMethodActivity, TotalPaymentSummaryActivity::class.java)
                }
            }
            if (etPromoCode.text != null) {
                intent.putExtra("promoCode", etPromoCode.text.toString())
            }
            intent.putExtra("paymentMethod", adapter.selectedItem!!.value)
            intent.putExtra("paymentMethodName", adapter.selectedItem!!.name)
            startActivity(intent)
        }

        etPromoCode.setOnKeyListener(object : OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard()
                    return true
                }
                return false
            }
        })

    }

    override fun onShowKeyboard(keyboardHeight: Int) {
        Util.fullscreen(this.window.decorView)
    }

    override fun onHideKeyboard() {
        Util.fullscreen(this.window.decorView)
    }

    override fun onResume() {
        super.onResume()
        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }
    }
}