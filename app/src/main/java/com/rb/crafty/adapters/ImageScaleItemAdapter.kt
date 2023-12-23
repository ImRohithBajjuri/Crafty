package com.rb.crafty.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rb.crafty.R
import com.rb.crafty.dataObjects.ImageScaleTypeData
import com.rb.crafty.databinding.ScaleTypeItemBinding

class ImageScaleItemAdapter(): RecyclerView.Adapter<ImageScaleItemAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var datalist: MutableList<ImageScaleTypeData>

    var currentScaleType: ImageScaleTypeData? = null


    interface ImageScaleAdapterListener {
        fun onScaleSelected(scaleTypeData: ImageScaleTypeData)
    }
    lateinit var listener: ImageScaleAdapterListener


    constructor(context: Context, datalist: MutableList<ImageScaleTypeData>, listener: ImageScaleAdapterListener) : this() {
        this.context = context
        this.datalist = datalist
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ScaleTypeItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.scaleTypeItemText.text = datalist[position].typeName

        //Set the selected scale type
        if (currentScaleType != null) {
            holder.binding.scaleTypeItemCard.setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT))
            holder.binding.scaleTypeItemText.setTextColor(ContextCompat.getColor(context,
                R.color.deepPurple
            ))
            if (datalist[position].typeName == currentScaleType!!.typeName) {
                holder.binding.scaleTypeItemCard.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context,
                    R.color.deepPurple
                )))
                holder.binding.scaleTypeItemText.setTextColor(ContextCompat.getColor(context,
                    R.color.white
                ))
            }
        }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    inner class ViewHolder(binder: ScaleTypeItemBinding): RecyclerView.ViewHolder(binder.root) {
        val binding = binder
        init {
            binding.scaleTypeItemCard.setOnClickListener {
                //Update the current scale type.
                val prevPos = datalist.indexOf(currentScaleType)
                currentScaleType = datalist[adapterPosition]

                notifyItemChanged(prevPos)
                notifyItemChanged(adapterPosition)

                //Notify the listener.
                listener.onScaleSelected(datalist[adapterPosition])
            }
        }

    }
}