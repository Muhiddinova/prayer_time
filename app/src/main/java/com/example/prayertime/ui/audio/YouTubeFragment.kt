package com.example.prayertime.ui.audio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentYouTubeBinding
import com.example.prayertime.databinding.YouTubeItemBinding
import com.example.prayertime.ui.home.Model
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class YouTubeFragment : Fragment() ,AdapterYouTube.YouTubeItemListener{
    private lateinit var binding:FragmentYouTubeBinding
    private   var youTubePlayerView=YouTubePlayerView(requireContext())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_you_tube, container, false)
        youTubePlayerView= YouTubePlayerView(requireContext())
        lifecycle.addObserver(youTubePlayerView)
        setRv()
        return binding.root
    }

    private fun setRv() {
        val list=getList()
        val adapter=AdapterYouTube(this)
        adapter.setData(list)
        binding.rvYouTube.adapter=adapter
    }

    private fun getList(): List<YouTubeModel> {
        return listOf(
          YouTubeModel(1,"GiqdBLENYwk"),
            YouTubeModel(2,"GiqdBLENYwk"),
            YouTubeModel(3,"GiqdBLENYwk")
        )
    }

    override fun onClicked(model: YouTubeModel) {
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(model.videoId, 0f)
            }
        })}


    }










class AdapterYouTube(private val listener: YouTubeItemListener): RecyclerView.Adapter<AdapterYouTube.VH>() {
    interface YouTubeItemListener{

        fun onClicked(model: YouTubeModel)
    }
    private var list= listOf<YouTubeModel>()
    fun setData(list: List<YouTubeModel>) {
        this.list = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterYouTube.VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: YouTubeItemBinding= DataBindingUtil.inflate(
            inflater,
            R.layout.you_tube_item,
            parent,
            false
        )
        return VH(binding)

    }

    override fun onBindViewHolder(holder: AdapterYouTube.VH, position: Int) {
        holder.itemView.setOnClickListener {
            listener.onClicked(list[position])
        }
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int =list.size
class VH(private val binding: YouTubeItemBinding):RecyclerView.ViewHolder(binding.root){
    fun onBind(model: YouTubeModel){

    }
}


}




