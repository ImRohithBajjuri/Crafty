package com.rb.crafty.sheets

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rb.crafty.R
import com.rb.crafty.databinding.FragmentPremiumUserSheetBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils


class PremiumUserSheet() : BottomSheetDialogFragment() {

    lateinit var binding: FragmentPremiumUserSheetBinding

    lateinit var colourUtils: ColourUtils

    lateinit var firestore: FirebaseFirestore

    var user: FirebaseUser? = null

    var isNewPremium = false

    companion object {
        val TYPE_GET = "get"
        val TYPE_GOT = "got"
    }

    interface PremiumSheetListener {
        fun getPremium()
    }

    var type = TYPE_GOT

    var listener: PremiumSheetListener? = null

    constructor(type: String, isNewPremium: Boolean, premiumSheetListener: PremiumSheetListener?) : this() {
        this.type = type
        this.isNewPremium = isNewPremium
        this.listener = premiumSheetListener
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        adjustSheetStyle(AppUtils.isDarkMode(requireActivity()))
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPremiumUserSheetBinding.inflate(inflater, container, false)

        colourUtils = ColourUtils(requireActivity())

        firestore = Firebase.firestore
        user = Firebase.auth.currentUser

        //Adjust the text if the user is already a premium user.
        if (type == TYPE_GOT) {
            if (isNewPremium) {
                binding.premiumSheetInfoText.text = getString(R.string.new_premium_user_text)

            }
            else {
                binding.premiumSheetInfoText.text = getString(R.string.premium_user_text)
            }
            binding.premiumSheetGetPremiumButton.visibility = View.GONE
            binding.premiumSheetPremiumDetailsButton.visibility = View.VISIBLE

        }
        else {
            binding.premiumSheetInfoText.text =  getString(R.string.get_premium_sheet_text)
            binding.premiumSheetGetPremiumButton.visibility = View.VISIBLE
            binding.premiumSheetPremiumDetailsButton.visibility = View.GONE
        }


        binding.premiumSheetGetPremiumButton.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (listener != null) {
                        listener!!.getPremium()
                    }
                    dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
           it.startAnimation(anim)
        }

        binding.premiumSheetPremiumDetailsButton.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val detailsSheet = PremiumDetailsSheet()
                    detailsSheet.show(childFragmentManager, "UseCaseOne")
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })

            it.startAnimation(anim)
        }

        darkMode(AppUtils.isDarkMode(requireActivity()))
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

    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.premiumSheetHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumSheetInfoText.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumSheetFeaturesHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumSheetImg.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)

            binding.premiumSheetFeaturesCard.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.darkGrey5))

            binding.premiumSheetFeatureOne.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumSheetFeatureTwo.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumSheetFeatureThree.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumSheetFeatureFour.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumSheetFeatureFive.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumSheetFeatureSix.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumSheetFeatureSeven.setTextColor(colourUtils.darkModeDeepPurple)

            binding.premiumSheetGetPremiumButton.setCardBackgroundColor(colourUtils.darkModeDeepPurple)
            binding.premiumSheetPremiumDetailsButton.setCardBackgroundColor(colourUtils.darkModeDeepPurple)

            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureOne, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureTwo, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureThree, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureFour, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureFive, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureSix, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureSeven, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))

        }
        else {
            binding.premiumSheetHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumSheetInfoText.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumSheetFeaturesHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumSheetImg.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)

            binding.premiumSheetFeaturesCard.setCardBackgroundColor(Color.WHITE)


            binding.premiumSheetFeatureOne.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumSheetFeatureTwo.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumSheetFeatureThree.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumSheetFeatureFour.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumSheetFeatureFive.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumSheetFeatureSix.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumSheetFeatureSeven.setTextColor(colourUtils.lightModeDeepPurple)

            binding.premiumSheetGetPremiumButton.setCardBackgroundColor(colourUtils.lightModeDeepPurple)
            binding.premiumSheetPremiumDetailsButton.setCardBackgroundColor(colourUtils.lightModeDeepPurple)


            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureOne, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureTwo, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureThree, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureFour, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureFive, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureSix, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.premiumSheetFeatureSeven, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
        }
    }
}