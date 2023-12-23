package com.rb.crafty.textControls

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.databinding.FragmentTextInputBinding


class TextInputFragment() : Fragment() {

    var binding: FragmentTextInputBinding? = null

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
        binding = FragmentTextInputBinding.inflate(inflater, container, false)

        binding!!.textInputOptionField.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listener.onTextUpdate(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        return binding!!.root
    }

    fun setCurrentText(text: String) {
        if (binding != null) {
            binding!!.textInputOptionField.setText(text)
        }
    }

}