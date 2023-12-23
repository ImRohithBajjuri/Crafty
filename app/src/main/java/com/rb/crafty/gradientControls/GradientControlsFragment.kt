package com.rb.crafty.gradientControls

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.dataObjects.GradientData
import com.rb.crafty.R
import com.rb.crafty.databinding.FragmentGradientControlsBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.Elements

class GradientControlsFragment() : Fragment() {
    var binding: FragmentGradientControlsBinding? = null

    lateinit var listener: AppUtils.ElementOptionsControlsListener
    lateinit var colors: MutableList<String>
    var selectedColor: Int = 0
    var currentAngle: Int = 0
    lateinit var gradient: GradientDrawable

    constructor(listener: AppUtils.ElementOptionsControlsListener) : this() {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGradientControlsBinding.inflate(inflater, container, false)

        colors = arrayListOf("#000000", "#ffffff")
        gradient = GradientDrawable()
        gradient.cornerRadius = AppUtils.pxToDp(requireActivity(), 20).toFloat()


        binding!!.gradOneHex.setOnClickListener {
            //Set current color.
            selectedColor = 0

            binding!!.gradOneHex.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.selected_grad_hex_bgr
            )
            binding!!.gradTwoHex.background = ColorDrawable(Color.TRANSPARENT)

            //Set the color picker color.
            binding!!.gradColorControlCard.setCardBackgroundColor(Color.parseColor(colors[0]))

        }

        binding!!.gradTwoHex.setOnClickListener {
            //Set current color.
            selectedColor = 1

            binding!!.gradTwoHex.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.selected_grad_hex_bgr
            )
            binding!!.gradOneHex.background = ColorDrawable(Color.TRANSPARENT)

            //Set the color picker color.
            binding!!.gradColorControlCard.setCardBackgroundColor(Color.parseColor(colors[1]))
        }

        binding!!.gradColorControlCard.setOnClickListener {
            val listener = object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, it: Int) {
                    //Update the grad color and other UI.
                    if (selectedColor == 0) {
                        val colorString = AppUtils.intToHex(it)

                        binding!!.gradOneHexTxt.text = colorString
                        binding!!.gradOneHexIcon.imageTintList = ColorStateList.valueOf(it)
                        binding!!.gradColorControlCard.setCardBackgroundColor(it)

                        colors[0] = colorString


                        updateGradientIcon()

                        listener.onGradientColorsUpdate(colors)

                    }
                    else {
                        val colorString = AppUtils.intToHex(it)

                        binding!!.gradTwoHexTxt.text = colorString
                        binding!!.gradTwoHexIcon.imageTintList = ColorStateList.valueOf(it)
                        binding!!.gradColorControlCard.setCardBackgroundColor(it)

                        colors[1] = colorString

                        updateGradientIcon()


                        listener.onGradientColorsUpdate(colors)
                    }
                }

                override fun onDialogDismissed(dialogId: Int) {
                }

            }
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    Elements(requireActivity()).buildColorPickerDialog(listener).show(childFragmentManager, "Color")
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }


        binding!!.gradAngleSeek.addOnChangeListener { slider, value, fromUser ->
            currentAngle = value.toInt()
            binding!!.gradAngleTxt.text = value.toString()
            listener.onGradientAngleUpdate(value.toInt())
            updateGradientIcon()
        }
        return binding!!.root
    }

    fun updateGradientIcon() {
        val intColorArray = intArrayOf(Color.parseColor(colors[0]), Color.parseColor(colors[1]))
        gradient.colors = intColorArray
        when (currentAngle) {
            0 -> {gradient.orientation = GradientDrawable.Orientation.LEFT_RIGHT}
            45 -> {gradient.orientation = GradientDrawable.Orientation.TL_BR}
            90 -> {gradient.orientation = GradientDrawable.Orientation.TOP_BOTTOM}
            135 -> {gradient.orientation = GradientDrawable.Orientation.TR_BL}
            180 -> {gradient.orientation = GradientDrawable.Orientation.RIGHT_LEFT}
            225 -> {gradient.orientation = GradientDrawable.Orientation.BR_TL}
            270 -> {gradient.orientation = GradientDrawable.Orientation.BOTTOM_TOP}
            315 -> {gradient.orientation = GradientDrawable.Orientation.BL_TR}
        }
        binding!!.gradOutIcon.background = gradient

    }

    fun setCurrentGradientColors(elementData: ElementData) {
        val gradientData = elementData.data as GradientData
        if (binding != null) {
            colors = gradientData.colors!!
            currentAngle = gradientData.gradientAngle

            binding!!.gradOneHexTxt.text = gradientData.colors!![0]
            binding!!.gradOneHexIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(gradientData.colors!![0]))

            binding!!.gradTwoHexTxt.text = gradientData.colors!![1]
            binding!!.gradTwoHexIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(gradientData.colors!![1]))

            binding!!.gradColorControlCard.setCardBackgroundColor(Color.parseColor(gradientData.colors!![0]))

            binding!!.gradAngleSeek.value = gradientData.gradientAngle.toFloat()

            updateGradientIcon()
        }
    }

}