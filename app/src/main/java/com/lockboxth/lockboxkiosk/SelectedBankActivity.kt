package com.lockboxth.lockboxkiosk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockboxth.lockboxkiosk.adapter.PudoBankRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoBankRequest
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_select_bank_account.*

class SelectedBankActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_bank)

        showProgressDialog()

        PudoRepository.getInstance().bank(
            PudoBankRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
            onSuccess = { resp ->
                val items = ArrayList(resp.bank_list)
                items.removeAt(0)
                recyclerView.layoutManager = LinearLayoutManager(this)
                val adapter = PudoBankRecyclerAdapter(this, items)
                adapter.setOnClick { item ->
                    intent.putExtra("selected", item)
                    setResult(RESULT_OK, intent)
                    finish()
                }
                recyclerView.adapter = adapter
                hideProgressDialog()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
            }
        )


    }

    override fun onResume() {
        super.onResume()
        Util.fullscreen(this.window.decorView)
    }
}