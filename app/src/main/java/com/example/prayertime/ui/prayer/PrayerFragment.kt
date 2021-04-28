package com.example.prayertime.ui.prayer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertime.PRAYER_ID
import com.example.prayertime.R
import com.example.prayertime.TITLE_ID
import com.example.prayertime.databinding.FragmentPrayerBinding
import com.example.prayertime.databinding.ItemPrayerBinding
import com.example.prayertime.model.PrayerText
import com.google.gson.Gson

class PrayerFragment : Fragment(), AdapterPrayer.RvItemListener {
    private lateinit var binding: FragmentPrayerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prayer, container, false)
        setRv()
        return binding.root
    }

    private fun setRv() {

        val oldList = Prayer().getList()
        val newList = separateListByTitleId(oldList)
        val adapter = AdapterPrayer(this)
        adapter.setData(oldList)
        binding.rvPrayerName.adapter = adapter
    }

    private fun separateListByTitleId(list: List<PrayerText>) {

        val changedList = arrayListOf<PrayerText>()
        val _t = arguments?.getInt(TITLE_ID)
        for (i in list.indices) {

            if (list[i].titleId == _t){


            }

        }
    }


    override fun onClicked(prayerText: PrayerText) {

        val jsonString = Gson().toJson(prayerText)
        findNavController().navigate(
                R.id.prayerList, bundleOf(PRAYER_ID to jsonString)
            )
    }
}


class AdapterPrayer(private val listener: RvItemListener) :
    RecyclerView.Adapter<AdapterPrayer.VH>() {

    interface RvItemListener {
        fun onClicked(prayerText: PrayerText)
    }

    private var list = listOf<PrayerText>()

    fun setData(list: List<PrayerText>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPrayer.VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemPrayerBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_prayer,
            parent,
            false
        )
        return VH(binding, parent.context)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener {
            listener.onClicked(list[position])
        }
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    class VH(private val binding: ItemPrayerBinding, private val context: Context?) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(prayerText: PrayerText) {
            binding.prayerName = prayerText
        }

    }
}