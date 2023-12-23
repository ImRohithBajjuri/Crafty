package com.rb.crafty.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rb.crafty.R
import com.rb.crafty.dataObjects.FontData
import com.rb.crafty.dataObjects.UIFontData
import com.rb.crafty.databinding.FontItemBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FontsAdapter(): RecyclerView.Adapter<FontsAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var fontsList: MutableList<UIFontData>
    var currentFont: FontData? = null
    var usageFrom: String = "normal"

    lateinit var colourUtils: ColourUtils

    lateinit var firestore: FirebaseFirestore
    var user: FirebaseUser? = null

    interface FontsAdapterListener {
        fun onFontSelected(fontData: FontData)

        fun onSearchFontAddClicked(fontData: FontData)
    }

    lateinit var listener: FontsAdapterListener


    constructor(context: Context, fontsList: MutableList<UIFontData>, usageFrom: String, listener: FontsAdapterListener) : this() {
        this.context = context
        this.fontsList = fontsList
        this.usageFrom = usageFrom
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FontItemBinding.inflate(LayoutInflater.from(context), parent, false)

        colourUtils = ColourUtils(context)

        firestore = Firebase.firestore
        user = Firebase.auth.currentUser
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.fontItemName.text = fontsList[position].fontData!!.fontName
        holder.binding.fontItemSubName.text = fontsList[position].fontData!!.fontName
        holder.binding.fontItemFamilyName.text = fontsList[position].fontData!!.fontFamily

        //Set the typeface.
        CoroutineScope(Dispatchers.Main).launch {
            if (fontsList[position].typeface != null) {
                holder.binding.fontItemIcon.typeface = fontsList[position].typeface
                holder.binding.fontItemName.typeface = fontsList[position].typeface
            }
        }

        holder.binding.fontItemFamilyName.visibility = View.GONE
        if (position != 0) {
            if (fontsList[position].fontData!!.fontFamily != fontsList[position - 1].fontData!!.fontFamily) {
                holder.binding.fontItemFamilyName.visibility = View.VISIBLE
            }
        }
        else {
            holder.binding.fontItemFamilyName.visibility = View.VISIBLE
        }


        //Highlight the selected font.
        holder.binding.fontItemSelected.visibility = View.GONE
        if (currentFont != null) {
            if (fontsList[position].fontData!!.id == currentFont!!.id) {
                holder.binding.fontItemSelected.visibility = View.VISIBLE
            }
        }


        //Show the add button if the adapter is used for search.
        if (usageFrom == "search") {
            holder.binding.fontItemSearchAddIcon.visibility = View.VISIBLE
        }
        else {
            holder.binding.fontItemSearchAddIcon.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return fontsList.size
    }

    inner class ViewHolder(val binding: FontItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (usageFrom == "normal") {
                    //Set the current font if its not set already.
                    if (currentFont == null) {
                        currentFont = fontsList[adapterPosition].fontData

                        notifyItemChanged(adapterPosition)

                        //Notify the listener.
                        listener.onFontSelected(fontsList[adapterPosition].fontData!!)

                        return@setOnClickListener
                    }

                    //Update the current font.
                    for (font in fontsList) {
                        //Get the previous position.
                        if (font.fontData!!.id == currentFont!!.id) {
                            val prevPos = fontsList.indexOf(font)
                            currentFont = fontsList[adapterPosition].fontData

                            notifyItemChanged(prevPos)
                            notifyItemChanged(adapterPosition)

                            break
                        }
                    }

                    //Notify the listener.
                    listener.onFontSelected(fontsList[adapterPosition].fontData!!)

                }
            }

            binding.fontItemSearchAddIcon.setOnClickListener {
                val fontData = fontsList[adapterPosition].fontData!!
                val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        //Notify the listener.
                        listener.onSearchFontAddClicked(fontData)
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
                it.startAnimation(anim)
            }


            darkMode(AppUtils.isDarkMode(context))
        }

        fun darkMode(isDark: Boolean) {
            if (usageFrom == "profile") {
                if (isDark) {
                    binding.fontItemName.setTextColor(colourUtils.darkModeDeepPurple)
                    binding.fontItemFamilyName.setTextColor(colourUtils.darkModeDeepPurple)
                    binding.fontItemSubName.setTextColor(colourUtils.darkModeDeepPurple)
                    binding.fontItemIcon.setTextColor(colourUtils.darkModeDeepPurple)
                }
                else {
                    binding.fontItemName.setTextColor(colourUtils.lightModeDeepPurple)
                    binding.fontItemFamilyName.setTextColor(colourUtils.lightModeDeepPurple)
                    binding.fontItemSubName.setTextColor(colourUtils.lightModeDeepPurple)
                    binding.fontItemIcon.setTextColor(colourUtils.lightModeDeepPurple)
                }
            }
        }
    }



    fun filterFonts(filteredList: MutableList<UIFontData>){
        fontsList = filteredList
        notifyDataSetChanged()
    }


}