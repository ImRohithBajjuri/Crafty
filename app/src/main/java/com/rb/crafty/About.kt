package com.rb.crafty

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.Animation
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rb.crafty.databinding.ActivityAboutBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils

class About : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding
    lateinit var colourUtils: ColourUtils

    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        colourUtils = ColourUtils(this)

        user = Firebase.auth.currentUser

        darkMode(AppUtils.isDarkMode(this))

        binding.aboutVersion.text = getString(R.string.app_version)

        binding.aboutBackButton.setOnClickListener {
            finish()
        }

        binding.aboutOssLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    startActivity(Intent(this@About, OssLicensesMenuActivity::class.java))
                    overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        val appurl = Uri.parse("https://play.google.com/store/apps/details?id=com.rb.crafty")

        binding.aboutRateLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(Intent.ACTION_VIEW, appurl)
                    startActivity(intent)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.aboutShareLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.type = "text/html"
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Creating amazing cards using Crafty on Play Store $appurl"
                    )

                    startActivity(Intent.createChooser(intent, "Share with :"))
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.aboutTosLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data =
                        Uri.parse("https://docs.google.com/document/d/e/2PACX-1vSVHiOcq4Ed1rTJLZFJOPws4_-ow1eezGk4YeUYE4kaWSGIsSDgHn8uU611NgKhMzl2sRlpNCM_wYUX/pub")
                    startActivity(intent)
                    overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.aboutPpLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data =
                        Uri.parse("https://docs.google.com/document/d/e/2PACX-1vRHs0Guz10by8RksM9m0dlHxRssy-J8Xf9yjtamB8fyJoggugNJFVYJpGqgbsu0FChTWraRY7el_Fx1/pub")
                    startActivity(intent)
                    overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.aboutFeedbackLay.setOnClickListener {

            val theme = if (AppUtils.isDarkMode(this)) {
                R.style.DarkCustomDialogTheme
            }
            else {
                R.style.CustomDialogTheme
            }
            val builder = AlertDialog.Builder(this, theme)
            builder.setTitle("Your feedback is valuable to us")
            builder.setMessage("Tell us what you think about the app and how we can improve it. We'll do our best with your help")
            builder.setNegativeButton(
                "Nope"
            ) { dialog, which ->
                //Do nothing.
            }
            builder.setPositiveButton(
                "Of course"
            ) { dialog, which ->
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.type = "plain/text"
                intent.data = Uri.fromParts("mailto", "rebootingbrains@gmail.com", null)
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("rebootingbrains@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
                intent.putExtra(Intent.EXTRA_TEXT, "Body")
                startActivity(Intent.createChooser(intent, "Please select a mail app,"))

            }
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    builder.show()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.aboutReportCard.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (user != null) {
                      val intent = ShareCompat.IntentBuilder(this@About)
                            .setType("plain/text")
                            .addEmailTo("rebootingbrains@gmail.com")
                            .setSubject("Bug Report ${user!!.uid}")
                          .setText("Describe the bug")
                            .createChooserIntent()

                         startActivity(Intent.createChooser(intent, "Please select a mail app,"))

                    }
                    else {
                        AppUtils.buildSnackbar(this@About, "Sign in to report a bug", binding.root).show()
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_puller, R.anim.activity_close)
    }

    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.aboutParent.backgroundTintList = ColorStateList.valueOf(Color.BLACK)

            binding.aboutHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.aboutBackButton.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)

            binding.aboutInfoHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.aboutReviewHeader.setTextColor(colourUtils.darkModeDeepPurple)

            binding.aboutReportCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)
            binding.aboutInfoCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)
            binding.aboutReviewCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)

            binding.aboutPpTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.aboutFeedbackTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.aboutRateTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.aboutTosTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.aboutShareTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.aboutOssTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.aboutReportTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.aboutReportTxt.setTextColor(colourUtils.darkModeDeepPurple)


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
            binding.aboutParent.backgroundTintList = ColorStateList.valueOf(colourUtils.lightModeParentPurple)

            binding.aboutHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.aboutBackButton.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)

            binding.aboutInfoHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.aboutReviewHeader.setTextColor(colourUtils.lightModeDeepPurple)


            binding.aboutReportCard.setCardBackgroundColor(Color.WHITE)
            binding.aboutInfoCard.setCardBackgroundColor(Color.WHITE)
            binding.aboutReviewCard.setCardBackgroundColor(Color.WHITE)

            binding.aboutPpTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.aboutFeedbackTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.aboutRateTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.aboutTosTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.aboutShareTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.aboutOssTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.aboutReportTxt.setTextColor(colourUtils.lightModeDeepPurple)


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