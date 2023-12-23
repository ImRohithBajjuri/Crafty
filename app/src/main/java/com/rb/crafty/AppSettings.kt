package com.rb.crafty

import android.animation.LayoutTransition
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.rb.crafty.databinding.ActivityAppSettingsBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import com.rb.crafty.utils.Elements

class AppSettings : AppCompatActivity() {
    lateinit var binding: ActivityAppSettingsBinding

    lateinit var colourUtils: ColourUtils

    lateinit var firestore: FirebaseFirestore

    var user: FirebaseUser? = null

    var darkChanged = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSettingsBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        firestore = Firebase.firestore
        user = Firebase.auth.currentUser

        //Initialize ads if required.
        val adInitializeListener = OnInitializationCompleteListener {

        }
        if (user != null) {
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {
                if (!it.result.exists()) {
                    val adRequest = AdRequest.Builder().build()
                    binding.appSettingsBanner.loadAd(adRequest)
                    binding.appSettingsBanner.visibility = View.VISIBLE
                }
                else {
                    binding.appSettingsBanner.visibility = View.GONE
                }
            }
        }
        else {
            binding.appSettingsBanner.visibility = View.VISIBLE
        }



        colourUtils = ColourUtils(this)

        val prefs = getSharedPreferences(AppUtils.APP_PREFS, MODE_PRIVATE)
        val theme = prefs.getString(AppUtils.APP_THEME_KEY, AppUtils.APP_THEME_LIGHT)

        binding.settingsThemeTxt.text = theme

