package com.rb.crafty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rb.crafty.dataObjects.PatternData
import com.rb.crafty.databinding.PatternItemBinding
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.Patterns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PatternsAdapter(): RecyclerView.Adapter<PatternsAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var dataList: MutableList<PatternData>
    lateinit var listener: AppUtils.ElementOptionsControlsListener
    var currentSelectedPattern: PatternData? = null
    lateinit var recyclerView: RecyclerView
    var patternsFor: String = "element"
    var user: FirebaseUser? = null
    lateinit var firestore: FirebaseFirestore

    constructor(context: Context, dataList: MutableList<PatternData>, currentSelectedPattern: PatternData?, patternsFor: String, listener: AppUtils.ElementOptionsControlsListener): this() {
        this.context = context
        this.dataList = dataList
        this.currentSelectedPattern = currentSelectedPattern
        this.patternsFor = patternsFor
        this.listener = listener
    }

    lateinit var patterns: Patterns

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = PatternItemBinding.inflate(LayoutInflater.from(context), parent, false)

        user = Firebase.auth.currentUser
        firestore = Firebase.firestore

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = patterns.loadPatternWithId(dataList[position],300, 500)

                withContext(Dispatchers.Main) {
                    holder.binding.patternItemImg.setImageBitmap(bitmap)
                }
            }
        }


        holder.binding.patternItemCard.strokeWidth = 0
        if (currentSelectedPattern != null) {
            if (currentSelectedPattern!!.patternName == dataList[position].patternName) {
                holder.binding.patternItemCard.strokeWidth = AppUtils.pxToDp(context, 3)
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(val binding: PatternItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            patterns = Patterns(context)

            binding.patternItemCard.setOnClickListener {
                val pattern = dataList[adapterPosition]
                val prevPos = dataList.indexOf(currentSelectedPattern)

                currentSelectedPattern = dataList[adapterPosition]


                //Update the selected pattern.
                notifyItemChanged(prevPos)
                notifyItemChanged(adapterPosition)

                //Notify the listener.
                listener.onPatternDesignUpdate(dataList[adapterPosition], patternsFor)
            }
        }
    }


}