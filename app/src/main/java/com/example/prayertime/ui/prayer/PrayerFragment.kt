package com.example.prayertime.ui.prayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentPrayerBinding
import com.example.prayertime.ui.home.AdapterHome
import com.example.prayertime.ui.home.Model

class PrayerFragment : Fragment(), AdapterHome.RvItemListener {

    private lateinit var binding: FragmentPrayerBinding
    private lateinit var viewModel: PrayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prayer, container, false)
        setRv()
        return binding.root
    }

    private fun setRv() {
        val list = getList()
        val adapter=AdapterHome(this)
        adapter.setData(list)
        binding.rvPrayer.layoutManager=GridLayoutManager(activity,3)
        binding.rvPrayer.adapter=adapter
    }

    private fun getList(): List<Model> {
        return listOf(
            Model(
                1,
                "Hammasi",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            Model(1,
                "Tonggi va kechki ",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!),
            Model(1,
                "Namozdagi duo...",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!),
            Model(1,
                "Uy va oilada",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!),
            Model(1,
                "Qiyinchilik kel...",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!),
            Model(1,
                "Kasal bo'lganda",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!),
            Model(1,
                "Tabiat",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!),
            Model(1,
                "Xaj va Umra",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!),
            Model(1,
                "Sayohat",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!),
            Model(1,
                "Sahobalarning duo...",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!),
            Model(1,
                "Boshqalar",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!),


            )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PrayerViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onClicked(model: Model) {
        TODO("Not yet implemented")
    }

}