package com.lockboxth.lockboxkiosk

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockboxth.lockboxkiosk.adapter.PudoLocationTypeRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.PudoLocationType
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoLocationRequest
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_key_val_selector.*

class KeyValSelectorActivity : BaseActivity() {

    private var items = arrayListOf<PudoKeyVal>()
    private var _items = arrayListOf<PudoKeyVal>()

    lateinit var adapter: PudoLocationTypeRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        autoHideKeyboard = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_key_val_selector)


        showProgressDialog()
        etSearch.visibility = View.GONE

        val parentId = intent.getIntExtra("parent_id", 0)

        when (intent.getSerializableExtra("location_type") as PudoLocationType) {
            PudoLocationType.PROVINCE -> getProvince()
            PudoLocationType.DISTRICT -> getAmphur(parentId)
            PudoLocationType.SUB_DISTRICT -> getTumbon(parentId)
            PudoLocationType.LOCKER_LOCATION -> getLockerLocation()
            else -> {}
        }


    }

    private fun getProvince() {
        tvTitle.text = getString(R.string.select_province)
        val req = PudoLocationRequest(
            appPref.kioskInfo!!.generalprofile_id,
            appPref.currentTransactionId!!,
            province_id = null,
            amphur_id = null
        )
        PudoRepository.getInstance().provinces(
            req,
            onSuccess = { items ->
                this@KeyValSelectorActivity.items = items.province_option!!
                this@KeyValSelectorActivity._items = this@KeyValSelectorActivity.items;
                bindUI()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
            }
        )
    }

    private fun getTumbon(amphurId: Int) {
        tvTitle.text = getString(R.string.select_sub_dist)
        val req = PudoLocationRequest(
            appPref.kioskInfo!!.generalprofile_id,
            appPref.currentTransactionId!!,
            province_id = null,
            amphur_id = amphurId
        )
        PudoRepository.getInstance().subDistrict(
            req,
            onSuccess = { items ->
                this@KeyValSelectorActivity.items = items.district_option!!
                this@KeyValSelectorActivity._items = this@KeyValSelectorActivity.items;
                bindUI()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
            }
        )

    }

    private fun getAmphur(provinceId: Int) {
        tvTitle.text = getString(R.string.select_dist)
        val req = PudoLocationRequest(
            appPref.kioskInfo!!.generalprofile_id,
            appPref.currentTransactionId!!,
            province_id = provinceId,
            amphur_id = null
        )
        PudoRepository.getInstance().district(
            req,
            onSuccess = { items ->
                this@KeyValSelectorActivity.items = items.amphur_option!!
                this@KeyValSelectorActivity._items = this@KeyValSelectorActivity.items;
                bindUI()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
            }
        )
    }

    private fun getLockerLocation() {
        tvTitle.text = getString(R.string.select_location2)
        items = intent.getParcelableArrayListExtra("locker_location")!!
        _items = intent.getParcelableArrayListExtra("locker_location")!!
        bindUI()
        attachSearch()
    }

    private fun attachSearch() {
        etSearch.visibility = View.VISIBLE
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!etSearch.text.isNullOrEmpty()) {
                    items = ArrayList(_items.filter { i -> i.value.lowercase().contains(etSearch.text.toString().lowercase()) })
                } else {
                    items = _items
                }
                adapter.updateItem(items)

            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }


    private fun bindUI() {
        recyclerView.layoutManager = LinearLayoutManager(this@KeyValSelectorActivity)
        adapter = PudoLocationTypeRecyclerAdapter(this@KeyValSelectorActivity, items)
        adapter.setOnClick { s ->
            intent.putExtra("item", s)
            setResult(RESULT_OK, intent)
            finish()
        }
        recyclerView.adapter = adapter
        hideProgressDialog()
    }

}