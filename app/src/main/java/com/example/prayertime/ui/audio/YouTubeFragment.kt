package com.example.prayertime.ui.audio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentYouTubeBinding
import com.example.prayertime.databinding.ItemYouTubeBinding
import com.example.prayertime.model.YouTubeModel
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer


class YouTubeFragment : Fragment() {
    private lateinit var binding: FragmentYouTubeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_you_tube, container, false)

        setRv()
        return binding.root
    }

    private fun setRv() {
        val list = getList()
        val adapter = AdapterYouTube(requireContext())
        adapter.setData(list)
        binding.rvYouTube.adapter = adapter
    }

    private fun getList(): List<YouTubeModel> {
        return listOf(
            YouTubeModel(1, "P-5Ezbgtc-I&feature"),
            YouTubeModel(2, "Qx8pmRHDvo8"),
            YouTubeModel(3, "Qx8pmRHDvo8"),
            YouTubeModel(4, "NY-oqzisnlE"),
            YouTubeModel(5, "IkboRpFZK98"),
            YouTubeModel(6, "Qx8pmRHDvo8"),
            YouTubeModel(7, "Qx8pmRHDvo8"),

            )
    }


}


class AdapterYouTube(context: Context) :
    RecyclerView.Adapter<AdapterYouTube.VH>() {


    private val inflater = LayoutInflater.from(context)


    private var list = listOf<YouTubeModel>()
    fun setData(list: List<YouTubeModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterYouTube.VH {
        val binding: ItemYouTubeBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_you_tube,
            parent,
            false
        )
        return VH(binding, parent.context)

    }

    override fun onBindViewHolder(holder: VH, position: Int) {
//        holder.itemView.setOnClickListener {
//            listener.onClicked(list[position])
//        }
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
    class VH(private val binding: ItemYouTubeBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: YouTubeModel) {
            binding.player.initialize(context.getString(R.string.google_maps_key),
                object : YouTubePlayer.OnInitializedListener {
                    override fun onInitializationSuccess(
                        provider: YouTubePlayer.Provider?,
                        player: YouTubePlayer?,
                        wasRestored: Boolean
                    ) {
                        player?.loadPlaylist(model.videoId)
                        player?.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                    }

                    override fun onInitializationFailure(
                        p0: YouTubePlayer.Provider?,
                        p1: YouTubeInitializationResult?
                    ) {
                        Toast.makeText(context, "initializing failure", Toast.LENGTH_SHORT).show()
                    }

                })
//            binding.video.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//                override fun onReady(youTubePlayer: YouTubePlayer) {
//                    youTubePlayer.loadVideo(model.videoId, 0f)
//                }
//            })
        }
    }


}




