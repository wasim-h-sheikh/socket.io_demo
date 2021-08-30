package com.wasim.socketdemo.ui.fragment

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.wasim.socketdemo.R
import com.wasim.socketdemo.databinding.HomeFragmentBinding
import com.wasim.socketdemo.network.SocketManager
import com.wasim.socketdemo.util.Constants.EVENT_GET_NUMBER
import com.wasim.socketdemo.util.Constants.EVENT_RANDOM
import com.wasim.socketdemo.util.Constants.TAG
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject


class HomeFragment : Fragment(R.layout.home_fragment) {

    private val viewModel by viewModels<HomeViewModel>()

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var socketManager:SocketManager
    private lateinit var toneGenerator: ToneGenerator
    private lateinit var vibrator: Vibrator
    private val vibratePattern = longArrayOf(0, 200, 100)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = HomeFragmentBinding.bind(view)

        toneGenerator=ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        SocketManager().apply {
            socketManager=this
            getEmitterListener(Socket.EVENT_CONNECT_ERROR, onConnectError)
            getEmitterListener(Socket.EVENT_CONNECT, onConnect)
            getEmitterListener(Socket.EVENT_DISCONNECT, onDisconnect)
            getEmitterListener(EVENT_GET_NUMBER, onNewNumber)
        }
        updateUI()
    }

    fun updateUI(){

        viewModel.getEvenNumberLiveData().observe(viewLifecycleOwner, Observer {
            binding.tvLatestEvenNumber.text=it.toString()
            playBeep()
        })
        viewModel.getOddNumberLiveData().observe(viewLifecycleOwner, Observer {
            binding.tvLatestOddNumber.text=it.toString()
            playBeep()
        })
        viewModel.getEvenCounterLiveData().observe(viewLifecycleOwner, Observer {
            binding.tvEvenNumberCounter.text=it.toString()
        })
        viewModel.getOddCounterLiveData().observe(viewLifecycleOwner, Observer {
            binding.tvOddNumberCounter.text=it.toString()
        })
        binding.btnConnect.setOnClickListener { socketManager.connect() }
        binding.btnDisconnect.setOnClickListener {
            vibrate()
            socketManager.disconnect()
        }
        binding.btnReconnect.setOnClickListener {
            vibrate()
            socketManager.disconnect()
            socketManager.connect()
        }

    }
    fun vibrate(){

        vibrator.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createWaveform(vibratePattern, -1))
            } else {
                //deprecated in API 26
                it.vibrate(vibratePattern, -1)
            }
        }
    }
    fun playBeep(){
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
    }

    var onConnect = Emitter.Listener {
        Log.v(TAG, "Socket Connected!")
        SocketManager().sendOnSocket(EVENT_RANDOM,"")
    }
    private val onConnectError = Emitter.Listener {
        Log.v(TAG, "socket ConnectError!")
    }

    private val onDisconnect = Emitter.Listener {
        Log.v(TAG, "Socket disconnected!")
    }

    var onNewNumber = Emitter.Listener {args->
        Log.v(TAG, "onNewNumber")
        val number = JSONObject(args[0].toString()).getInt("number")
        viewModel.updateNumberLiveData(number)
        Log.v(TAG,"number:$number")
    }
}