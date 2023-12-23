package com.rb.crafty.mainControls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.children
import com.rb.crafty.dataObjects.MainCardData
import com.rb.crafty.databinding.FragmentMainCornersControlBinding
import com.rb.crafty.utils.AppUtils

class MainCornersControlFragment() : Fragment() {
    var binding: FragmentMainCornersControlBinding? = null

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
        binding = FragmentMainCornersControlBinding.inflate(inflater, container, false)

        binding!!.mainCornersOptionSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding!!.mainCornersOptionText.text = p1.toString()

                listener.onMainCornersUpdate(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.mainCornersOptionSeek.id,
                        binding!!.mainCornersOptionSH.id,
                        binding!!.mainCornersOptionText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.mainCornersOptionSeek.id,
                        binding!!.mainCornersOptionSH.id,
                        binding!!.mainCornersOptionText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })


        return binding!!.root
    }



    fun setCurrentMainCorners(mainCardData: MainCardData) {
        //Set the values to controls and text.
        if (binding != null) {
            binding!!.mainCornersOptionSeek.progress = mainCardData.cornerRadius
            binding!!.mainCornersOptionText.text = mainCardData.cornerRadius.toString()
        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.mainCornersControlParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.mainCornersControlParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }


}