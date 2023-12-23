package com.rb.crafty.elementControls

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.rb.crafty.R
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.databinding.FragmentElementBackgroundControlsBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.Elements

class ElementBackgroundControlsFragment() : Fragment() {
    var binding: FragmentElementBackgroundControlsBinding? = null
    lateinit var listener: AppUtils.ElementOptionsControlsListener

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
        binding = FragmentElementBackgroundControlsBinding.inflate(inflater, container, false)

        binding!!.elementBgrOptionPaddingSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding!!.elementBackgroundOptionPaddingText.text = progress.toString()
                listener.onElementBackgroundPaddingUpdate(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handleFocusedControlVisibility(true, intArrayOf(binding!!.elementBgrOptionPaddingSeek.id, binding!!.elementBackgroundOptionPaddingSH.id, binding!!.elementBackgroundOptionPaddingText.id))
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                handleFocusedControlVisibility(false, intArrayOf(binding!!.elementBgrOptionPaddingSeek.id, binding!!.elementBackgroundOptionPaddingSH.id, binding!!.elementBackgroundOptionPaddingText.id))
                listener.controlsNotInFocus()
            }

        })


        binding!!.elementBgrOptionCornersSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding!!.elementBackgroundOptionCornersText.text = progress.toString()

                listener.onElementBackgroundCornersUpdate(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handleFocusedControlVisibility(true, intArrayOf(binding!!.elementBgrOptionCornersSeek.id, binding!!.elementBackgroundOptionCornersSH.id, binding!!.elementBackgroundOptionCornersText.id))
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                handleFocusedControlVisibility(false, intArrayOf(binding!!.elementBgrOptionCornersSeek.id, binding!!.elementBackgroundOptionCornersSH.id, binding!!.elementBackgroundOptionCornersText.id))
                listener.controlsNotInFocus()
            }

        })




        binding!!.elementBackgroundControlColorCard.setOnClickListener {
            val listener = object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, it: Int) {
                    val colorString = AppUtils.intToHex(it)

                    binding!!.elementBackgroundOptionColorHex.text = colorString
                    val drawable =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
                    DrawableCompat.wrap(drawable).setTint(it)
                    binding!!.elementBackgroundOptionColorHex.setCompoundDrawablesWithIntrinsicBounds(
                        drawable,
                        null,
                        null,
                        null
                    )
                    binding!!.elementBackgroundControlColorCard.setCardBackgroundColor(it)

                    listener.onElementBackgroundColorUpdate(it)
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
        return binding!!.root
    }

    fun setCurrentBackground(padding: Int, corners: Int, colour: String) {
        if (binding != null) {
            binding!!.elementBgrOptionPaddingSeek.progress =
                padding
            binding!!.elementBgrOptionCornersSeek.progress =
                corners

            binding!!.elementBackgroundControlColorCard.setCardBackgroundColor(  Color.parseColor(colour))


            binding!!.elementBackgroundOptionPaddingText.text =
                padding.toString()
            binding!!.elementBackgroundOptionCornersText.text =
                corners.toString()

            binding!!.elementBackgroundOptionColorHex.text =
                colour

            val drawable =
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
            DrawableCompat.wrap(drawable).setTint(Color.parseColor(colour))
            binding!!.elementBackgroundOptionColorHex.setCompoundDrawablesWithIntrinsicBounds(
                drawable,
                null,
                null,
                null
            )

        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.elementBackgroundControlsParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.elementBackgroundControlsParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            }
            else {
                view.visibility = View.VISIBLE
            }
        }
    }
}