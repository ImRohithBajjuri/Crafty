package com.rb.crafty.elementControls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.children
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.databinding.FragmentElementRotationControlsBinding


class ElementRotationControlsFragment() : Fragment() {
    var binding: FragmentElementRotationControlsBinding? = null
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
        binding = FragmentElementRotationControlsBinding.inflate(inflater, container, false)

        binding!!.elementRotateOptionSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding!!.elementRotationOptionText.text = progress.toString()
                listener.onElementRotationUpdate(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.elementRotateOptionSeek.id,
                        binding!!.elementRotationOptionSH.id,
                        binding!!.elementRotationOptionText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.elementRotateOptionSeek.id,
                        binding!!.elementRotationOptionSH.id,
                        binding!!.elementRotationOptionText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })
        return binding!!.root
    }

    fun setCurrentRotation(rotation: Int) {
        if (binding != null) {
            binding!!.elementRotateOptionSeek.progress = rotation
        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.elementRotationControlsParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.elementRotationControlsParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }


}