package com.example.prayertime.ui.media

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentRadioBinding
import com.example.prayertime.databinding.ItemRvBinding
import com.example.prayertime.model.HomeItem


class RadioFragment : Fragment() {
    private var TAG = "RadioFragment"
    private lateinit var binding: FragmentRadioBinding

    private var mPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_radio, container, false)
       setRv()
//        initializeUIElements()
//        initializeMediaPlayer()
        return binding.root
    }

    private fun setRv() {
        val list=getList()
        val adapter=AdapterRadioFragment()
        adapter.setData(list)
        binding.rvRadio.adapter=adapter
    }

//    private fun initializeUIElements() {
//
//        binding.progressBar1.max = 100
//        binding.progressBar1.visibility = View.INVISIBLE
//        binding.buttonPlay.setOnClickListener {
//            startPlaying()
//        }
//        binding.buttonStopPlay.isEnabled = false
//        binding.buttonStopPlay.setOnClickListener(this)
//    }
//
//
//    private fun startPlaying() {
//        binding.buttonStopPlay.isEnabled = true
//        binding.buttonPlay.isEnabled = false
//        binding.progressBar1.visibility = View.VISIBLE
//        mPlayer!!.prepareAsync()
//        mPlayer!!.setOnPreparedListener { mPlayer!!.start() }
//    }

//    private fun stopPlaying() {
//        if (mPlayer!!.isPlaying) {
//            mPlayer!!.stop()
//            mPlayer!!.release()
//            initializeMediaPlayer()
//        }
//        binding.buttonPlay.isEnabled = true
//        binding.buttonStopPlay.isEnabled = false
//        binding.progressBar1.visibility = View.INVISIBLE
//    }

//    private fun initializeMediaPlayer() {
//        mPlayer = MediaPlayer()
//        try {
//            mPlayer!!.setDataSource(" http://onlineradiobox.com/uz/navoonline/player/?cs=uz.navoonline")
//            Log.d(TAG, "initializeMediaPlayer: $mPlayer")
//            mPlayer!!.start()
//        } catch (e: IllegalArgumentException) {
//            e.printStackTrace()
//        } catch (e: IllegalStateException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            startPlaying();
//            e.printStackTrace()
//        }
//        mPlayer!!.setOnBufferingUpdateListener { _, percent ->
//            binding.progressBar1.secondaryProgress = percent
//        }
//    }

//    override fun onPause() {
//        super.onPause()
//        if (mPlayer!!.isPlaying) {
//            mPlayer!!.stop()
//        }
//    }

//    override fun onClick(v: View?) {
//        if (v == binding.buttonPlay) {
//            startPlaying()
//        } else if (v == binding.buttonStopPlay) {
//            stopPlaying()
//        }
//    }


    private fun getList():List<HomeItem>{
        return listOf(
            HomeItem(1,"Azon FM",requireContext().let { ContextCompat.getDrawable(it,R.drawable.azon_fm)!! }),
            HomeItem(2,"Navro'z FM",requireContext().let { ContextCompat.getDrawable(it,R.drawable.navroz_fm)!! }),
        )
    }
}

class AdapterRadioFragment() : RecyclerView.Adapter<AdapterRadioFragment.VH>() {

private var list= listOf<HomeItem>()
    fun setData(list: List<HomeItem>){
        this.list=list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRadioFragment.VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRvBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_rv, parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: AdapterRadioFragment.VH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int =list.size

    class VH (private val binding: ItemRvBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(HomeItem: HomeItem) {
            binding.tvItem.text=HomeItem.text
            binding.ivMain.setImageDrawable(HomeItem.image)

        }

    }
}