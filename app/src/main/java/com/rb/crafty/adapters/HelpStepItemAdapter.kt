package com.rb.crafty.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rb.crafty.dataObjects.HelpData
import com.rb.crafty.databinding.HelpStepItemBinding
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils

class HelpStepItemAdapter(): RecyclerView.Adapter<HelpStepItemAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var stepsList: MutableList<HelpData>

    lateinit var colourUtils: ColourUtils

    constructor(context: Context, stepsList: MutableList<HelpData>) : this() {
        this.context = context
        this.stepsList = stepsList
    }


    inner class ViewHolder(val binding: HelpStepItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            darkMode()
        }

        fun darkMode() {
            if (AppUtils.isDarkMode(context)) {
                binding.helpStepTxt.setTextColor(Color.WHITE)
                binding.helpStepNum.setTextColor(colourUtils.darkModeDeepPurple)
            }
            else {
                binding.helpStepTxt.setTextColor(Color.BLACK)
                binding.helpStepNum.setTextColor(colourUtils.lightModeDeepPurple)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        colourUtils = ColourUtils(context)
        return ViewHolder(HelpStepItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return stepsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.helpStepNum.text = "Step ${position + 1}"
        holder.binding.helpStepTxt.text = stepsList[position].text
        holder.binding.helpStepImg.setImageDrawable(null)
        if (stepsList[position].img != 0) {
            Glide.with(context).asDrawable().load(stepsList[position].img).into(holder.binding.helpStepImg)
        }
    }
}