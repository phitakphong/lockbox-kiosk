package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail
import kotlinx.android.synthetic.main.recycler_total_payment_summary_item.view.*

class ServicePaymentSummaryRecyclerAdapter(
    private val context: Context,
    private var data: Map<String, LockerCalculateDetail>,
    private var showAmt: Boolean = true
) : RecyclerView.Adapter<ServicePaymentSummaryViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicePaymentSummaryViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_total_payment_summary_item, parent, false)
        return ServicePaymentSummaryViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ServicePaymentSummaryViewHolder, position: Int) {
        val key = data.keys.toList()[position]
        val item = data[key]!!
        holder.bindView(key, item, showAmt)
    }

}

@SuppressLint("SetTextI18n")
class ServicePaymentSummaryViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    lateinit var item: LockerCalculateDetail

    fun bindView(size: String, item: LockerCalculateDetail, showAmt: Boolean) {
        this.item = item

        this.view.tvSize.text = size.uppercase()
        this.view.tvTotalLocker.text = this.item.locker_no.count().toString()
        this.view.tvNo.text = "No. " + this.item.locker_no.map { l -> "${size}$l" }.joinToString(", ")
        if (!showAmt) {
            this.view.tvCost.visibility = View.GONE
            this.view.tvTotal.visibility = View.GONE
        } else {
            this.view.tvCost.visibility = View.VISIBLE
            this.view.tvTotal.visibility = View.VISIBLE
            this.view.tvCost.text = "${this.item.rate_hour} ${this.view.context.getString(R.string.bath)}/${this.view.context.getString(R.string.hour2)}"
            this.view.tvTotal.text = "${this.item.sum} ${this.view.context.getString(R.string.bath)}"
        }

    }
}