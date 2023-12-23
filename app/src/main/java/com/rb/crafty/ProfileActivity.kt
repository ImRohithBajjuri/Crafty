package com.rb.crafty

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.Animation
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.dataObjects.CardData
import com.rb.crafty.databinding.ActivityProfileBinding
import com.rb.crafty.sheets.HelpSheet
import com.rb.crafty.sheets.HiddenCardsAccessInfoSheet
import com.rb.crafty.sheets.ImportCardSheet
import com.rb.crafty.sheets.PremiumUserSheet
import com.rb.crafty.sheets.UserElementsSheet
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import com.rb.crafty.utils.PremiumLauncher
import kotlinx.coroutines.*
import java.io.ObjectInputStream


class ProfileActivity: AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding

    lateinit var firestore: FirebaseFirestore
    var user: FirebaseUser? = null

    lateinit var signinLauncher: ActivityResultLauncher<Intent>

    lateinit var importLauncher: ActivityResultLauncher<Intent>

    var darkChanged = false

    val providers = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    lateinit var colourUtils: ColourUtils

    lateinit var settingsLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        firestore = Firebase.firestore


        user = Firebase.auth.currentUser

        //Initialize ads if required.
        if (user != null) {
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {
                if (!it.result.exists()) {
                    binding.profileBanner.visibility = View.VISIBLE

                    val adRequest = AdRequest.Builder().build()
                    binding.profileBanner.loadAd(adRequest)

                    var adLoader: AdLoader? = null
                       adLoader = AdLoader.Builder(this, getString(R.string.profile_native))
                        .forNativeAd { ad : NativeAd ->
                            if (!adLoader!!.isLoading) {
                                val background = if (AppUtils.isDarkMode(this)) {
                                    ColorDrawable(colourUtils.darkModeCardsColour)
                                }
                                else {
                                    ColorDrawable(Color.WHITE)
                                }
                                val styles = NativeTemplateStyle.Builder()
                                    .withMainBackgroundColor(background).build()


                                binding.profileNativeTemplate.setStyles(styles)
                                binding.profileNativeTemplate.setNativeAd(ad)
                            }

                            if (isDestroyed) {
                                ad.destroy()
                                return@forNativeAd
                            }
                        }
                        .withAdListener(object : AdListener() {
                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                binding.profileNativeAdCard.visibility = View.GONE
                            }
                        })
                        .withNativeAdOptions(
                            NativeAdOptions.Builder()
                            .build())
                        .build()

                    adLoader.loadAd(AdRequest.Builder().build())

                    binding.profileNativeAdCard.visibility = View.VISIBLE
                }
                else {
                    binding.profileBanner.visibility = View.GONE
                    binding.profileNativeAdCard.visibility = View.GONE

                }
            }
        }
        else {
            binding.profileBanner.visibility = View.VISIBLE
            binding.profileNativeAdCard.visibility =View.GONE
        }


        colourUtils = ColourUtils(this)

        //Adjust the layout visibility.
        handleLayoutVisibility()



        //Set the user info.
        if (user != null) {
            Glide.with(this).load(user!!.photoUrl).centerCrop().circleCrop().into(binding.profileInfoImg)
            binding.profileInfoName.text = user!!.displayName
            binding.profileInfoContact.text = user!!.email

            //Check if the user is a premium user.
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {
                if (it.result.exists()) {
                    binding.premiumUserImg.visibility = View.VISIBLE
                }
                else {
                    binding.premiumUserImg.visibility = View.GONE
                }
            }
        }


        val signInIntent =
            AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).setAvailableProviders(providers)
                .build()

        signinLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            if (it.resultCode == RESULT_OK) {
                //Recreate the activity to reload everything.
                recreate()

                CoroutineScope(Dispatchers.Main).launch {
                    delay(100)
                    AppUtils.buildSnackbar(this@ProfileActivity, "Signed in successfully!", binding.root).show()
                }
            }
        }


        importLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val contentFileName = AppUtils.getContentFileName(this, it.data!!.data!!)
                if (contentFileName.endsWith(".cty", false)) {
                    importCard(it.data!!.data!!)
                } else {
                    AppUtils.buildSnackbar(this, "Please select a '.cty' file", binding.root).show()
                }

            }
        }

        settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == 0 && it.data != null) {
                val changed = it.data!!.getBooleanExtra("darkChanged", false)
                darkChanged = changed
                if (changed) {
                    darkMode(AppUtils.isDarkMode(this))

                    val intent = Intent()
                    intent.putExtra("darkChanged", darkChanged)
                    setResult(1, intent)

                }
            }
        }

        binding.profileInfoName.movementMethod = ScrollingMovementMethod()
        binding.profileInfoName.setHorizontallyScrolling(true)
        binding.profileInfoName.isSelected = true

        binding.profileInfoContact.movementMethod = ScrollingMovementMethod()
        binding.profileInfoContact.setHorizontallyScrolling(true)
        binding.profileInfoContact.isSelected = true



        binding.profileBackButton.setOnClickListener {
            finish()
        }

        binding.profileSignOut.setOnClickListener {
            //Sign the user out.
            signOut()
        }

        binding.profileSignOutText.setOnClickListener {
            //Sign the user out.
            signOut()
        }

        binding.profileSignOutIcon.setOnClickListener {
            //Sign the user out.
            signOut()

        }

        binding.profileSignInButton.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    signinLauncher.launch(signInIntent)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.profileFavLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(this@ProfileActivity, FavouriteCards::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.profileHiddenLay.setOnClickListener {
            val firstTime = getSharedPreferences("appPrefs", MODE_PRIVATE).getBoolean("hidSheetFirstTime", true)
            if (firstTime) {
                val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        if (user != null) {
                            val sheet = HiddenCardsAccessInfoSheet()
                            sheet.show(supportFragmentManager, "UseCaseOne")
                            getSharedPreferences("appPrefs", MODE_PRIVATE).edit().putBoolean("hidSheetFirstTime", false).apply()                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
                it.startAnimation(anim)

            }
            else {
                val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                promptInfo.setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL)
                promptInfo.setTitle("Authenticate to proceed")
                promptInfo.setDescription("Use your credentials to view your hidden cards")

                val biometricPrompt = androidx.biometric.BiometricPrompt(this,object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        AppUtils.buildSnackbar(this@ProfileActivity, "Failed to authenticate, try again...", binding.root).show()
                    }

                    override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)

                        //Proceed with opening the hidden cards.
                        val intent = Intent(this@ProfileActivity, HiddenCards::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        AppUtils.buildSnackbar(this@ProfileActivity, "Failed to authenticate, try again...", binding.root).show()
                    }
                })

                val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        if (user != null) {
                            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener { it2 ->
                                if (it2.result.exists()) {
                                    biometricPrompt.authenticate(promptInfo.build())
                                }
                                else {
                                    AppUtils.buildSnackbar(this@ProfileActivity, "This feature is only for premium users", binding.root).show()
                                }
                            }
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
                it.startAnimation(anim)
            }
        }

        binding.profileAddedFontsLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(this@ProfileActivity, AddedFontsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.profileImportCard.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.setDataAndType(MediaStore.Files.getContentUri("external"), "application/*")
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.flags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    if (AppUtils.hasStoragePermission(this@ProfileActivity)) {
                        if (user != null) {
                            importLauncher.launch(intent)
                        }
                    } else {
                        AppUtils.buildStoragePermission(this@ProfileActivity).show()
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.profileAppSettingsLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(this@ProfileActivity, AppSettings::class.java)
                    settingsLauncher.launch(intent)
                    overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.profileGetPremiumButton.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    //Check if the user is an already premium user.
                    val premiumUserSheet = PremiumUserSheet(PremiumUserSheet.TYPE_GET, true, object : PremiumUserSheet.PremiumSheetListener {
                        override fun getPremium() {
                            PremiumLauncher(this@ProfileActivity, object : PremiumLauncher.PremiumLauncherListener {
                                override fun alreadyPremium() {
                                    AppUtils.buildSnackbar(this@ProfileActivity, "You are a premium user, refresh the app to see changes...", binding.root).show()
                                }

                                override fun onPurchaseSuccess() {
                                    handleLayoutVisibility()
                                    val newUserSheet = PremiumUserSheet(PremiumUserSheet.TYPE_GOT, true, null)
                                    newUserSheet.show(supportFragmentManager, "UseCaseOne")

                                    setResult(34)
                                }

                                override fun onError(reason: String) {
                                    AppUtils.buildSnackbar(this@ProfileActivity, reason, binding.root).show()
                                }

                            }).startConnection()
                        }

                    })
                    premiumUserSheet.show(supportFragmentManager, "UseCaseThree")
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })

            it.startAnimation(anim)
        }

        binding.premiumUserImg.setOnClickListener {
            val premiumSheet = PremiumUserSheet(PremiumUserSheet.TYPE_GOT, false, null)
            premiumSheet.show(supportFragmentManager, "UseCaseTwo")
        }

        binding.profileAboutAppLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(this@ProfileActivity, About::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.profileUserElementsLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val sheet = UserElementsSheet(UserElementsSheet.FROM_PROFILE, object : UserElementsSheet.UserElementsSheetListener {
                        override fun onElementClicked(elementData: ElementData) {

                        }

                    })
                    sheet.show(supportFragmentManager, "UseCaseOne")
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding.profileHelpLay.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val helpSheet = HelpSheet()
                    helpSheet.show(supportFragmentManager, "profileActivity")
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }


        darkMode(AppUtils.isDarkMode(this))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_puller, R.anim.activity_close)
    }

    fun handleLayoutVisibility() {
        //Handle the visibility of layout based on if the user is logged in or not.
        if (user == null) {
            binding.profileInfoCard.visibility = View.GONE
            binding.profileImportCard.visibility = View.GONE
            binding.profileUserLibraryCard.visibility = View.GONE
            binding.profileAppRelatedCard.visibility =  View.GONE

            binding.profileSignedOutCard.visibility = View.VISIBLE
            binding.profileSignInButton.visibility = View.VISIBLE

            binding.profileGetPremiumHeader.visibility = View.GONE
            binding.profileGetPremiumCard.visibility = View.GONE
            binding.profileGetPremiumButton.visibility = View.GONE

            binding.profileLibraryHeader.visibility = View.GONE
            binding.profileAppRelatedHeader.visibility = View.GONE

        }
        else {
            binding.profileInfoCard.visibility = View.VISIBLE
            binding.profileImportCard.visibility = View.VISIBLE
            binding.profileUserLibraryCard.visibility = View.VISIBLE
            binding.profileAppRelatedCard.visibility =  View.VISIBLE

            binding.profileSignedOutCard.visibility = View.GONE
            binding.profileSignInButton.visibility = View.GONE

            binding.profileLibraryHeader.visibility = View.VISIBLE
            binding.profileAppRelatedHeader.visibility = View.VISIBLE

            //Verify premium.
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {
                if (it.result.exists()) {
                    binding.profileGetPremiumHeader.visibility = View.GONE
                    binding.profileGetPremiumCard.visibility = View.GONE
                    binding.profileGetPremiumButton.visibility = View.GONE

                    binding.profileBanner.visibility = View.GONE
                    binding.profileNativeAdCard.visibility = View.GONE
                }
                else {
                    binding.profileGetPremiumHeader.visibility = View.VISIBLE
                    binding.profileGetPremiumCard.visibility = View.VISIBLE
                    binding.profileGetPremiumButton.visibility = View.VISIBLE

                    binding.profileBanner.visibility = View.VISIBLE
                    binding.profileNativeAdCard.visibility = View.VISIBLE
                }
            }

        }
    }

    fun signOut() {
        Firebase.auth.signOut()
        user = null

        handleLayoutVisibility()
        setResult(-1)
        AppUtils.buildSnackbar(this, "Signed out successfully", binding.root).show()
    }

    fun importCard(filePath: Uri) {
        try {
            //Get the data from the file.
            val ois = ObjectInputStream(contentResolver.openInputStream(filePath))
            val cardData = ois.readObject() as CardData

            val importCardSheet = ImportCardSheet(cardData, object : ImportCardSheet.ImportSheetListener {
                override fun onCardImported() {
                    AppUtils.buildSnackbar(this@ProfileActivity, "Added the card successfully to your list", binding.root).show()
                }

                override fun onCardImportFailed() {
                    AppUtils.buildSnackbar(this@ProfileActivity, "Unable to add the card to your list, try again later...", binding.root).show()
                }

            })
            importCardSheet.show(supportFragmentManager, "UseCaseOne")
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.profileParent.setBackgroundColor(Color.BLACK)

            binding.profileHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileBackButton.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)

            binding.profileUserLibraryCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)
            binding.profileAppRelatedCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)
            binding.profileImportCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)
            binding.profileNativeAdCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)

            binding.profileFavCardTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileHiddenCardTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileAddedFontsCardTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileAppSettingsCardTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileAboutAppCardTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileHelpCardTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileImportCardTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileUserElementsTxt.setTextColor(colourUtils.darkModeDeepPurple)




            binding.profileInfoHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileLibraryHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileAppRelatedHeader.setTextColor(colourUtils.darkModeDeepPurple)


            binding.profileSignInButton.setCardBackgroundColor(colourUtils.darkModeDeepPurple)


            binding.profileGetPremiumHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.profileGetPremiumButton.setCardBackgroundColor(colourUtils.darkModeDeepPurple)

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
            binding.profileParent.setBackgroundColor(colourUtils.lightModeParentPurple)

            binding.profileHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileBackButton.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)


            binding.profileAppRelatedCard.setCardBackgroundColor(Color.WHITE)
            binding.profileUserLibraryCard.setCardBackgroundColor(Color.WHITE)
            binding.profileImportCard.setCardBackgroundColor(Color.WHITE)
            binding.profileNativeAdCard.setCardBackgroundColor(Color.WHITE)


            binding.profileFavCardTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileHiddenCardTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileAddedFontsCardTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileAppSettingsCardTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileAboutAppCardTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileHelpCardTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileImportCardTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileUserElementsTxt.setTextColor(colourUtils.lightModeDeepPurple)


            binding.profileInfoHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileLibraryHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileAppRelatedHeader.setTextColor(colourUtils.lightModeDeepPurple)


            binding.profileSignInButton.setCardBackgroundColor(colourUtils.lightModeDeepPurple)


            binding.profileGetPremiumHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.profileGetPremiumButton.setCardBackgroundColor(colourUtils.lightModeDeepPurple)


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