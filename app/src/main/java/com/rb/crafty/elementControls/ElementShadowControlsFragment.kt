package com.rb.crafty.elementControls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.children
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.databinding.FragmentElementShadowControlsBinding


class ElementShadowControlsFragment() : Fragment() {

    var binding: FragmentElementShadowControlsBinding? = null

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
        binding = FragmentElementShadowControlsBinding.inflate(inflater, container, false)
        binding!!.elementShadowOptionSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding!!.elementShadowOptionText.text = progress.toString()
                listener.onElementShadowRadiusUpdate(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.elementShadowOptionSeek.id,
                        binding!!.elementShadowOptionSH.id,
                        binding!!.elementShadowOptionText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.elementShadowOptionSeek.id,
                        binding!!.elementShadowOptionSH.id,
                        binding!!.elementShadowOptionText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })

        return binding!!.root
    }


    fun setCurrentShadow(radius: Int) {
        if (binding != null) {
            binding!!.elementShadowOptionSeek.progress = radius
        }
    }
    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.elementShadowControlsParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.elementShadowControlsParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }

}