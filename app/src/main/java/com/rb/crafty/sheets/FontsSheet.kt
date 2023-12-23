package com.rb.crafty.sheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.crafty.R
import com.rb.crafty.databinding.FragmentFontsSheetBinding

class FontsSheet : BottomSheetDialogFragment() {
    lateinit var binding: FragmentFontsSheetBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFontsSheetBinding.inflate(inflater, container, false)
        return binding.root
    }
}