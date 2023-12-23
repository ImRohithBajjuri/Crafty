package com.rb.crafty.sheets

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.crafty.R
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.databinding.FragmentElementInfoSheetBinding
import com.rb.crafty.utils.AppUtils


class ElementInfoSheet() : BottomSheetDialogFragment() {

    lateinit var binding: FragmentElementInfoSheetBinding

    lateinit var elementData: ElementData

    constructor(elementData: ElementData) : this() {
        this.elementData = elementData
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.PurpleBottomSheetDialogStyle)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        binding = FragmentElementInfoSheetBinding.inflate(inflater, container, false)

        binding.elementInfoName.text = elementData.elementName
        binding.elementInfoBy.text = "Created by : ${elementData.elementBy}"
        binding.elementInfoDate.text = "Created on : ${AppUtils.getNormalTime(elementData.createdOn)}"

        binding.elementInfoType.text = "Element type : ${elementData.elementType}"

        binding.elementInfoId.text = "Card ID : ${elementData.id}"

        return binding.root
    }
}