package com.rb.crafty.mainControls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.children
import com.rb.crafty.R
import com.rb.crafty.databinding.FragmentMainZoomControlsBinding
import com.rb.crafty.utils.AppUtils

class MainZoomControlsFragment() : Fragment() {

    var binding: FragmentMainZoomControlsBinding? = null
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
        binding = FragmentMainZoomControlsBinding.inflate(inflater, container, false)
        binding!!.mainZoomXSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val zoomX = progress.toFloat()/10
                binding!!.mainZoomXText.text = zoomX.toString()
                listener.onMainZoomXUpdate(zoomX.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.mainZoomXSeek.id,
                        binding!!.mainZoomXSH.id,
                        binding!!.mainZoomXText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.mainZoomXSeek.id,
                        binding!!.mainZoomXSH.id,
                        binding!!.mainZoomXText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })

        binding!!.mainZoomYSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val zoomY = progress.toFloat()/10
                binding!!.mainZoomYText.text = zoomY.toString()
                listener.onMainZoomYUpdate(zoomY)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.mainZoomYSeek.id,
                        binding!!.mainZoomYSH.id,
                        binding!!.mainZoomYText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.mainZoomYSeek.id,
                        binding!!.mainZoomYSH.id,
                        binding!!.mainZoomYText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })
        return binding!!.root
    }

    fun setCurrentZoom(zoomX: Float, zoomY: Float) {
        if (binding != null && isAdded && activity != null) {
            binding!!.mainZoomXSeek.progress = (zoomX * 10).toInt()
            binding!!.mainZoomYSeek.progress = (zoomY * 10).toInt()
        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.mainZoomControlsParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.mainZoomControlsParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }

}