package com.rb.crafty

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.rb.crafty.dataObjects.CardData
import com.rb.crafty.databinding.ActivityCardViewerBinding
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import com.rb.crafty.utils.Elements

class CardViewer : AppCompatActivity() {
    lateinit var binding: ActivityCardViewerBinding
    lateinit var elements: Elements
    lateinit var colourUtils: ColourUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardViewerBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        colourUtils = ColourUtils(this)
        elements = Elements(this)

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("cardData", CardData::class.java)
        } else {
            intent.getSerializableExtra("cardData") as CardData
        }

       elements.makeGreetrCardThumb(data!!, object : Elements.ThumbnailLoaderListener {
            override fun thumbnailLoaded(view: MaterialCardView) {
                binding.cardViewerContainer.addView(view)
            }
        })

        binding.cardViewerName.text = data.cardName
        binding.cardViewerBy.text = "Created by : ${data.by}"
        binding.cardViewerDate.text = "Created on : ${AppUtils.getNormalTime(data.createdOn)}"
        if (data.updatedOn == 0L) {
            binding.cardViewerUpdated.text = "Not updated yet"
        }
        else {
            binding.cardViewerUpdated.text = "Updated on : ${AppUtils.getNormalTime(data.updatedOn)}"
        }

        if (data.elementsList == null) {
            binding.cardViewerElementsNum.text = "No.of elements : 0"
        }
        else {
            binding.cardViewerElementsNum.text = "No.of elements : ${data.elementsList!!.size}"
        }

        binding.cardViewerCardId.text = "Card ID : ${data.id}"

        binding.caredViewerBackButton.setOnClickListener {
            finish()
        }

        darkMode(AppUtils.isDarkMode(this))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_puller, R.anim.activity_close)
    }

    fun darkMode(isDark: Boolean) {
        if (isDark) {

            binding.cardViewerParent.setBackgroundColor(Color.BLACK)
            binding.cardViewerHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.caredViewerBackButton.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)
            binding.cardViewerDetailsHeader.setTextColor(colourUtils.darkModeDeepPurple)


            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
            else {
                var flags: Int =
                    window.decorView.getSystemUiVisibility()

                flags =
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

                window.decorView.setSystemUiVisibility(flags)

            }
            window.statusBarColor = Color.BLACK
        }
        else {

            binding.cardViewerParent.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGrey))
            binding.cardViewerHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.caredViewerBackButton.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)
            binding.cardViewerDetailsHeader.setTextColor(colourUtils.lightModeDeepPurple)


            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                window.insetsController!!.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
            else {
                var flags: Int = binding.root.getSystemUiVisibility()
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.setSystemUiVisibility(flags)
            }

            window.statusBarColor = ContextCompat.getColor(this, R.color.lightGrey)

        }
    }

}