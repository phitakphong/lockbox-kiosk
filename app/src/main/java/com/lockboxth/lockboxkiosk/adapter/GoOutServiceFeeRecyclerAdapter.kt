package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.go.GoPaymentConfirmDetail
import kotlinx.android.synthetic.main.recycler_go_out_service_fee_item.view.*

class GoOutServiceFeeRecyclerAdapter(
    private val context: Context,
    private var data: Map<String, GoPaymentConfirmDetail>
) : RecyclerView.Adapter<GoOutServiceFeeViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoOutServiceFeeViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_go_out_service_fee_item, parent, false)
        return GoOutServiceFeeViewHolder(inflaterView, context)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: GoOutServiceFeeViewHolder, position: Int) {
        val key = data.keys.toList()[position]
        val item = data[key]!!
        holder.bindView(key, item)
    }

}

@SuppressLint("SetTextI18n")
class GoOutServiceFeeViewHolder(v: View, private val context: Context) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    lateinit var item: GoPaymentConfirmDetail

    fun bindView(size: String, item: GoPaymentConfirmDetail) {
        this.item = item

        Util.setIconSize(size, this.view.imgLockerSize)

        this.view.tvLockerNo.text = size.uppercase()
        this.view.tvTotalPcs.text = "${context.getString(R.string.parcel)} ${item.parcel_cnt} ${this.context.getString(R.string.pcs)}"
        this.view.tvTimeUse.text = "${context.getString(R.string.time_use)} ${item.time_use_hour} ${context.getString(R.string.hour2)} ${item.time_use_min} ${context.getString(R.string.mm)}"
        this.view.tvTimeOver.text = context.getString(R.string.time_over).replace("XXX", Util.formatMoney(item.time_over.toFloat()))
        this.view.tvCost.text =
            "${this.view.context.getString(R.string.fee_per_day)} ${this.item.rate_day} ${this.view.context.getString(R.string.bath)} / ${this.view.context.getString(R.string.day)}"
        this.view.tvTotalCost.text = "${this.item.sum} ${this.view.context.getString(R.string.bath)}"

    }


}