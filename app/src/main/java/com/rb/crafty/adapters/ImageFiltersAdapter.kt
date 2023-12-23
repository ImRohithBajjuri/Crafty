package com.rb.crafty.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rb.crafty.R
import com.rb.crafty.dataObjects.ImageFilterData
import com.rb.crafty.databinding.ImageFilterItemBinding
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ImageFilters
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.coroutines.*

class ImageFiltersAdapter(): RecyclerView.Adapter<ImageFiltersAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var filtersList: MutableList<ImageFilterData>
    lateinit var placeHolder: Bitmap
    lateinit var imageFilters: ImageFilters
    lateinit var loadedFilters: MutableList<Bitmap>

    var currentSelectedFilter: ImageFilterData? = null

    lateinit var listener: AppUtils.ElementOptionsControlsListener

    lateinit var firestore: FirebaseFirestore
    var user: FirebaseUser? = null

    constructor(context: Context, filtersList: MutableList<ImageFilterData>, loadedFilters: MutableList<Bitmap>, listener: AppUtils.ElementOptionsControlsListener) : this() {
        this.context = context
        this.filtersList = filtersList
        this.loadedFilters = loadedFilters
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ImageFilterItemBinding.inflate(LayoutInflater.from(context), parent, false)

        firestore = Firebase.firestore
        user = Firebase.auth.currentUser

        imageFilters = ImageFilters(context)

        placeHolder = BitmapFactory.decodeResource(context.resources, R.drawable.filter_placeholder)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filtersList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.filterName.text = filtersList[position].filterName

        runBlocking {
            CoroutineScope(Dispatchers.Default).launch {
                withContext(Dispatchers.Main) {
                   Glide.with(context).asDrawable().load(loadedFilters[position]).into(holder.binding.filterImage)
                }
            }
        }


        holder.binding.filterImage.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        if (currentSelectedFilter != null) {
            if (filtersList[position].filterName == currentSelectedFilter!!.filterName) {
                holder.binding.filterImage.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,
                    R.color.deepPurple
                ))
            }
        }
    }

    inner class ViewHolder (var binding: ImageFilterItemBinding): RecyclerView.ViewHolder(binding.root) {
        var gpuFilter: GPUImage = GPUImage(context)

        init {
            gpuFilter.setImage(placeHolder)

            binding.root.setOnClickListener {

                val data = filtersList[adapterPosition]
                val prevPos = filtersList.indexOf(currentSelectedFilter)

                currentSelectedFilter = filtersList[adapterPosition]

                //Update the selected pattern.
                notifyItemChanged(prevPos)
                notifyItemChanged(adapterPosition)

                //Notify the listener.
                listener.onImageFilterUpdate(filtersList[adapterPosition])
            }
        }
    }

}