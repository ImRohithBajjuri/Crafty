package com.rb.crafty.sheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rb.crafty.R
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.utils.Elements.Companion.BLOCK_TYPE
import com.rb.crafty.utils.Elements.Companion.GRADIENT_TYPE
import com.rb.crafty.utils.Elements.Companion.IMAGE_TYPE
import com.rb.crafty.utils.Elements.Companion.PATTERN_TYPE
import com.rb.crafty.utils.Elements.Companion.TEXT_TYPE
import com.rb.crafty.databinding.FragmentElementsSheetBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import com.rb.crafty.utils.Elements.Companion.AUDIO_TYPE


class ElementsSheet(): BottomSheetDialogFragment() {

    lateinit var binding: FragmentElementsSheetBinding

    interface ElementsSheetListener {
        fun onElementSelected(type: String)

        fun onSavedElementSelected(elementData: ElementData)

        fun onShowMessage(text: String)
    }
    lateinit var listener: ElementsSheetListener

    lateinit var colourUtils: ColourUtils

    lateinit var firestore: FirebaseFirestore

    var user: FirebaseUser? = null

    constructor(listener: ElementsSheetListener) : this() {
        this.listener = listener
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
        binding = FragmentElementsSheetBinding.inflate(inflater, container, false)

        firestore = Firebase.firestore
        user = Firebase.auth.currentUser

        colourUtils = ColourUtils(requireActivity())

        binding.textElement.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    //Notify the listener with the type.
                    listener.onElementSelected(TEXT_TYPE)

                    //Close the sheet.
                    dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.imageElement.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    //Notify the listener with the type.
                    listener.onElementSelected(IMAGE_TYPE)

                    //Close the sheet.
                    dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.blockElement.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    //Notify the listener with the type.
                    listener.onElementSelected(BLOCK_TYPE)

                    //Close the sheet.
                    dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.gradientElement.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    //Notify the listener with the type.
                    listener.onElementSelected(GRADIENT_TYPE)

                    //Close the sheet.
                    dismiss()                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.patternElement.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    //Notify the listener with the type.
                    listener.onElementSelected(PATTERN_TYPE)

                    //Close the sheet.
                    dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.audioElement.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    //Notify the listener with the type.
                    listener.onElementSelected(AUDIO_TYPE)

                    //Close the sheet.
                    dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.savedElements.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    var userElementsSheet: UserElementsSheet? = null
                    userElementsSheet = UserElementsSheet(UserElementsSheet.FROM_CREATOR, object : UserElementsSheet.UserElementsSheetListener {
                        override fun onElementClicked(elementData: ElementData) {
                            listener.onSavedElementSelected(elementData)

                            userElementsSheet!!.dismiss()

                            dismiss()
                        }
                    })
                    userElementsSheet.show(childFragmentManager, "UseCaseTwo")
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            it.startAnimation(anim)
        }

        //Dark mode.
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
            binding.elementsSheetHeader.setTextColor(colourUtils.darkModeDeepPurple)

            binding.textElementTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.imageElementTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.blockElementTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.gradientElementTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.patternElementTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.audioElementTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.savedElementsTxt.setTextColor(colourUtils.darkModeDeepPurple)

        }
        else {
            binding.elementsSheetHeader.setTextColor(colourUtils.lightModeDeepPurple)

            binding.textElementTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.blockElementTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.imageElementTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.gradientElementTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.patternElementTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.audioElementTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.savedElementsTxt.setTextColor(colourUtils.lightModeDeepPurple)

        }
    }
}