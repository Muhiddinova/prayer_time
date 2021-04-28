package com.example.prayertime.ui.prayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.prayertime.R
import com.example.prayertime.TITLE_ID
import com.example.prayertime.databinding.FragmentPrayerNameBinding
import com.example.prayertime.model.HomeItem
import com.example.prayertime.ui.home.AdapterHome

class PrayerNameFragment : Fragment(), AdapterHome.RvItemListener {

    private lateinit var binding: FragmentPrayerNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prayer_name, container, false)
        setRv()
        return binding.root
    }

    override fun onClicked(HomeItem: HomeItem) {

        findNavController().navigate(
            R.id.prayerFragment, bundleOf(TITLE_ID to HomeItem.id)
        )


    }

    private fun setRv() {
        val list = getList()
        val adapter = AdapterHome(this)
        adapter.setData(list)
        binding.rvPrayer.layoutManager = GridLayoutManager(activity, 3)
        binding.rvPrayer.adapter = adapter
    }

    private fun getList(): List<HomeItem> {
        return listOf(
            HomeItem(
                1,
                "Hammasi",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            HomeItem(
                2,
                "Tonggi va kechki ",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            HomeItem(
                3,
                "Namozdagi duolar",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            HomeItem(
                4,
                "Uy va oilada",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            HomeItem(
                5,
                "Qiyinchilik kelganda",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            HomeItem(
                6,
                "Kasal bo'lganda",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            HomeItem(
                7,
                "Tabiat",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            HomeItem(
                8,
                "Xaj va Umra",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            HomeItem(
                9,
                "Sayohat",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            HomeItem(
                10,
                "Sahobalarning duolari",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),
            HomeItem(
                11,
                "Boshqalar",
                requireContext().let {
                    ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads)
                }!!
            ),


            )
    }


}