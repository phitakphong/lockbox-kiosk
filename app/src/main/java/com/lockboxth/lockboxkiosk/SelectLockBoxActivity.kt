package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lockboxth.lockboxkiosk.adapter.TransactionRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.go.GoLockerLayoutRequest
import com.lockboxth.lockboxkiosk.http.model.locker.ConfirmSelectLockerRequest
import com.lockboxth.lockboxkiosk.http.model.locker.LockerLayoutResponse
import com.lockboxth.lockboxkiosk.http.model.locker.LockerOutRequest
import com.lockboxth.lockboxkiosk.http.model.locker.SlotItem
import com.lockboxth.lockboxkiosk.http.repository.GoRepository
import com.lockboxth.lockboxkiosk.http.repository.LockerRepository
import kotlinx.android.synthetic.main.activity_select_lockbox.*


class SelectLockBoxActivity : BaseActivity() {

    private val sizeMap: HashMap<String, Int> = HashMap()
    private val statusMap: HashMap<String, Int> = hashMapOf()
    private val selectedLocker: HashMap<Int, Boolean> = HashMap()
    private var lockerLayoutData: LockerLayoutResponse? = null
    private var transactionAdapter: TransactionRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_lockbox)

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        btnBack.setOnClickListener {
            onCancel()
        }

        getLockerLayout()

        btnLeft.setOnClickListener {
            hScroll.post {
                hScroll.smoothScrollTo(hScroll.scrollX - 360, 0)
            }
        }

        btnRight.setOnClickListener {
            hScroll.post {
                hScroll.smoothScrollTo(hScroll.scrollX + 360, 0)
            }
        }

        btnReset.setOnClickListener {
            if (appPref.currentTransactionType == TransactionType.IN || appPref.currentTransactionType == TransactionType.GO_IN) {
                generateLockerLayout()
            }
        }

        initBtnConfirm()

    }

    private fun initBtnConfirm() {
        when (appPref.currentTransactionType) {
            TransactionType.IN -> {
                vTransaction.visibility = View.GONE
                layoutTransaction.visibility = View.GONE
                btnConfirm.setOnClickListener {

                    val selectedCount = selectedLocker.values.count { s -> s }

                    if (selectedCount == 0) {
                        showMessage(getString(R.string.select_locker2))
                        return@setOnClickListener
                    }

                    showProgressDialog()

                    val blockUses: ArrayList<Int> = arrayListOf()
                    selectedLocker.keys.forEach { k ->
                        if (selectedLocker[k] == true) {
                            blockUses.add(k)
                        }
                    }

                    val req = ConfirmSelectLockerRequest(
                        appPref.kioskInfo!!.generalprofile_id,
                        blockUses,
                        appPref.currentTransactionId!!
                    )
                    LockerRepository.getInstance().confirmIn(
                        req,
                        onSuccess = {
                            hideProgressDialog()
                            val intent = Intent(this@SelectLockBoxActivity, RegisterActivity::class.java)
                            startActivity(intent)
                        },
                        onFailure = { error ->
                            hideProgressDialog()
                            showMessage(error)
                        }
                    )
                }
            }
            TransactionType.GO_IN -> {

                tvTitle.text = getString(R.string.select_locker_go_in)
                vTransaction.visibility = View.GONE
                layoutTransaction.visibility = View.GONE
                btnConfirm.setOnClickListener {
                    val selectedCount = selectedLocker.values.count { s -> s }
                    if (selectedCount == 0) {
                        showMessage(getString(R.string.select_locker_go_in_require))
                        return@setOnClickListener
                    }
                    var blockUse = 0
                    selectedLocker.keys.forEach { k ->
                        if (selectedLocker[k] == true) {
                            blockUse = k
                            return@forEach
                        }
                    }
                    val intent = Intent(this@SelectLockBoxActivity, ServiceFeeSummaryActivity::class.java)
                    intent.putExtra("block_use", blockUse)
                    startActivity(intent)
                    finish()
                }
            }
            else -> {
                tvTitle.text = getString(R.string.select_locker_out2)
                btnConfirm.setOnClickListener {
                    if (transactionAdapter!!.selectedItems.size == 0) {
                        showMessage(getString(R.string.select_locker_out3))
                        return@setOnClickListener
                    }
                    val blockUse = transactionAdapter!!.selectedItems
                    val req = ConfirmSelectLockerRequest(appPref.kioskInfo!!.generalprofile_id, blockUse, appPref.currentTransactionId!!)
                    showProgressDialog()
                    LockerRepository.getInstance().generalOutVerify(
                        req,
                        onSuccess = {
                            hideProgressDialog()
                            val intent = Intent(this@SelectLockBoxActivity, TotalPaymentSummaryActivity::class.java)
                            intent.putExtra("calcTotal", true)
                            startActivity(intent)
                            finish()
                        },
                        onFailure = { error ->
                            hideProgressDialog()
                            showMessage(error)
                        }
                    )
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun generateLockerLayout() {

        statusMap["vacant"] = 0
        statusMap["used"] = 0
        statusMap["booked"] = 0
        statusMap["maintenance"] = 0
        statusMap["monitor"] = 0

        selectedLocker.clear()
        sizeMap.clear()

        lockerContainer.removeAllViews()

        lockerLayoutData!!.lockers.forEach { lockerItem ->
            when (lockerItem.slot_type) {
                "y" -> {
                    val parent = LinearLayoutCompat(this@SelectLockBoxActivity)
                    parent.layoutParams = LinearLayoutCompat.LayoutParams(140, LinearLayoutCompat.LayoutParams.MATCH_PARENT)
                    parent.orientation = LinearLayoutCompat.VERTICAL
                    parent.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.white))
                    val slotJson = Gson().toJson(lockerItem.slots)
                    val slots = Gson().fromJson<ArrayList<SlotItem>>(slotJson, object : TypeToken<ArrayList<SlotItem>>() {}.type)
                    slots.forEach { slot ->
                        val locker: LinearLayoutCompat = buildLockerItem(slot, lockerItem.slot_type)
                        parent.addView(locker)
                    }
                    lockerContainer.addView(parent)
                }
                "x" -> {
                    val slotJson = Gson().toJson(lockerItem.slots)
                    val slots = Gson().fromJson<ArrayList<SlotItem>>(slotJson, object : TypeToken<ArrayList<SlotItem>>() {}.type)
                    slots.forEach { slot ->
                        val parent = LinearLayoutCompat(this@SelectLockBoxActivity)
                        parent.layoutParams = LinearLayoutCompat.LayoutParams(140, LinearLayoutCompat.LayoutParams.MATCH_PARENT)
                        parent.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.white))
                        parent.orientation = LinearLayoutCompat.VERTICAL
                        parent.weightSum = 4f
                        val locker: LinearLayoutCompat = buildLockerItem(slot, lockerItem.slot_type)
                        val dummy = LinearLayoutCompat(this@SelectLockBoxActivity)
                        dummy.layoutParams = LinearLayoutCompat.LayoutParams(
                            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                            0,
                            parent.weightSum - (locker.layoutParams as LinearLayoutCompat.LayoutParams).weight
                        )
                        dummy.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.black))
                        parent.addView(dummy)
                        parent.addView(locker)
                        lockerContainer.addView(parent)
                    }
                }
                "xy" -> {
                    val slotJson = Gson().toJson(lockerItem.slots)
                    val slots = Gson().fromJson<ArrayList<ArrayList<SlotItem>>>(slotJson, object : TypeToken<ArrayList<ArrayList<SlotItem>>>() {}.type)

                    val parent = LinearLayoutCompat(this@SelectLockBoxActivity)
                    parent.layoutParams = LinearLayoutCompat.LayoutParams(190, LinearLayoutCompat.LayoutParams.MATCH_PARENT)
                    parent.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.white))
                    parent.orientation = LinearLayoutCompat.VERTICAL

                    slots.forEach { s ->

                        val child = LinearLayoutCompat(this@SelectLockBoxActivity)
                        child.layoutParams = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0, 1f)
                        child.orientation = LinearLayoutCompat.HORIZONTAL
