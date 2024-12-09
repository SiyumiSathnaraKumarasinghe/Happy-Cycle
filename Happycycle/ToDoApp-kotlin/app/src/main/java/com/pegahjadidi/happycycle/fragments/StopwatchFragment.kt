package com.pegahjadidi.happycycle.fragments


import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pegahjadidi.happycycle.MainActivity
import com.pegahjadidi.happycycle.databinding.ActivityStopwatchFragmentBinding

class StopwatchFragment : Fragment() {
    private var _binding: ActivityStopwatchFragmentBinding? = null
    private val binding get() = _binding!!

    private var isRunning = false
    private var baseTime: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityStopwatchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startStopButton.setOnClickListener {
            if (isRunning) {
                stopStopwatch()
            } else {
                startStopwatch()
            }
        }

        binding.stopButton.setOnClickListener {
            stopStopwatch()
        }

        binding.resetButton.setOnClickListener {
            resetStopwatch()
        }

        // Back button functionality to navigate back to MainActivity
        binding.backButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun startStopwatch() {
        isRunning = true
        binding.startStopButton.text = "Pause"
        baseTime = SystemClock.elapsedRealtime() - (binding.tvStopwatch.base - SystemClock.elapsedRealtime())
        binding.tvStopwatch.base = baseTime
        binding.tvStopwatch.start()
    }

    private fun stopStopwatch() {
        if (isRunning) {
            isRunning = false
            binding.startStopButton.text = "Start"
            binding.tvStopwatch.stop()
        }
    }

    private fun resetStopwatch() {
        stopStopwatch()
        binding.tvStopwatch.base = SystemClock.elapsedRealtime()
        baseTime = 0L
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
