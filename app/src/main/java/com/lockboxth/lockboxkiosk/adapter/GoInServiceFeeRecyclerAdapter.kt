package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.MyPrefs
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail
import kotlinx.android.synthetic.main.recycler_go_in_service_fee_item.view.*

class GoInServiceFeeRecyclerAdapter(
    private val context: Context,
    private var data: Map<String, LockerCalculateDetail>
) : RecyclerView.Adapter<GoInServiceFeeViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoInServiceFeeViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_go_in_service_fee_item, parent, false)
        return GoInServiceFeeViewHolder(inflaterView, context)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: GoInServiceFeeViewHolder, position: Int) {
        val key = data.keys.toList()[position]
        val item = data[key]!!
        holder.bindView(key, item)
    }

}

@SuppressLint("SetTextI18n")
class GoInServiceFeeViewHolder(v: View, context: Context) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    lateinit var item: LockerCalculateDetail
    private val pref = MyPrefs(context)

    fun bindView(size: String, item: LockerCalculateDetail) {
        this.item = item

        Util.setIconSize(size, this.view.imgLockerSize)

        this.view.tvSize.text = size.uppercase() + " = " + this.item.locker_no.count()
        this.view.tvText1.text = "Loker No. " + this.item.locker_no.map { l -> "${size}$l" }.joinToString(", ")

        this.view.tvText4.text =
            "${this.view.context.getString(R.string.fee_per_day)} ${this.item.rate_day} ${this.view.context.getString(R.string.bath)} / ${this.view.context.getString(R.string.day)}"
        this.view.tvText5.text = "${this.item.sum} ${this.view.context.getString(R.string.bath)}"

    }


}