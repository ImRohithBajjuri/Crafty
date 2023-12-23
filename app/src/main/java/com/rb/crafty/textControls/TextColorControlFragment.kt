package com.rb.crafty.textControls

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
import com.rb.crafty.dataObjects.TextData
import com.rb.crafty.databinding.FragmentTextColorControlBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.Elements


class TextColorControlFragment() : Fragment() {

    var binding: FragmentTextColorControlBinding? = null

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
        binding = FragmentTextColorControlBinding.inflate(inflater, container, false)

        //Update the color.
        binding!!.textControlColorCard.setOnClickListener {
            val listener = object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, it: Int) {
                    //Update the text color and other UI.
                    binding!!.textControlColorCard.setCardBackgroundColor(it)
                    binding!!.textColorOptionHex.text = AppUtils.intToHex(it)
                    val drawable =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
                    DrawableCompat.wrap(drawable).setTint(it)
                    binding!!.textColorOptionHex.setCompoundDrawablesWithIntrinsicBounds(
                        drawable,
                        null,
                        null,
                        null
                    )

                    listener.onTextColorUpdate(it)
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

    fun setCurrentColor(textData: TextData) {
        if (binding != null) {
            binding!!.textColorOptionHex.text = textData.color
            val drawable =
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
            DrawableCompat.wrap(drawable).setTint(Color.parseColor(textData.color))
            binding!!.textColorOptionHex.setCompoundDrawablesWithIntrinsicBounds(
                drawable,
                null,
                null,
                null
            )

            //Show the current color in the card.
            binding!!.textControlColorCard.setCardBackgroundColor(Color.parseColor(textData.color))

        }
    }

}