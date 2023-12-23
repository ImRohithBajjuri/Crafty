package com.rb.crafty.sheets

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.crafty.R
import com.rb.crafty.databinding.FragmentSavingSheetBinding
import com.rb.crafty.utils.AppUtils


class SavingSheet() : BottomSheetDialogFragment() {

    lateinit var binding: FragmentSavingSheetBinding

    lateinit var bitmap: Bitmap

    lateinit var filType: String

    lateinit var fileName: String

    var isElement: Boolean = false

    lateinit var mediaListener: AppUtils.MediaListener

    interface SavingSheetListener {
        fun onImageSaveProcessCompleted (path: String, isElement: Boolean)
    }

    lateinit var savingSheetListener: SavingSheetListener



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.PurpleBottomSheetDialogStyle)
        return super.onCreateDialog(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSavingSheetBinding.inflate(inflater, container, false)

        binding.savingSheetProgress.visibility = View.GONE


        return binding.root
    }

    fun updateProgress(progress: Int) {
        binding.savingSheetProgress.text = "$progress%"
        binding.savingSheetProgress.visibility = View.VISIBLE
    }

}