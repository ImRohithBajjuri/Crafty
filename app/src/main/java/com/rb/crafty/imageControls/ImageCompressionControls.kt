package com.rb.crafty.imageControls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.children
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.dataObjects.ImageData
import com.rb.crafty.databinding.FragmentImageCompressionControlsBinding


class ImageCompressionControls() : Fragment() {
    var binding: FragmentImageCompressionControlsBinding? = null

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
        binding = FragmentImageCompressionControlsBinding.inflate(inflater, container, false)

        binding!!.imageCompressionOptionSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //Update the text.
                binding!!.imageCompressionOptionText.text = progress.toString()

                listener.onImageCompressUpdate(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.imageCompressionOptionSeek.id,
                        binding!!.imageCompressionOptionSH.id,
                        binding!!.imageCompressionOptionText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.imageCompressionOptionSeek.id,
                        binding!!.imageCompressionOptionSH.id,
                        binding!!.imageCompressionOptionText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })

        return binding!!.root
    }

    fun setCurrentCompression(elementData: ElementData) {
        //Get the image data.
        val imageData = elementData.data as ImageData

        //Set the available values.
        if (binding != null) {
            binding!!.imageCompressionOptionSeek.progress = imageData.quality
            binding!!.imageCompressionOptionText.text = imageData.quality.toString()
        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.imageCompressionControlsParent.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.imageCompressionControlsParent.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }

}