//                        child.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.green))
                        s.forEach { slot ->
                            val locker: LinearLayoutCompat = buildLockerItem(slot, lockerItem.slot_type)
                            child.addView(locker)
                        }

                        parent.addView(child)
                    }
                    lockerContainer.addView(parent)
                }
            }
        }

//        getSummaryItem()

        tvVacantTotal.text = "(${lockerLayoutData!!.cnt_available})"
        tvUsedTotal.text = "(${lockerLayoutData!!.cnt_use})"
        tvReserveTotal.text = "(${lockerLayoutData!!.cnt_book})"

    }

    @SuppressLint("SetTextI18n")
    private fun getLockerLayout() {
        showProgressDialog()
        if (appPref.currentTransactionType == TransactionType.IN) {
            LockerRepository.getInstance().generalLayoutIn(
                appPref.kioskInfo!!.generalprofile_id,
                onSuccess = { resp ->
                    lockerLayoutData = resp
                    appPref.currentTransactionId = resp.txn
                    generateLockerLayout()
                    hideProgressDialog()
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error)
                }
            )
        } else if (appPref.currentTransactionType == TransactionType.GO_IN) {
            GoRepository.getInstance().goLockerLayout(
                GoLockerLayoutRequest(
                    appPref.kioskInfo!!.generalprofile_id,
                    appPref.currentTransactionId!!
                ),
                onSuccess = { resp ->
                    lockerLayoutData = LockerLayoutResponse(
                        txn = "",
                        cnt_available = resp.cnt_available,
                        cnt_use = resp.cnt_use,
                        cnt_book = resp.cnt_book,
                        lockers = resp.lockers,
                        locker_size_details = resp.locker_size_details,
                        locker_name = null,
                        locker_transaction = null
                    )
                    generateLockerLayout()
                    hideProgressDialog()
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error)
                }
            )
        } else if (appPref.currentTransactionType == TransactionType.OUT) {
            val req = LockerOutRequest(
                appPref.kioskInfo!!.generalprofile_id,
                appPref.currentTransactionId!!
            )
            LockerRepository.getInstance().generalLayoutOut(
                req,
                onSuccess = { resp ->
                    lockerLayoutData = resp
                    generateLockerLayout()
                    recyclerView.layoutManager = LinearLayoutManager(this@SelectLockBoxActivity)
                    transactionAdapter = TransactionRecyclerAdapter(this@SelectLockBoxActivity, resp.locker_transaction!!)
                    recyclerView.adapter = transactionAdapter
                    hideProgressDialog()
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error)
                }
            )
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getSummaryItem() {

        summaryContainer.removeAllViews()
        var leftId: Int? = null
        var index = 0
        lockerLayoutData!!.locker_size_details.forEach { s ->
            val sizeSummary = layoutInflater.inflate(R.layout.recycler_locker_summary_item, null) as ConstraintLayout
            sizeSummary.id
            var params = ConstraintLayout.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            sizeSummary.layoutParams = params
            val icon = sizeSummary.findViewById<ImageView>(R.id.imgSummarySize)
            val text = sizeSummary.findViewById<TextView>(R.id.tvSummarySize)
            Util.setIconSize(s.size, icon)

            text.text = "${s.size} = ${s.count}"
            if (leftId == null) {
                leftId = View.generateViewId()
            } else {
                params = sizeSummary.layoutParams as ConstraintLayout.LayoutParams
                params.leftToRight = leftId!!
                sizeSummary.requestLayout()
                leftId = View.generateViewId()
            }
            sizeSummary.id = leftId!!
            summaryContainer.addView(sizeSummary)

        }
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun buildLockerItem(item: SlotItem, slotType: String): LinearLayoutCompat {

        val locker: LinearLayoutCompat = layoutInflater.inflate(R.layout.recycler_locker_item, null) as LinearLayoutCompat
        val lockerNo = locker.findViewById<TextView>(R.id.lockerNo)
        val lockerIcon = locker.findViewById<ImageView>(R.id.icLockerIcon)
        lockerNo.text = "${item.size}${item.locker_no}"
        var weight = 0f
        when (item.size) {
            "S", "SS", "SX" -> {
                weight = 1f
            }
            "M", "MX" -> {
                weight = 1.5f
            }
            "L" -> {
                weight = 2f
            }
            "XL" -> {
                weight = 2.5f
            }
            "E", "EX" -> {
                weight = 3f
            }
            else -> {
                weight = 1f
            }
        }

        var param = LinearLayoutCompat.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            weight
        )

        if (slotType == "xy") {
            lockerIcon.visibility = View.GONE
            param = LinearLayoutCompat.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                weight
            )
        }

        param.topMargin = 2
        param.bottomMargin = 2
        param.leftMargin = 2
        param.rightMargin = 2

        when (item.status) {
            "vacant" -> {
                locker.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.yellow))
                lockerIcon.setImageDrawable(ContextCompat.getDrawable(this@SelectLockBoxActivity, R.drawable.ic_unlock))
            }
            "maintenance",
            "used" -> {
                locker.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.red))
                lockerIcon.setImageDrawable(ContextCompat.getDrawable(this@SelectLockBoxActivity, R.drawable.ic_lock))
            }
            "monitor" -> {
                locker.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.black))
                lockerIcon.visibility = View.GONE
                lockerNo.visibility = View.GONE
                param.topMargin = 0
                param.bottomMargin = 0
                param.leftMargin = 0
                param.rightMargin = 0
            }
            else -> {
                locker.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.purple))
                lockerIcon.setImageDrawable(ContextCompat.getDrawable(this@SelectLockBoxActivity, R.drawable.ic_reserve))
            }
        }

        statusMap[item.status] = statusMap[item.status]!! + 1

        if (!sizeMap.containsKey(item.size)) {
            sizeMap[item.size] = 0
        }
        sizeMap[item.size] = sizeMap[item.size]!! + 1

        if (appPref.currentTransactionType == TransactionType.IN || appPref.currentTransactionType == TransactionType.GO_IN) {
            locker.setOnClickListener {

                val maxSelected = if (appPref.currentTransactionType == TransactionType.IN) {
                    3
                } else {
                    1
                }

                if (!item.can_select) {
                    return@setOnClickListener
                }
                val selectedCount = selectedLocker.values.count { s -> s }
                if (selectedCount == maxSelected && (!selectedLocker.containsKey(item.block_use) || selectedLocker[item.block_use] == false)) {
                    return@setOnClickListener
                }

                if (!selectedLocker.containsKey(item.block_use)) {
                    selectedLocker[item.block_use] = true
                } else {
                    selectedLocker[item.block_use] = !selectedLocker[item.block_use]!!
                }

                if (selectedLocker[item.block_use] == true) {
                    locker.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.green))
                } else {
                    locker.setBackgroundColor(ContextCompat.getColor(this@SelectLockBoxActivity, R.color.yellow))
                }
            }
        }

        locker.layoutParams = param
        return locker
    }

    var firstResume = true
    override fun onResume() {
        super.onResume()
        if (!firstResume) {
            this.timerResume()
        }
        firstResume = false

    }
}