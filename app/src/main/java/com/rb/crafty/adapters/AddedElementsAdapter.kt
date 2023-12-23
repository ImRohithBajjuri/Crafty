package com.rb.crafty.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rb.crafty.CreatorActivity
import com.rb.crafty.R
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.databinding.AddedElementItemBinding

class AddedElementsAdapter(): RecyclerView.Adapter<AddedElementsAdapter.ViewHolder>()  {

    lateinit var context: Context
    lateinit var elementsList: MutableList<ElementData>
    var currentFocusedElement: Int = 0

    interface ClickListener{
        fun onClick(position: Int)
    }

    lateinit var clickLstener: ClickListener

    constructor(context: Context, elementsList: MutableList<ElementData>) : this() {
        this.context = context
        this.elementsList = elementsList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AddedElementItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val elementData = elementsList[position]

        holder.binding.addedElement.text = elementData.elementName
        //IF the element name is empty then set it with it's element type.
        if (TextUtils.isEmpty(elementData.elementName.trim())) {
            holder.binding.addedElement.text = elementData.elementType
        }

        //Set the proper UI
        holder.binding.addedElementCard.setCardBackgroundColor(ContextCompat.getColor(context,
            R.color.deepPurpleLight
        ))
        holder.binding.addedElement.setTextColor(ContextCompat.getColor(context, R.color.deepPurple))

        if (currentFocusedElement == position) {
            holder.binding.addedElementCard.setCardBackgroundColor(ContextCompat.getColor(context,
                R.color.deepPurple
            ))
            holder.binding.addedElement.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return elementsList.size
    }

    inner class ViewHolder(itemView: AddedElementItemBinding): RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
        init {
            binding.addedElementCard.setOnClickListener {
                //Notify the elements bar
                clickLstener.onClick(adapterPosition)

                //Update the UI.
                val previousPosition = currentFocusedElement
                currentFocusedElement = adapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(currentFocusedElement)

            }

            binding.addedElementCard.setOnLongClickListener {
                (context as CreatorActivity).elementsMedium.showElementPopUp()

                true
            }
        }
    }

    fun setListener(listener: ClickListener) {
      clickLstener = listener
    }

}