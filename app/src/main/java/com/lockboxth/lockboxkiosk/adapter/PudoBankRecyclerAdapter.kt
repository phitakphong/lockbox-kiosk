package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import kotlinx.android.synthetic.main.recycler_name_item.view.*

class PudoBankRecyclerAdapter(
    private val context: Context, private var data: ArrayList<PudoKeyVal>
) : RecyclerView.Adapter<PudoBankViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var onClick: ((PudoKeyVal) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PudoBankViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_name_item, parent, false)
        return PudoBankViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PudoBankViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item)
        holder.onClick {
            this.onClick!!(item)
        }
    }

    fun setOnClick(onClick: ((PudoKeyVal) -> Unit)) {
        this.onClick = onClick
    }

}

@SuppressLint("SetTextI18n")
class PudoBankViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v

    fun bindView(item: PudoKeyVal) {
        this.view.tvName.text = item.value
    }

    fun onClick(onClick: () -> Unit) {
        this.view.listItem.setOnClickListener {
            onClick()
        }
    }


}