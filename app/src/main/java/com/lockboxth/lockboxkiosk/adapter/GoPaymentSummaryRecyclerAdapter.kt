package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.http.model.go.GoPaymentConfirmDetail
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.recycler_go_payment_summary_item.view.*

class GoPaymentSummaryRecyclerAdapter(
    private val context: Context,
    private var data: Map<String, GoPaymentConfirmDetail>,
) : RecyclerView.Adapter<GoPaymentSummaryViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoPaymentSummaryViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_go_payment_summary_item, parent, false)
        return GoPaymentSummaryViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: GoPaymentSummaryViewHolder, position: Int) {
        val key = data.keys.toList()[position]
        val item = data[key]!!
        holder.bindView(key, item)
    }

}

@SuppressLint("SetTextI18n")
class GoPaymentSummaryViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    lateinit var item: GoPaymentConfirmDetail
    private val context = v.context

    fun bindView(size: String, item: GoPaymentConfirmDetail) {
        this.item = item
        this.view.tvLockerNo.text = size.uppercase()
        this.view.tvTotal.text = "${this.item.parcel_cnt} ${this.view.context.getString(R.string.pcs)}"
        this.view.tvOver.text = "${this.context.getString(R.string.over)} ${this.item.time_over} ${this.context.getString(R.string.day)}"
        this.view.tvCost.text = "${this.item.rate_day} ${this.view.context.getString(R.string.bath)}/${this.view.context.getString(R.string.day)}"
        this.view.tvTotalCost.text = "${this.item.sum} ${this.view.context.getString(R.string.bath)}"

    }
}