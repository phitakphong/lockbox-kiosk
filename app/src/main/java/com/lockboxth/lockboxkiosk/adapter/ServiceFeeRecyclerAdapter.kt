package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.MyPrefs
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCalculateDetail
import kotlinx.android.synthetic.main.recycler_service_fee_item.view.*

class ServiceFeeRecyclerAdapter(
    private val context: Context,
    private var data: Map<String, LockerCalculateDetail>,
    private var minService: Int
) : RecyclerView.Adapter<ServiceFeeViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceFeeViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_service_fee_item, parent, false)
        return ServiceFeeViewHolder(inflaterView, context)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ServiceFeeViewHolder, position: Int) {
        val key = data.keys.toList()[position]
        val item = data[key]!!
        holder.bindView(key, item, this.minService)
    }

}

@SuppressLint("SetTextI18n")
class ServiceFeeViewHolder(v: View, context: Context) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    lateinit var item: LockerCalculateDetail
    private val pref = MyPrefs(context)

    fun bindView(size: String, item: LockerCalculateDetail, minService: Int) {
        this.item = item

        Util.setIconSize(size, this.view.imgLockerSize)

        this.view.tvSize.text = size.uppercase() + " " + this.item.locker_no.count()
        this.view.tvText1.text = "Loker No. " + this.item.locker_no.map { l -> "${size}$l" }.joinToString(", ")
        this.view.tvText2.text = this.view.context.getString(R.string.fee_p_h) + " ${this.item.rate_hour} ${this.view.context.getString(R.string.bath)} / ${this.view.context.getString(R.string.hour)}"
        if (pref.currentTransactionType == TransactionType.GO_OUT) {
            this.view.tvText3.text = "${this.view.context.getString(R.string.fee_over)} $minService ${this.view.context.getString(R.string.hour)}"
        } else {
            this.view.tvText3.text = "${this.view.context.getString(R.string.minimum_fee)} $minService ${this.view.context.getString(R.string.hour)}"
        }
        this.view.tvText4.text =
            "${this.view.context.getString(R.string.fee_per_day)} ${this.item.rate_day} ${this.view.context.getString(R.string.bath)} / ${this.view.context.getString(R.string.day)}"
        this.view.tvText5.text = "${this.item.sum} ${this.view.context.getString(R.string.bath)}"

    }


}