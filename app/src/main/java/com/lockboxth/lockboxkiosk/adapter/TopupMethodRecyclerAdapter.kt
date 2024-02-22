package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.topup.TopupMethod
import kotlinx.android.synthetic.main.recycler_payment_method_item.view.*

class TopupMethodRecyclerAdapter(
    private val context: Context,
    private var data: ArrayList<TopupMethod>
) : RecyclerView.Adapter<TopupMethodViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var selectedItem: TopupMethod? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopupMethodViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_payment_method_item, parent, false)
        return TopupMethodViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TopupMethodViewHolder, position: Int) {
        val item = data[position]
        val itemSelected = selectedItem?.value == item.value
        holder.bindView(item, itemSelected)
        holder.onClick { s ->
            selectedItem = s
            notifyDataSetChanged()
        }

    }

}

@SuppressLint("SetTextI18n")
class TopupMethodViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    lateinit var item: TopupMethod

    fun bindView(item: TopupMethod, selected: Boolean) {
        this.item = item

        Util.loadImage(item.img_dat, this.view.img)
        this.view.tvName.text = this.item.name
        if (this.item.extra_amount != "0") {
            this.view.discountBadge.visibility = View.VISIBLE
            when (this.item.extra_unit) {
                "percent" -> {
                    this.view.tvDiscount.text = this.item.extra_amount + "%"
                }
                "baht" -> {
                    this.view.tvDiscount.text = "฿" + this.item.extra_amount
                }
            }
        } else {
            this.view.discountBadge.visibility = View.GONE
        }
        if(selected){
            this.view.listItem.background = ContextCompat.getDrawable(this.view.context,R.drawable.card_item_white_border)
        }else{
            this.view.listItem.background = ContextCompat.getDrawable(this.view.context,R.drawable.card_item_white)
        }

    }

    fun onClick(onClick: (TopupMethod) -> Unit) {
        this.view.listItem.setOnClickListener {
            onClick(this@TopupMethodViewHolder.item)
        }
    }


}