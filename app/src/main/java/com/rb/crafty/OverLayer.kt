package com.rb.crafty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.doOnLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rb.crafty.dataObjects.GuideData
import com.rb.crafty.databinding.ActivityOverLayerBinding

class OverLayer : AppCompatActivity() {
    lateinit var binding: ActivityOverLayerBinding

    var currentPos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverLayerBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val gson = Gson()
        val jsonList = intent.getStringExtra("guidedList")!!
        val type = object : TypeToken<MutableList<GuideData>>(){}.type

        val guidedList =  gson.fromJson(jsonList, type) as MutableList<GuideData>

        val from = intent.getStringExtra("from")

        binding.overLayingText.text = guidedList[0].stepText

        binding.root.doOnLayout {
            binding.overLayingCard.x = guidedList[0].stepX - binding.overLayingCard.width/2
            binding.overLayingCard.y = guidedList[0].stepY - binding.overLayingCard.height/2
        }

        binding.overLayerNext.setOnClickListener {
            currentPos+=1
            if (from == "main") {
                if (currentPos == 1) {
                    binding.overLayerNext.text = "Okay"
                }

                if (currentPos == 2) {
                    finish()
                    return@setOnClickListener
                }
            }

            binding.overLayingCard.x = guidedList[currentPos].stepX
            binding.overLayingCard.y = guidedList[currentPos].stepY

            binding.overLayingText.text = guidedList[currentPos].stepText

        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_stable, R.anim.activity_stable)
    }
}