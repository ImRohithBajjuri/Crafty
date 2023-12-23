package com.rb.crafty.textControls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.children
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.dataObjects.TextData
import com.rb.crafty.databinding.FragmentTextSizeControlsBinding


class TextSizeControlsFragment() : Fragment() {
    var binding: FragmentTextSizeControlsBinding? = null

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
        binding = FragmentTextSizeControlsBinding.inflate(inflater, container, false)

        binding!!.textSizeOptionViewer.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding!!.textSizeOptionText.text = progress.toString()
                listener.onTextSizeUpdate(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Show only the current seek.
                handleFocusedControlVisibility(
                    true,
                    intArrayOf(
                        binding!!.textSizeOptionViewer.id,
                        binding!!.textSizeOptionSH.id,
                        binding!!.textSizeOptionText.id
                    )
                )
                listener.controlsInFocus()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Show all.
                handleFocusedControlVisibility(
                    false,
                    intArrayOf(
                        binding!!.textSizeOptionViewer.id,
                        binding!!.textSizeOptionSH.id,
                        binding!!.textSizeOptionText.id
                    )
                )
                listener.controlsNotInFocus()
            }

        })
        return binding!!.root
    }

    fun setCurrentSize(textData: TextData) {
        //Show the current size in the controls.
        if (binding != null) {
            binding!!.textSizeOptionViewer.progress = textData.size
            binding!!.textSizeOptionText.text = textData.size.toString()
        }
    }

    fun handleFocusedControlVisibility(isControlInFocus: Boolean, ids: IntArray) {
        if (!isControlInFocus) {
            for (view in binding!!.textSizeOptionLayout.children) {
                view.visibility = View.VISIBLE
            }
            return
        }
        for (view in binding!!.textSizeOptionLayout.children) {
            if (!ids.contains(view.id)) {
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }

}