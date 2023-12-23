package com.rb.crafty.sheets

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.rb.crafty.R
import com.rb.crafty.dataObjects.PremiumData
import com.rb.crafty.databinding.FragmentPremiumDetailsSheetBinding
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils

class PremiumDetailsSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentPremiumDetailsSheetBinding

    lateinit var colourUtils: ColourUtils

    var user: FirebaseUser? = null

    lateinit var firestore: FirebaseFirestore

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        adjustSheetStyle(AppUtils.isDarkMode(requireActivity()))
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPremiumDetailsSheetBinding.inflate(inflater, container, false)
        colourUtils = ColourUtils(requireActivity())

        user = Firebase.auth.currentUser
        firestore = Firebase.firestore

        if (user != null) {
            //Set profile pic
            Glide.with(this).asDrawable().load(user!!.photoUrl).circleCrop().into(binding.premiumDetailsUserPic)
            binding.premiumDetailsName.text = user!!.displayName

            //Get premium details.
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.toObject<PremiumData>()
                    binding.premiumDetailsId.text = "${binding.premiumDetailsId.text} ${data!!.orderId}"
                    binding.premiumDetailsDate.text = "${binding.premiumDetailsDate.text} ${AppUtils.getNormalTime(data.premiumPurchaseDate)}"
                }
            }
        }



        darkMode(AppUtils.isDarkMode(requireActivity()))
        return binding.root
    }
    fun adjustSheetStyle(isDark: Boolean){
        if (isDark){
            setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.DarkBottomSheetDialogStyle)
        }
        else{
            setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.BottomSheetDialogStyle)
        }
    }

    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.premiumDetailsSheetImg.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)

            binding.premiumDetailsSheetHeader.setTextColor(colourUtils.darkModeDeepPurple)

            binding.premiumDetailsName.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumDetailsProductName.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumDetailsSoldBy.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumDetailsDate.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumDetailsAccess.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumDetailsId.setTextColor(colourUtils.darkModeDeepPurple)
            binding.premiumDetailsPaidThrough.setTextColor(colourUtils.darkModeDeepPurple)

        }
        else {
            binding.premiumDetailsSheetImg.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)

            binding.premiumDetailsSheetHeader.setTextColor(colourUtils.lightModeDeepPurple)

            binding.premiumDetailsName.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumDetailsProductName.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumDetailsSoldBy.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumDetailsDate.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumDetailsAccess.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumDetailsId.setTextColor(colourUtils.lightModeDeepPurple)
            binding.premiumDetailsPaidThrough.setTextColor(colourUtils.lightModeDeepPurple)
        }
    }
}