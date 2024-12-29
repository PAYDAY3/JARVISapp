package com.example.jarvisassistant.ui.voice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jarvisassistant.databinding.FragmentVoiceBinding

class VoiceFragment : Fragment() {

    private var _binding: FragmentVoiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var voiceViewModel: VoiceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        voiceViewModel = ViewModelProvider(this).get(VoiceViewModel::class.java)

        setupVoiceButton()
        observeVoiceRecognitionResult()
    }

    private fun setupVoiceButton() {
        binding.buttonVoice.setOnClickListener {
            voiceViewModel.startVoiceRecognition(requireActivity())
        }
    }

    private fun observeVoiceRecognitionResult() {
        voiceViewModel.voiceRecognitionResult.observe(viewLifecycleOwner) { result ->
            binding.textViewResult.text = result
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

