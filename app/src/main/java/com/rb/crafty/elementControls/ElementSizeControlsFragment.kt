package com.rb.crafty.elementControls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.children
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.databinding.FragmentElementSizeControlsBinding


class ElementSizeControlsFragment() : Fragment() {
    var binding: FragmentElementSizeControlsBinding? = null
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
        binding = FragmentElementSizeControlsBinding.inflate(inflater, container, false)

        binding!!.elementSizeOptionWidthSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding!!.elementSizeOptionWidthText.text = progress.toString()
                listener.onElementWidthUpdate(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.elementSizeOptionWidthSeek.id,
                        binding!!.elementSizeOptionWidthSH.id,
                        binding!!.elementSizeOptionWidthText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.elementSizeOptionWidthSeek.id,
                        binding!!.elementSizeOptionWidthSH.id,
                        binding!!.elementSizeOptionWidthText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })

        binding!!.elementSizeOptionHeightSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding!!.elementSizeOptionHeightText.text = progress.toString()
                listener.onElementHeightUpdate(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.elementSizeOptionHeightSeek.id,
                        binding!!.elementSizeOptionHeightSH.id,
                        binding!!.elementSizeOptionHeightText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.elementSizeOptionHeightSeek.id,
                        binding!!.elementSizeOptionHeightSH.id,
                        binding!!.elementSizeOptionHeightText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })


        return binding!!.root
    }

    fun setCurrentSize(width: Int, height: Int) {
        if (binding != null && isAdded && activity != null) {
            binding!!.elementSizeOptionWidthSeek.progress = width
            binding!!.elementSizeOptionHeightSeek.progress = height
        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.elementSizeControlsParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.elementSizeControlsParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }
}