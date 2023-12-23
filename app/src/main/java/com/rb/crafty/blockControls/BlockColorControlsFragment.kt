package com.rb.crafty.blockControls

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.rb.crafty.R
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.dataObjects.BlockData
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.databinding.FragmentBlockColorControlsBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.Elements


class BlockColorControlsFragment() : Fragment() {

    var binding: FragmentBlockColorControlsBinding? = null

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
        binding = FragmentBlockColorControlsBinding.inflate(inflater, container, false)

        binding!!.blockColorControlCard.setOnClickListener {
            val listener = object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, it: Int) {
                    //Update the block color and other UI.
                    binding!!.blockColorControlCard.setCardBackgroundColor(it)
                    binding!!.blockColorOptionHex.text = AppUtils.intToHex(it)

                    val drawable =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
                    DrawableCompat.wrap(drawable).setTint(it)
                    binding!!.blockColorOptionHex.setCompoundDrawablesWithIntrinsicBounds(
                        drawable,
                        null,
                        null,
                        null
                    )

                    listener.onBlockColorUpdate(it)
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

    fun setCurrentBlockColor(elementData: ElementData) {
        if (binding != null) {
            val blockData = elementData.data as BlockData
            //Set the current values to the color picker.
            binding!!.blockColorControlCard.setCardBackgroundColor(Color.parseColor(blockData.color))
            binding!!.blockColorOptionHex.text = blockData.color
            val drawable =
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
            DrawableCompat.wrap(drawable).setTint(Color.parseColor(blockData.color))
            binding!!.blockColorOptionHex.setCompoundDrawablesWithIntrinsicBounds(
                drawable,
                null,
                null,
                null
            )
        }
    }

}