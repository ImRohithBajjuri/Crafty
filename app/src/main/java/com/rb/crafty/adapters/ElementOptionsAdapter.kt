package com.rb.crafty.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rb.crafty.R
import com.rb.crafty.dataObjects.ElementOptionData
import com.rb.crafty.databinding.ElementOptionItemBinding

class ElementOptionsAdapter(): RecyclerView.Adapter<ElementOptionsAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var dataList: MutableList<ElementOptionData>
    lateinit var drawableList: MutableList<Int>
    var currentSelectedOption: ElementOptionData? = null

    lateinit var recyclerView: RecyclerView

    interface ElementOptionsAdapterListener {
        fun optionSelected(elementOptionData: ElementOptionData)
    }
    lateinit var listener: ElementOptionsAdapterListener

    constructor(context: Context, dataList: MutableList<ElementOptionData>, drawableList: MutableList<Int>, listener: ElementOptionsAdapterListener) : this() {
        this.context = context
        this.dataList = dataList
        this.drawableList = drawableList
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ElementOptionItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.optionImage.setImageDrawable(ContextCompat.getDrawable(context, drawableList[position]))
        holder.binding.optionName.text = dataList[position].name

        //Highlight the selected option.
        if (currentSelectedOption != null) {
            holder.binding.optionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context,
                R.color.deepPurple
            ))
            holder.binding.optionParent.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            holder.binding.optionName.setTextColor(ContextCompat.getColor(context,
                R.color.deepPurple
            ))
            if (currentSelectedOption!!.optionType == dataList[position].optionType) {
                holder.binding.optionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context,
                    R.color.white
                ))
                holder.binding.optionParent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,
                    R.color.deepPurple
                ))
                holder.binding.optionName.setTextColor(ContextCompat.getColor(context,
                    R.color.white
                ))
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(binder: ElementOptionItemBinding): androidx.recyclerview.widget.RecyclerView.ViewHolder(binder.root) {
        val binding = binder

        init {
            binding.optionParent.setOnClickListener {
                //Update the current selected option and it's UI.
                var prevPos = dataList.indexOf(currentSelectedOption)
                for (data in dataList) {
                    if (data.optionType == currentSelectedOption!!.optionType) {
                        prevPos = dataList.indexOf(data)
                        break
                    }
                }

                currentSelectedOption = dataList[adapterPosition]

                notifyItemChanged(prevPos)
                notifyItemChanged(adapterPosition)

                //Notify with the listener.
                listener.optionSelected(dataList[adapterPosition])
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }
}