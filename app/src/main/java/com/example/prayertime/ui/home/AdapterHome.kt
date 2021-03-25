package com.example.prayertime.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertime.R
import com.example.prayertime.databinding.ItemRvBinding

class AdapterHome(private val listener:RvItemListener):RecyclerView.Adapter<AdapterHome.VH>() {


    interface RvItemListener{
        fun onClicked(model: Model)
    }

private var list= listOf<Model>()
    fun setData(list: List<Model>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHome.VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding:ItemRvBinding = DataBindingUtil.inflate(inflater, R.layout.item_rv,parent,false)
        return VH(binding)

    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener {
            listener.onClicked(list[position])
        }
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size


    class VH(private val binding:ItemRvBinding) :RecyclerView.ViewHolder(binding.root){
        fun onBind(model:Model){
            with(binding) {
                this.tvItem.text =model.text
                this.ivMain.setImageDrawable(model.image)

            }
        }

    }
}