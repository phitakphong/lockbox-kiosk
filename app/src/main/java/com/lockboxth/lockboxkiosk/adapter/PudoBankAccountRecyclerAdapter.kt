package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoBankAccount
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import kotlinx.android.synthetic.main.recycler_name_item.view.*

class PudoBankAccountRecyclerAdapter(
    private val context: Context,
    private var data: ArrayList<PudoBankAccount>
) : RecyclerView.Adapter<PudoBankAccountViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var onClick: ((PudoBankAccount) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PudoBankAccountViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_name_item, parent, false)
        return PudoBankAccountViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PudoBankAccountViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item)
        holder.onClick {
            this.onClick!!(item)
        }
    }

    fun setOnClick(onClick: ((PudoBankAccount) -> Unit)) {
        this.onClick = onClick
    }

}

@SuppressLint("SetTextI18n")
class PudoBankAccountViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v

    fun bindView(item: PudoBankAccount) {
        this.view.img.visibility = View.VISIBLE
        Util.loadImage(item.bank_image, this.view.img)
        this.view.tvName.text = " ${item.bank_name} ${item.censor_account_id}"
    }

    fun onClick(onClick: () -> Unit) {
        this.view.listItem.setOnClickListener {
            onClick()
        }
    }


}