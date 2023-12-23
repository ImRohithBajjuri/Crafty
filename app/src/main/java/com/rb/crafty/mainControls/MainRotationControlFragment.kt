package com.rb.crafty.mainControls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.children
import com.rb.crafty.dataObjects.MainCardData
import com.rb.crafty.databinding.FragmentMainRotationControlBinding
import com.rb.crafty.utils.AppUtils


class MainRotationControlFragment() : Fragment() {

    var binding: FragmentMainRotationControlBinding? = null

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
        binding = FragmentMainRotationControlBinding.inflate(inflater, container, false)

        binding!!.mainRotateOptionSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding!!.mainRotationOptionText.text = p1.toString()
                listener.onMainRotateUpdate(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.mainRotateOptionSeek.id,
                        binding!!.mainRotationOptionSH.id,
                        binding!!.mainRotationOptionText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.mainRotateOptionSeek.id,
                        binding!!.mainRotationOptionSH.id,
                        binding!!.mainRotationOptionText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })

        return binding!!.root
    }

    fun setCurrentMainRotation(mainCardData: MainCardData) {
        //Set the values to the controls.
        if (binding != null) {
            binding!!.mainRotateOptionSeek.progress = mainCardData.rotation
            binding!!.mainRotationOptionText.text = mainCardData.rotation.toString()
        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.mainRotationControlsParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.mainRotationControlsParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }

}