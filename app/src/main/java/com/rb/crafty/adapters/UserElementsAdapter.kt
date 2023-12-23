package com.rb.crafty.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.UnicodeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialCalendar
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.databinding.UserElementItemBinding
import com.rb.crafty.sheets.ElementInfoSheet
import com.rb.crafty.sheets.UserElementsSheet
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.Elements
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserElementsAdapter() : RecyclerView.Adapter<UserElementsAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var elementsList: MutableList<ElementData>
    lateinit var elements: Elements
    lateinit var listener: UserElementsSheet.UserElementsSheetListener
    lateinit var callFrom: String

    constructor(
        context: Context,
        elementsList: MutableList<ElementData>,
        callFrom: String,
        listener: UserElementsSheet.UserElementsSheetListener
    ) : this() {
        this.context = context
        this.elementsList = elementsList
        this.callFrom = callFrom
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        elements = Elements(context)
        val binding =
            UserElementItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return elementsList.size
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.userElementItemCard.removeAllViews()
        CoroutineScope(Dispatchers.IO).launch {
            elements.buildElement(elementsList[position], object : Elements.ElementBuilderListener {
                override fun onElementReady(view: View) {
                    holder.binding.userElementItemCard.addView(view)
                }

                override fun onElementFailed() {
                }

            })
        }

        holder.binding.userElementItemName.text = elementsList[position].elementName
    }

    inner class ViewHolder(val binding: UserElementItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.userElementItemCard.setOnClickListener {
                val anim = AnimUtils.pressAnim(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        if (callFrom == UserElementsSheet.FROM_CREATOR) {
                            listener.onElementClicked(elementsList[adapterPosition])
                        } else {
                            val sheet = ElementInfoSheet(elementsList[adapterPosition])
                            sheet.show(
                                (context as AppCompatActivity).supportFragmentManager,
                                "UseCaseTwo"
                            )
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                })
                it.startAnimation(anim)
            }

            binding.userElementItemCard.setOnLongClickListener {
                val sheet = ElementInfoSheet(elementsList[adapterPosition])
                sheet.show((context as AppCompatActivity).supportFragmentManager, "UseCaseTwo")
                true
            }
        }
    }
}