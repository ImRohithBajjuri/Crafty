package com.rb.crafty.sheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.crafty.R
import com.rb.crafty.dataObjects.CardData
import com.rb.crafty.databinding.FragmentCardInfoSheetBinding
import com.rb.crafty.utils.AppUtils


class CardInfoSheet() : BottomSheetDialogFragment() {

    lateinit var binding: FragmentCardInfoSheetBinding

    lateinit var cardData: CardData

    constructor(cardData: CardData) : this() {
        this.cardData = cardData
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.PurpleBottomSheetDialogStyle)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCardInfoSheetBinding.inflate(inflater, container, false)


        binding.cardInfoName.text = cardData.cardName
        binding.cardInfoBy.text = "Created by : ${cardData.by}"
        binding.cardInfoDate.text = "Created on : ${AppUtils.getNormalTime(cardData.createdOn)}"
        if (cardData.updatedOn == 0L) {
            binding.cardInfoUpdated.text = "Not updated yet"
        }
        else {
            binding.cardInfoUpdated.text = "Updated on : ${AppUtils.getNormalTime(cardData.updatedOn)}"
        }

        if (cardData.elementsList == null) {
            binding.cardInfoElementsNum.text = "No.of elements : 0"
        }
        else {
            binding.cardInfoElementsNum.text = "No.of elements : ${cardData.elementsList!!.size}"
        }

        binding.cardInfoCardId.text = "Card ID : ${cardData.id}"

        return binding.root
    }
}