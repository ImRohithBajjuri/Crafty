package com.rb.crafty.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rb.crafty.databinding.LangItemBinding
import java.util.Locale

class AudioLanguagesAdapter(): RecyclerView.Adapter<AudioLanguagesAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var languages: MutableList<Locale>

    constructor(context: Context, languages: MutableList<Locale>) : this() {
        this.context = context
        this.languages = languages
    }


    inner class ViewHolder(val binding: LangItemBinding): RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LangItemBinding.inflate(LayoutInflater.from(context)))
    }

    override fun getItemCount(): Int {
        return languages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.langItemName.text = languages[position].language
    }
}