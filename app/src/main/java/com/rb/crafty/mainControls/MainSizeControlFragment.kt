package com.rb.crafty.mainControls

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.children
import com.rb.crafty.dataObjects.MainCardData
import com.rb.crafty.databinding.FragmentMainSizeControlBinding
import com.rb.crafty.utils.AppUtils


class MainSizeControlFragment() : Fragment() {
    var binding: FragmentMainSizeControlBinding? = null

    lateinit var listener: AppUtils.ElementOptionsControlsListener
    lateinit var cardData: MainCardData

    constructor(listener: AppUtils.ElementOptionsControlsListener, cardData: MainCardData) : this() {
        this.listener = listener
        this.cardData = cardData
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentMainSizeControlBinding.inflate(inflater, container, false)


        binding!!.mainSizeOptionWidthSeek.max = cardData.maxWidth
        binding!!.mainSizeOptionHeightSeek.max = cardData.maxHeight

        binding!!.mainSizeOptionWidthSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding!!.mainSizeOptionWidthText.text = p1.toString()

                listener.onMainWidthUpdate(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.mainSizeOptionWidthSeek.id,
                        binding!!.mainSizeOptionWidthSH.id,
                        binding!!.mainSizeOptionWidthText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.mainSizeOptionWidthSeek.id,
                        binding!!.mainSizeOptionWidthSH.id,
                        binding!!.mainSizeOptionWidthText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })

        binding!!.mainSizeOptionHeightSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding!!.mainSizeOptionHeightText.text = p1.toString()

                listener.onMainHeightUpdate(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.mainSizeOptionHeightSeek.id,
                        binding!!.mainSizeOptionHeightSH.id,
                        binding!!.mainSizeOptionHeightText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.mainSizeOptionHeightSeek.id,
                        binding!!.mainSizeOptionHeightSH.id,
                        binding!!.mainSizeOptionHeightText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })

        return binding!!.root
    }



    fun setCurrentMainSize(mainCardData: MainCardData) {

        //Set the current progress to the seekbar and texts.
        if (binding != null) {
            binding!!.mainSizeOptionWidthSeek.progress = mainCardData.width
            binding!!.mainSizeOptionHeightSeek.progress = mainCardData.height

            binding!!.mainSizeOptionWidthText.text = mainCardData.width.toString()
            binding!!.mainSizeOptionHeightText.text = mainCardData.height.toString()
        }

    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.mainSizeOptionLayout.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.mainSizeOptionLayout.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }
}