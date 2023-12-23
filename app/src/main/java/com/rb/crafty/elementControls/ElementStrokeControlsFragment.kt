package com.rb.crafty.elementControls

import android.content.res.ColorStateList
import android.graphics.Color
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
import com.rb.crafty.databinding.FragmentElementStrokeControlsBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.Elements


class ElementStrokeControlsFragment() : Fragment() {
    var binding: FragmentElementStrokeControlsBinding? = null

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
        binding = FragmentElementStrokeControlsBinding.inflate(inflater, container, false)

        binding!!.elementStrokeWidthControlSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding!!.elementStrokeWidthControlText.text = progress.toString()
                listener.onElementStrokeWidthUpdate(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.elementStrokeWidthControlSeek.id,
                        binding!!.elementStrokeWidthControlSH.id,
                        binding!!.elementStrokeWidthControlText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.elementStrokeWidthControlSeek.id,
                        binding!!.elementStrokeWidthControlSH.id,
                        binding!!.elementStrokeWidthControlText.id
                    )
                )

                listener.controlsNotInFocus()
            }

        })



        binding!!.elementStrokeControlColorCard.setOnClickListener {
            val listener = object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, it: Int) {
                    val colorString = AppUtils.intToHex(it)
                    binding!!.elementStrokeColorControlHex.text = colorString

                    val drawable =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
                    DrawableCompat.wrap(drawable).setTint(it)
                    binding!!.elementStrokeColorControlHex.setCompoundDrawablesWithIntrinsicBounds(
                        drawable,
                        null,
                        null,
                        null
                    )

                    binding!!.elementStrokeControlColorCard.setCardBackgroundColor(it)
                    listener.onElementStrokeColorUpdate(it)
                }

                override fun onDialogDismissed(dialogId: Int) {
                }

            }
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    Elements(requireActivity()).buildColorPickerDialog(listener)
                        .show(childFragmentManager, "Color")
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        return binding!!.root
    }

    fun setCurrentStroke(strokeWidth: Int, strokeColor: String) {
        if (binding != null) {
            binding!!.elementStrokeWidthControlSeek.progress = strokeWidth
            binding!!.elementStrokeControlColorCard.setCardBackgroundColor(
                Color.parseColor(
                    strokeColor
                )
            )

            binding!!.elementStrokeColorControlHex.text = strokeColor

            val drawable =
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
            DrawableCompat.wrap(drawable).setTint(Color.parseColor(strokeColor))
            binding!!.elementStrokeColorControlHex.setCompoundDrawablesWithIntrinsicBounds(
                drawable,
                null,
                null,
                null
            )

        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.elementStrokeControlsParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.elementStrokeControlsParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }

}