package com.rb.crafty.sheets

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.crafty.HelpActivity
import com.rb.crafty.R
import com.rb.crafty.databinding.FragmentHelpSheetBinding
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils


class HelpSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentHelpSheetBinding
    lateinit var colourUtils: ColourUtils

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        adjustSheetStyle(AppUtils.isDarkMode(requireActivity()))
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHelpSheetBinding.inflate(inflater, container, false)
        colourUtils = ColourUtils(requireActivity())

        binding.helpAddFav.text = "How to add a card to your ${getString(R.string.favourites)}"
        binding.helpCreate.setOnClickListener {
            val intent = Intent(requireActivity(), HelpActivity::class.java)
            intent.putExtra("helpFor", "create")
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
        }
        binding.helpExportCardImage.setOnClickListener {
            val intent = Intent(requireActivity(), HelpActivity::class.java)
            intent.putExtra("helpFor", "exportCardImage")
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
        }
        binding.helpExportCard.setOnClickListener {
            val intent = Intent(requireActivity(), HelpActivity::class.java)
            intent.putExtra("helpFor", "exportCard")
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
        }

        binding.helpAddFav.setOnClickListener {
            val intent = Intent(requireActivity(), HelpActivity::class.java)
            intent.putExtra("helpFor", "addFav")
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
        }

        binding.helpHide.setOnClickListener {
            val intent = Intent(requireActivity(), HelpActivity::class.java)
            intent.putExtra("helpFor", "hide")
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
        }

        darkMode(AppUtils.isDarkMode(requireActivity()))
        return binding.root
    }

    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.helpSheetHeader.setTextColor(colourUtils.darkModeDeepPurple)

            binding.helpCreate.setTextColor(colourUtils.darkModeDeepPurple)
            binding.helpExportCardImage.setTextColor(colourUtils.darkModeDeepPurple)
            binding.helpExportCard.setTextColor(colourUtils.darkModeDeepPurple)
            binding.helpAddFav.setTextColor(colourUtils.darkModeDeepPurple)
            binding.helpHide.setTextColor(colourUtils.darkModeDeepPurple)
        }
        else {
            binding.helpSheetHeader.setTextColor(colourUtils.lightModeDeepPurple)

            binding.helpCreate.setTextColor(colourUtils.lightModeDeepPurple)
            binding.helpExportCardImage.setTextColor(colourUtils.lightModeDeepPurple)
            binding.helpExportCard.setTextColor(colourUtils.lightModeDeepPurple)
            binding.helpAddFav.setTextColor(colourUtils.lightModeDeepPurple)
            binding.helpHide.setTextColor(colourUtils.lightModeDeepPurple)
        }
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