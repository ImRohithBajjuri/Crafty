package com.rb.crafty.sheets

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.biometric.BiometricManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.crafty.HiddenCards
import com.rb.crafty.R
import com.rb.crafty.databinding.FragmentHiddenCardsAccessInfoSheetBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils

class HiddenCardsAccessInfoSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentHiddenCardsAccessInfoSheetBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.PurpleBottomSheetDialogStyle)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHiddenCardsAccessInfoSheetBinding.inflate(inflater, container, false)

        binding.hidAccessOkButton.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }
        return binding.root
    }

}