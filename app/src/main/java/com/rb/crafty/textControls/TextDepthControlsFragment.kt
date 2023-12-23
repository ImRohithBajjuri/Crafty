package com.rb.crafty.textControls

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
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.rb.crafty.R
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.dataObjects.TextData
import com.rb.crafty.databinding.FragmentTextDepthControlsBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.Elements


class TextDepthControlsFragment() : Fragment() {

    var binding: FragmentTextDepthControlsBinding? = null

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
        binding = FragmentTextDepthControlsBinding.inflate(inflater, container, false)


        binding!!.textDepthOptionRadiusSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding!!.textDepthOptionRadiusText.text = progress.toString()
                listener.onTextDepthRadiusUpdate(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.textDepthOptionRadiusSeek.id,
                        binding!!.textDepthOptionRadiusSH.id,
                        binding!!.textDepthOptionRadiusText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.textDepthOptionRadiusSeek.id,
                        binding!!.textDepthOptionRadiusSH.id,
                        binding!!.textDepthOptionRadiusText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })


        binding!!.textDepthOptionHorizontalSeek.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    binding!!.textDepthOptionHorizontalText.text = progress.toString()
                    listener.onTextDepthHorzUpdate(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    //Show only the current seek.
                    handleFocusedControlVisibility(
                        true,
                        intArrayOf(
                            binding!!.textDepthOptionHorizontalSeek.id,
                            binding!!.textDepthOptionHorizontalSH.id,
                            binding!!.textDepthOptionHorizontalText.id
                        )
                    )
                    listener.controlsInFocus()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    //Show all.
                    handleFocusedControlVisibility(
                        false,
                        intArrayOf(
                            binding!!.textDepthOptionHorizontalSeek.id,
                            binding!!.textDepthOptionHorizontalSH.id,
                            binding!!.textDepthOptionHorizontalText.id
                        )
                    )
                    listener.controlsNotInFocus()
                }

            })


        binding!!.textDepthOptionVerticalSeek.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    binding!!.textDepthOptionVerticalText.text = progress.toString()
                    listener.onTextDepthVertUpdate(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    //Show only the current seek.
                    handleFocusedControlVisibility(
                        true,
                        intArrayOf(
                            binding!!.textDepthOptionVerticalSeek.id,
                            binding!!.textDepthOptionVerticalSH.id,
                            binding!!.textDepthOptionVerticalText.id
                        )
                    )
                    listener.controlsInFocus()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    //Show all.
                    handleFocusedControlVisibility(
                        false,
                        intArrayOf(
                            binding!!.textDepthOptionVerticalSeek.id,
                            binding!!.textDepthOptionVerticalSH.id,
                            binding!!.textDepthOptionVerticalText.id
                        )
                    )
                    listener.controlsNotInFocus()
                }

            })


        binding!!.textDepthControlColorCard.setOnClickListener {
            val listener = object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, it: Int) {
                    //Update the text depth color and other UI.
                    binding!!.textDepthControlColorCard.setCardBackgroundColor(it)
                    binding!!.textDepthOptionColorHex.text = AppUtils.intToHex(it)
                    val drawable =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
                    DrawableCompat.wrap(drawable).setTint(it)
                    binding!!.textDepthOptionColorHex.setCompoundDrawablesWithIntrinsicBounds(
                        drawable,
                        null,
                        null,
                        null
                    )
                    listener.onTextDepthColorUpdate(it)
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

    fun setCurrentDepth(textData: TextData) {
        if (binding != null) {
            binding!!.textDepthOptionRadiusSeek.progress = textData.depthRadius
            binding!!.textDepthOptionVerticalSeek.progress =
                textData.vertDepth + 100
            binding!!.textDepthOptionHorizontalSeek.progress =
                textData.horzDepth + 100

            //Show the current color in the viewer.
            binding!!.textDepthControlColorCard.setCardBackgroundColor(Color.parseColor(textData.depthColor))

            binding!!.textDepthOptionRadiusText.text =
                textData.depthRadius.toString()
            binding!!.textDepthOptionVerticalText.text =
                textData.vertDepth.toString()
            binding!!.textDepthOptionHorizontalText.text =
                textData.horzDepth.toString()

            binding!!.textDepthOptionColorHex.text = textData.depthColor
            val drawable =
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
            DrawableCompat.wrap(drawable).setTint(Color.parseColor(textData.depthColor))
            binding!!.textDepthOptionColorHex.setCompoundDrawablesWithIntrinsicBounds(
                drawable,
                null,
                null,
                null
            )
        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.textDepthOptionLayout.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.textDepthOptionLayout.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }

}