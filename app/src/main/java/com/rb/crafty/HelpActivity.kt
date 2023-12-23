package com.rb.crafty

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rb.crafty.adapters.HelpStepItemAdapter
import com.rb.crafty.dataObjects.HelpData
import com.rb.crafty.databinding.ActivityHelpBinding
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils

class HelpActivity : AppCompatActivity() {
    lateinit var binding: ActivityHelpBinding

    lateinit var colourUtils: ColourUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        colourUtils = ColourUtils(this)


        val helpFor = intent.getStringExtra("helpFor")

        val stepsList = ArrayList<HelpData>()
        when (helpFor) {
            "create" -> stepsList.addAll(AppUtils.createCardHelpList())

            "exportCardImage" -> stepsList.addAll(AppUtils.createCardExportAsImageHelpList())

            "exportCard" -> stepsList.addAll(AppUtils.createCardExportHelpList())

            "addFav" -> stepsList.addAll(AppUtils.createCardFavingHelpList(this))

            "hide" -> stepsList.addAll(AppUtils.createCardHidingHelpList())
        }

        val adapter = HelpStepItemAdapter(this, stepsList)
        val layoutManager = LinearLayoutManager(this)
        binding.helpStepsRecy.layoutManager = layoutManager
        binding.helpStepsRecy.adapter = adapter

        binding.helpBackButton.setOnClickListener {
            finish()
        }

        darkMode()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_puller, R.anim.activity_close)
    }

    fun darkMode() {
        if (AppUtils.isDarkMode(this)) {
            binding.helpParent.background = ColorDrawable(Color.BLACK)

            binding.helpHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.helpBackButton.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)

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
            binding.helpParent.background = ColorDrawable(colourUtils.lightModeParentPurple)

            binding.helpHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.helpBackButton.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                window.insetsController!!.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
            else {
                var flags: Int = binding.root.getSystemUiVisibility()
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.setSystemUiVisibility(flags)
            }

            window.statusBarColor = colourUtils.lightModeParentPurple
        }
    }

}