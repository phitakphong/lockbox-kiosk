package com.lockboxth.lockboxkiosk

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockboxth.lockboxkiosk.adapter.PudoBankAccountRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoBankAccount
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import kotlinx.android.synthetic.main.activity_pudo_parcel_detail.*
import kotlinx.android.synthetic.main.activity_select_bank_account.*

class SelectBankAccountActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_bank_account)
    }

    private fun init() {
        val items = ArrayList(PudoParcelDetailActivity.bankItems)
        items.add(PudoBankAccount(-1, getString(R.string.add_new_acc), "", "", "", ""))
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PudoBankAccountRecyclerAdapter(this, items)
        adapter.setOnClick { item ->
            if (item.bankacc_id == -1) {
                val intent = Intent(this@SelectBankAccountActivity, PudoAddBankAccountActivity::class.java)
                addBankResultLauncher.launch(intent)
            } else {
                PudoParcelDetailActivity.selectedBankAccount = item
                finish()
            }
        }
        recyclerView.adapter = adapter
    }

    private var addBankResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        init()
    }

}