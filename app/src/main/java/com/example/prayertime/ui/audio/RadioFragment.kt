package com.example.prayertime.ui.audio

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentRadioBinding
import java.io.IOException


class RadioFragment : Fragment() {
    private lateinit var binding:FragmentRadioBinding

    private var player: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_radio, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeUIElements()
        initializeMediaPlayer()
    }

    private fun initializeUIElements() {

      binding.progressBar1.max = 100
        binding.progressBar1.visibility = View.INVISIBLE
//        binding.buttonPlay.setOnClickListener(onClick(v:View))
        binding.buttonStopPlay.setEnabled(false)
//        binding.buttonStopPlay.setOnClickListener()
    }

    fun onClick(v: View) {
        if (v ===binding.buttonPlay) {
            startPlaying()
        } else if (v === binding.buttonPlay) {
            stopPlaying()
        }
    }

    private fun startPlaying() {
      binding.buttonStopPlay.setEnabled(true)
        binding.buttonPlay.setEnabled(false)
        binding.progressBar1.visibility = View.VISIBLE
        player!!.prepareAsync()
        player!!.setOnPreparedListener { player!!.start() }
    }

    private fun stopPlaying() {
        if (player!!.isPlaying) {
            player!!.stop()
            player!!.release()
            initializeMediaPlayer()
        }
        binding.buttonPlay.setEnabled(true)
        binding.buttonStopPlay.setEnabled(false)
        binding.progressBar1.visibility = View.INVISIBLE
    }

    private fun initializeMediaPlayer() {
        player = MediaPlayer()
        try {
            player!!.setDataSource("http://onlineradiobox.com/uz/navoonline/player/?cs=uz.navoonline")
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        player!!.setOnBufferingUpdateListener { mp, percent ->
           binding.progressBar1.secondaryProgress = percent
            Log.i("Buffering", "" + percent)
        }
    }

    override fun onPause() {
        super.onPause()
        if (player!!.isPlaying) {
            player!!.stop()
        }
    }

}