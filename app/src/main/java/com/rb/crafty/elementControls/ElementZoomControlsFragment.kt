package com.rb.crafty.elementControls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.children
import com.rb.crafty.R
import com.rb.crafty.databinding.FragmentElementZoomControlsBinding
import com.rb.crafty.utils.AppUtils


class ElementZoomControlsFragment() : Fragment() {
    var binding: FragmentElementZoomControlsBinding? = null
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
    ): View{
        // Inflate the layout for this fragment
        binding = FragmentElementZoomControlsBinding.inflate(inflater, container, false)
        binding!!.elementZoomXSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val zoomX = progress.toFloat()/10
                binding!!.elementZoomXText.text = zoomX.toString()
                listener.onElementZoomXUpdate(zoomX.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.elementZoomXSeek.id,
                        binding!!.elementZoomXSH.id,
                        binding!!.elementZoomXText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.elementZoomXSeek.id,
                        binding!!.elementZoomXSH.id,
                        binding!!.elementZoomXText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })

        binding!!.elementZoomYSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val zoomY = progress.toFloat()/10
                binding!!.elementZoomYText.text = zoomY.toString()
                listener.onElementZoomYUpdate(zoomY)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.elementZoomYSeek.id,
                        binding!!.elementZoomYSH.id,
                        binding!!.elementZoomYText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.elementZoomYSeek.id,
                        binding!!.elementZoomYSH.id,
                        binding!!.elementZoomYText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })
        return binding!!.root
    }

    fun setCurrentZoom(zoomX: Float, zoomY: Float) {
        if (binding != null && isAdded && activity != null) {
            binding!!.elementZoomXSeek.progress = (zoomX * 10).toInt()
            binding!!.elementZoomYSeek.progress = (zoomY * 10).toInt()
        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.elementZoomControlsParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.elementZoomControlsParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }

}