        when (theme) {
            AppUtils.APP_THEME_LIGHT ->{
                binding.settingsThemeGroup.check(R.id.settingsLightTheme)

                binding.settingThemeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.light_mode_35dp))
            }

            AppUtils.APP_THEME_DARK ->{
                binding.settingsThemeGroup.check(R.id.settingsDarkTheme)
                binding.settingThemeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dark_mode_35dp))
            }

            AppUtils.APP_THEME_SYSTEM -> { binding.settingsThemeGroup.check(R.id.settingsSystemTheme)

                if (AppUtils.isDarkMode(this))  {
                    binding.settingThemeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dark_mode_35dp))

                }
                else {
                    binding.settingThemeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.light_mode_35dp))

                }
            }
        }

        val isHighlight = prefs.getBoolean(AppUtils.ELEMENT_HIGHLIGHT_KEY, true)

        binding.settingsElementsHighlight.isChecked = isHighlight

        if (isHighlight) {
            binding.settingsElementHighlightColourLay.visibility = View.VISIBLE
        }
        else {
            binding.settingsElementHighlightColourLay.visibility = View.GONE
        }


        val highlightColour = prefs.getString(AppUtils.ELEMENT_HIGHLIGHT_COLOUR_KEY, "#311B92")
        binding.settingsElementHighlightColourTxt.text = highlightColour
        TextViewCompat.setCompoundDrawableTintList(binding.settingsElementHighlightColourTxt, ColorStateList.valueOf(Color.parseColor(highlightColour)))


        val highlightCorners = prefs.getInt(AppUtils.ELEMENT_HIGHLIGHT_CORNERS_KEY, 20)
        binding.settingsElementHighlightCorners.progress = highlightCorners
        binding.settingsElementHighlightCornersTxt.text = highlightCorners.toString()


        val elementMenu = prefs.getBoolean(AppUtils.ELEMENT_MENU_KEY, true)
        binding.settingsElementsMenu.isChecked = elementMenu


        val layoutTransition = LayoutTransition()
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.settingsParent.layoutTransition = layoutTransition


        binding.settingsThemeCard.setOnClickListener {
            if (binding.settingsThemeGroup.visibility == View.VISIBLE) {
                binding.settingsThemeGroup.visibility = View.GONE

                binding.settingsThemeArrow.clearAnimation()
                val anim = AnimUtils.rotateAnim(binding.settingsThemeArrow.rotation, 0f)
                anim.fillAfter = true

                binding.settingsThemeArrow.startAnimation(anim)
            }
            else {
                binding.settingsThemeGroup.visibility = View.VISIBLE


                val anim = AnimUtils.rotateAnim(binding.settingsThemeArrow.rotation, 180f)
                anim.fillAfter = true


                binding.settingsThemeArrow.startAnimation(anim)
            }
        }

        binding.settingsThemeGroup.setOnCheckedChangeListener { _, checkedId ->
           updateCurrentTheme(binding.settingsThemeGroup.findViewById<View>(checkedId)!!.tag.toString())

            //Set the dark changed to true.
            darkChanged = true
            val intent = Intent()
            intent.putExtra("darkChanged", darkChanged)
            setResult(0, intent)
        }

        binding.settingsBackButton.setOnClickListener {
            finish()
        }

        binding.settingsElementsHighlight.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(AppUtils.ELEMENT_HIGHLIGHT_KEY, isChecked).apply()

            if (isChecked) {
                binding.settingsElementHighlightColourLay.visibility = View.VISIBLE
            }
            else {
                binding.settingsElementHighlightColourLay.visibility = View.GONE
            }
        }

        binding.settingsElementHighlightColourLay.setOnClickListener {
            val dialog = Elements(this).buildColorPickerDialog(object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, color: Int) {
                    val colorString = AppUtils.intToHex(color)

                    prefs.edit().putString(AppUtils.ELEMENT_HIGHLIGHT_COLOUR_KEY, colorString).apply()

                    binding.settingsElementHighlightColourTxt.text = colorString
                    TextViewCompat.setCompoundDrawableTintList(binding.settingsElementHighlightColourTxt, ColorStateList.valueOf(color))

                }

                override fun onDialogDismissed(dialogId: Int) {
                }


            })
            dialog.show(supportFragmentManager, "UseCaseOne")
        }

        binding.settingsElementHighlightCorners.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                prefs.edit().putInt(AppUtils.ELEMENT_HIGHLIGHT_CORNERS_KEY, progress).apply()
                binding.settingsElementHighlightCornersTxt.text = progress.toString()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.settingsElementsMenu.setOnCheckedChangeListener { buttonView, isChecked ->
            prefs.edit().putBoolean(AppUtils.ELEMENT_MENU_KEY, isChecked).apply()
        }

        darkMode(AppUtils.isDarkMode(this))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_puller, R.anim.activity_close)
    }

    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.settingsParent.setBackgroundColor(Color.BLACK)
            binding.settingsThemeCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)

            binding.settingsHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.settingsBackButton.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)
            binding.settingsThemeHeader.setTextColor(colourUtils.darkModeDeepPurple)

            binding.settingThemeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dark_mode_35dp))
            binding.settingThemeTitle.setTextColor(colourUtils.darkModeDeepPurple)
            binding.settingsThemeTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.settingsThemeArrow.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)

            binding.settingsLightTheme.setTextColor(colourUtils.darkModeDeepPurple)
            binding.settingsDarkTheme.setTextColor(colourUtils.darkModeDeepPurple)
            binding.settingsSystemTheme.setTextColor(colourUtils.darkModeDeepPurple)


            binding.settingsElementsHeader.setTextColor(colourUtils.darkModeDeepPurple)


            binding.settingsElementsHighlighterCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)
            binding.settingsElementsHighlight.setTextColor(colourUtils.darkModeDeepPurple)
            binding.settingsElementsHighlight.thumbTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)

            binding.settingsElementHighlightColourTitle.setTextColor(colourUtils.darkModeDeepPurple)
            binding.settingsElementHighlightColourTxt.setTextColor(colourUtils.darkModeDeepPurple)

            binding.settingsElementHighlightCorners.progressTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)
            binding.settingsElementHighlightCorners.thumbTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)
            binding.settingsElementHighlightCornersTitle.setTextColor(colourUtils.darkModeDeepPurple)
            binding.settingsElementHighlightCornersTxt.setTextColor(colourUtils.darkModeDeepPurple)

            binding.settingsElementsMenuCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)
            binding.settingsElementsMenu.setTextColor(colourUtils.darkModeDeepPurple)
            binding.settingsElementsMenu.thumbTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)


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
            binding.settingsParent.setBackgroundColor(colourUtils.lightModeParentPurple)
            binding.settingsThemeCard.setCardBackgroundColor(Color.WHITE)

            binding.settingsHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.settingsBackButton.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)
            binding.settingsThemeHeader.setTextColor(colourUtils.lightModeDeepPurple)

            binding.settingThemeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.light_mode_35dp))
            binding.settingThemeTitle.setTextColor(colourUtils.lightModeDeepPurple)
            binding.settingsThemeTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.settingsThemeArrow.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)

            binding.settingsLightTheme.setTextColor(colourUtils.lightModeDeepPurple)
            binding.settingsDarkTheme.setTextColor(colourUtils.lightModeDeepPurple)
            binding.settingsSystemTheme.setTextColor(colourUtils.lightModeDeepPurple)

            binding.settingsElementsHeader.setTextColor(colourUtils.lightModeDeepPurple)

            binding.settingsElementsHighlighterCard.setCardBackgroundColor(Color.WHITE)
            binding.settingsElementsHighlight.setTextColor(colourUtils.lightModeDeepPurple)
            binding.settingsElementsHighlight.thumbTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)

            binding.settingsElementHighlightColourTitle.setTextColor(colourUtils.lightModeDeepPurple)
            binding.settingsElementHighlightColourTxt.setTextColor(colourUtils.lightModeDeepPurple)


            binding.settingsElementHighlightCorners.progressTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)
            binding.settingsElementHighlightCorners.thumbTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)
            binding.settingsElementHighlightCornersTitle.setTextColor(colourUtils.lightModeDeepPurple)
            binding.settingsElementHighlightCornersTxt.setTextColor(colourUtils.lightModeDeepPurple)


            binding.settingsElementsMenuCard.setCardBackgroundColor(Color.WHITE)
            binding.settingsElementsMenu.setTextColor(colourUtils.lightModeDeepPurple)
            binding.settingsElementsMenu.thumbTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)

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

    fun updateCurrentTheme(theme: String) {
        val prefs = getSharedPreferences(AppUtils.APP_PREFS, MODE_PRIVATE)
        prefs.edit().putString(AppUtils.APP_THEME_KEY, theme).apply()

        binding.settingsThemeTxt.text = theme

        darkMode(AppUtils.isDarkMode(this))

    }
}