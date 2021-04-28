package com.example.prayertime.ui.media

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentAudioBinding

class AudioFragment : Fragment() {

    private lateinit var binding: FragmentAudioBinding
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var radio:MediaStore.Audio.Radio
    private var TAG="AudioFragment"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio, container, false)

        binding.btnPlay.setOnClickListener {
            media()

            Log.d(TAG, "onCreateView: $it")
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun media() {
        val url =
            "http://onlineradiobox.com/uz/navoonline/player/?cs=uz.navoonline" // your URL here

        mediaPlayer?.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setWakeMode(requireContext(), PowerManager.PARTIAL_WAKE_LOCK)
            setDataSource(url)
            setOnBufferingUpdateListener { _, percent ->

            }
            start()
//                prepare() // might take long! (for buffering, etc)


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}


