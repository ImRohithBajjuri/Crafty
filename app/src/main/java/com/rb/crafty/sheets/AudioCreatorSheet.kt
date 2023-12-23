package com.rb.crafty.sheets

import android.app.Dialog
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.media3.common.AudioAttributes
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.crafty.R
import com.rb.crafty.adapters.AudioLanguagesAdapter
import com.rb.crafty.databinding.FragmentAudioCreatorSheetBinding
import com.rb.crafty.databinding.FragmentAudioInputControlsBinding
import com.rb.crafty.databinding.FragmentAudioLanguageSheetBinding
import com.rb.crafty.utils.AppUtils
import java.util.Locale


class AudioCreatorSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentAudioCreatorSheetBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        adjustSheetStyle(AppUtils.isDarkMode(requireActivity()))
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAudioCreatorSheetBinding.inflate(inflater, container, false)

        val tts = TextToSpeech(requireActivity()) {
        }

        val style = if (AppUtils.isDarkMode(requireActivity())){
            R.style.DarkBottomSheetDialogStyle
        }
        else{
           R.style.BottomSheetDialogStyle
        }

        val audioLangBinding = FragmentAudioLanguageSheetBinding.inflate(LayoutInflater.from(requireActivity()))
        val audioLangSheet = BottomSheetDialog(requireActivity(), style)
        audioLangSheet.setContentView(audioLangBinding.root)


        val langAdapter = AudioLanguagesAdapter(requireActivity(), tts.availableLanguages.toMutableList())
        val lm = LinearLayoutManager(requireActivity())
        audioLangBinding.audioLanguagesRecy.layoutManager = lm
        audioLangBinding.audioLanguagesRecy.adapter = langAdapter


        audioLangSheet.show()

        return binding.root
    }

    fun adjustSheetStyle(isDark: Boolean){
        if (isDark){
            setStyle(STYLE_NORMAL,R.style.DarkBottomSheetDialogStyle)
        }
        else{
            setStyle(STYLE_NORMAL,R.style.BottomSheetDialogStyle)
        }
    }
}