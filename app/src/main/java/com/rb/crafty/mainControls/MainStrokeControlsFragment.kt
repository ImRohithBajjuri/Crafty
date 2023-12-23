package com.rb.crafty.mainControls

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
import com.rb.crafty.dataObjects.MainCardData
import com.rb.crafty.databinding.FragmentMainStrokeControlsBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.Elements


class MainStrokeControlsFragment() : Fragment() {
    var binding: FragmentMainStrokeControlsBinding? = null

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
        binding = FragmentMainStrokeControlsBinding.inflate(inflater, container, false)

        binding!!.mainStrokeWidthControlSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding!!.mainStrokeWidthControlText.text = p1.toString()

                listener.onMainStrokeWidthUpdate(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.mainStrokeWidthControlSeek.id,
                        binding!!.mainStrokeWidthControlSH.id,
                        binding!!.mainStrokeWidthControlText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.mainStrokeWidthControlSeek.id,
                        binding!!.mainStrokeWidthControlSH.id,
                        binding!!.mainStrokeWidthControlText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })


        binding!!.mainStrokeControlColorCard.setOnClickListener {
            val listener = object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, it: Int) {
                    binding!!.mainStrokeColorControlHex.text = AppUtils.intToHex(it)

                    val drawable =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
                    DrawableCompat.wrap(drawable).setTint(it)
                    binding!!.mainStrokeColorControlHex.setCompoundDrawablesWithIntrinsicBounds(
                        drawable,
                        null,
                        null,
                        null
                    )

                    binding!!.mainStrokeControlColorCard.setCardBackgroundColor(it)
                    listener.onMainStrokeColorUpdate(it)
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

    fun setCurrentMainStroke(mainCardData: MainCardData) {

        //Set the values to controls and texts.
        if (binding != null) {
            binding!!.mainStrokeControlColorCard.setCardBackgroundColor(Color.parseColor(mainCardData.strokeColor))

            binding!!.mainStrokeWidthControlSeek.progress = mainCardData.strokeWidth

            binding!!.mainStrokeColorControlHex.text = mainCardData.strokeColor

            val drawable =
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
            DrawableCompat.wrap(drawable).setTint(Color.parseColor(mainCardData.strokeColor))
            binding!!.mainStrokeColorControlHex.setCompoundDrawablesWithIntrinsicBounds(
                drawable,
                null,
                null,
                null
            )

            binding!!.mainStrokeWidthControlText.text = mainCardData.strokeWidth.toString()
        }
    }
    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.mainStrokeControlsParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.mainStrokeControlsParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }

}