package com.rb.crafty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.rb.crafty.dataObjects.CardData
import com.rb.crafty.databinding.CardsUiItemBinding
import com.rb.crafty.sheets.CardOptionsSheet
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.Elements

class CardsUIAdapter(): RecyclerView.Adapter<CardsUIAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var dataList: MutableList<CardData>
    lateinit var callFrom: String
    lateinit var sheetListener: CardOptionsSheet.CardOptionsSheetListener

    constructor(context: Context, dataList: MutableList<CardData>, callFrom: String, sheetListener: CardOptionsSheet.CardOptionsSheetListener) : this() {
        this.context = context
        this.dataList = dataList
        this.callFrom = callFrom
        this.sheetListener = sheetListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardsUiItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Load the thumbnail.
        holder.elements.makeGreetrCardThumb(dataList[position], object :
            Elements.ThumbnailLoaderListener {
            override fun thumbnailLoaded(view: MaterialCardView) {
                val params = holder.binding.cardsUiIdeasItemCard.layoutParams as CoordinatorLayout.LayoutParams
                val id = holder.binding.cardsUiIdeasItemCard.id
                holder.binding.cardsUiItemParent.removeView(
                    holder.binding.cardsUiItemParent.findViewById(
                        id
                    )
                )
                view.id = id
                holder.binding.cardsUiItemParent.addView(view, params)
            }
        })
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(val binding: CardsUiItemBinding): RecyclerView.ViewHolder(binding.root) {
        var elements: Elements = Elements(context)

        init {
            val snackbar = AppUtils.buildSnackbar(context, "", binding.root)
            binding.cardsUiIdeasItemCard.setOnLongClickListener {
                var sheet: CardOptionsSheet? = null
                    sheet = CardOptionsSheet(dataList[adapterPosition], binding.cardsUiIdeasItemCard, callFrom, sheetListener)
                sheet.show((context as AppCompatActivity).supportFragmentManager, "CardsUIAdapter")
                true
            }

            binding.cardsUiItemParent.setOnClickListener {
                val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        var sheet: CardOptionsSheet? = null
                        sheet = CardOptionsSheet(dataList[adapterPosition], binding.cardsUiIdeasItemCard, callFrom, sheetListener)
                        sheet!!.show((context as AppCompatActivity).supportFragmentManager, "CardsUIAdapter")
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
                it.startAnimation(anim)
            }
        }

    }


}