package com.rb.crafty

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeechService
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.Animation
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.rb.crafty.adapters.CardsUIAdapter
import com.rb.crafty.adapters.HomeGreetrUiAdapter
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.dataObjects.FontData
import com.rb.crafty.dataObjects.CardData
import com.rb.crafty.dataObjects.GuideData
import com.rb.crafty.dataObjects.PremiumData
import com.rb.crafty.databinding.ActivityMainBinding
import com.rb.crafty.sheets.CardOptionsSheet
import com.rb.crafty.sheets.ImportCardSheet
import com.rb.crafty.sheets.ImportElementSheet
import com.rb.crafty.sheets.PremiumUserSheet
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import com.rb.crafty.utils.PremiumLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.StreamCorruptedException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), HomeGreetrUiAdapter.HomeUiAdapterListener {
    lateinit var binding: ActivityMainBinding


    lateinit var greetrList: MutableList<CardData>

    lateinit var firestore: FirebaseFirestore

    var user: FirebaseUser? = null

    lateinit var functions: FirebaseFunctions

    lateinit var firebaseAuth: FirebaseAuth

    lateinit var storageReference: StorageReference

    lateinit var signinLauncher: ActivityResultLauncher<Intent>

    lateinit var createLauncher: ActivityResultLauncher<Intent>

    val providers = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    lateinit var someIdeasList: MutableList<CardData>

    lateinit var someIdeasAdapter: CardsUIAdapter

    lateinit var homeGreetrAdapter: HomeGreetrUiAdapter

    lateinit var craftyCardsSnapshotListener: EventListener<QuerySnapshot>

    lateinit var craftyCardsCollectionRef: CollectionReference

    lateinit var listenerRegistration: ListenerRegistration

    lateinit var colourUtils: ColourUtils

    lateinit var profileLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        firestore = Firebase.firestore
        firebaseAuth = Firebase.auth
        storageReference = Firebase.storage.reference
        functions = Firebase.functions

        colourUtils = ColourUtils(this)

        var tts: TextToSpeech? = null
        tts = TextToSpeech(this) {
            val cachePath =
                "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DOWNLOADS}/ttsAudioTest.mp3"
            tts!!.synthesizeToFile("Hey this is rohith ", null, File(cachePath), "testId")
        }


        val appPrefs = getSharedPreferences("appPrefs", MODE_PRIVATE)
        var count = appPrefs.getInt("openCount", 0)
        count++
       val editor = appPrefs.edit()
        editor.putInt("openCount", count)
        editor.apply()

        if (count == 3) {
            askRating()
        }

        val signinIntent =
            AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build()

        signinLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            if (it.resultCode == RESULT_OK) {
                user = firebaseAuth.currentUser!!
                //Recreate the activity to reload everything.
                recreate()
            }
        }


        profileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == 1 && it.data != null) {
                    if (it.data!!.getBooleanExtra("darkChanged", false)) {
                        darkMode(AppUtils.isDarkMode(this))
                        binding.homeUiRecy.adapter = homeGreetrAdapter
                    }
                }

                if (Firebase.auth.currentUser == null) {
                    //Recreate the activity once the user signs out.
                    recreate()
                }

                if (user == null && Firebase.auth.currentUser != null) {
                    //This means the user has signed in.
                    recreate()
                }

                if (it.resultCode == 34) {
                    binding.mainGetPremiumImg.visibility = View.GONE
                    binding.mainBanner.visibility = View.GONE
                    binding.mainNativeTemplate.visibility = View.GONE
                }
            }


        //Check if user is signed in or not.
        if (firebaseAuth.currentUser == null) {
            signinLauncher.launch(signinIntent)
        } else {
            user = firebaseAuth.currentUser!!
            //Set profile pic
            Glide.with(this).asDrawable().load(user!!.photoUrl).circleCrop().into(binding.userPic)

            //Check if the user is a premium user.
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get()
                .addOnCompleteListener {
                    if (it.result.exists()) {
                        binding.mainGetPremiumImg.visibility = View.GONE
                    } else {
                        binding.mainGetPremiumImg.visibility = View.VISIBLE
                    }
                }
        }


        //Initialize ads if required.
        val adInitializeListener = OnInitializationCompleteListener {
            val adRequest = AdRequest.Builder().build()
            binding.mainBanner.loadAd(adRequest)

            var adLoader: AdLoader? = null
            adLoader = AdLoader.Builder(this, getString(R.string.home_cards_ui_native))
                .forNativeAd { ad: NativeAd ->
                    if (!adLoader!!.isLoading) {
                        val background = if (AppUtils.isDarkMode(this)) {
                            ColorDrawable(colourUtils.darkModeCardsColour)
                        } else {
                            ColorDrawable(Color.WHITE)
                        }
                        val styles = NativeTemplateStyle.Builder()
                            .withMainBackgroundColor(background).build()


                        binding.mainNativeTemplate.setStyles(styles)
                        binding.mainNativeTemplate.setNativeAd(ad)
                    }

                    if (isDestroyed) {
                        ad.destroy()
                        return@forNativeAd
                    }
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .build()
                )
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
        if (user != null) {
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get()
                .addOnCompleteListener {
                    if (!it.result.exists()) {
                        MobileAds.initialize(this, adInitializeListener)
                        binding.mainBanner.visibility = View.VISIBLE
                        binding.mainNativeAdCard.visibility = View.VISIBLE
                    } else {
                        binding.mainBanner.visibility = View.GONE
                        binding.mainNativeAdCard.visibility = View.GONE

                        val data = it.result.toObject<PremiumData>()!!
                        validator(data)
                    }
                }
        } else {
            MobileAds.initialize(this, adInitializeListener)
            binding.mainBanner.visibility = View.VISIBLE
            binding.mainNativeAdCard.visibility = View.GONE
        }



        greetrList = ArrayList()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.homeUiRecy.layoutManager = layoutManager

        homeGreetrAdapter = HomeGreetrUiAdapter(
            greetrList,
            this,
            this,
            object : CardOptionsSheet.CardOptionsSheetListener {
                override fun onRefreshClicked(cardData: CardData) {
                    homeGreetrAdapter.notifyItemChanged(greetrList.indexOf(cardData))
                }

                override fun onFavRemoved(cardData: CardData) {

                }


                override fun onShowMessage(text: String) {
                    AppUtils.buildSnackbar(this@MainActivity, text, binding.root).show()
                }

                override fun onViewFullClicked() {

                }
            })

        binding.homeUiRecy.adapter = homeGreetrAdapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.homeUiRecy)

        binding.homeUIDotsIndicator.attachToRecyclerView(binding.homeUiRecy)


        someIdeasList = ArrayList()
        someIdeasAdapter = CardsUIAdapter(
            this,
            someIdeasList,
            CardOptionsSheet.SOME_IDEAS,
            object : CardOptionsSheet.CardOptionsSheetListener {
                override fun onRefreshClicked(cardData: CardData) {
                    someIdeasAdapter.notifyItemChanged(someIdeasList.indexOf(cardData))

                    AppUtils.buildSnackbar(this@MainActivity, "Card Refreshed", binding.root).show()
                }

                override fun onFavRemoved(cardData: CardData) {

                }

                override fun onShowMessage(text: String) {
                    AppUtils.buildSnackbar(this@MainActivity, text, binding.root).show()
                }

                override fun onViewFullClicked() {

                }
            })

        val someIdeasLM = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.someIdeasRecy.layoutManager = someIdeasLM
        binding.someIdeasRecy.adapter = someIdeasAdapter

        val snapHelper2 = PagerSnapHelper()
        snapHelper2.attachToRecyclerView(binding.someIdeasRecy)



        if (user != null) {
            //Get the cards from Some Ideas.
            firestore.collection(AppUtils.SOME_IDEAS_COLLECTION).get().addOnSuccessListener {
                for (doc in it.documents) {
                    val data = doc.toObject<CardData>()
                    someIdeasList.add(data!!)
                    someIdeasAdapter.notifyItemInserted(someIdeasList.indexOf(data))
                }
            }

            var greetrLoopCount = 0
            //Get the user's created greetr cards and update the UI whenever user updates, adds and deletes their card.
            craftyCardsSnapshotListener = EventListener<QuerySnapshot> { value, error ->
                for (change in value!!.documentChanges) {

                    //Add the cards and update the UI.
                    if (change.type == DocumentChange.Type.ADDED) {
                        val data = change.document.toObject<CardData>()
                        greetrList.add(data)
                        homeGreetrAdapter.notifyItemInserted(greetrList.size - 1)
                        greetrLoopCount++

                        //Loop ends
                        if (greetrLoopCount == greetrList.size) {
                            handleGreetrUiVisibility()
                        }
                    }

                    //Update when there are any changes to cards.
                    if (change.type == DocumentChange.Type.MODIFIED) {
                        val data = change.document.toObject<CardData>()
                        for (cardData in greetrList) {
                            if (data.id == cardData.id) {
                                val position = greetrList.indexOf(cardData)
                                greetrList[position] = data
                                homeGreetrAdapter.notifyItemChanged(position)
                                /*   //Display the user.
                                   val snackbar = AppUtils.buildSnackbar(
                                       this@MainActivity,
                                       "Applied changes to your card successfully.",
                                       binding.root
                                   )
                                   snackbar.duration = Snackbar.LENGTH_LONG
                                   snackbar.show()*/
                            }
                        }
                    }

                    //Update when cards removed.
                    if (change.type == DocumentChange.Type.REMOVED) {
                        val data = change.document.toObject<CardData>()
                        val iterator = greetrList.iterator()
                        while (iterator.hasNext()) {
                            val cardData = iterator.next()
                            if (cardData.id == data.id) {
                                val position = greetrList.indexOf(cardData)
                                iterator.remove()
                                homeGreetrAdapter.notifyItemRemoved(position)

                                //Show the empty card if there are no cards.
                                handleGreetrUiVisibility()

                                //Display the user.
                                val snackbar = AppUtils.buildSnackbar(
                                    this@MainActivity,
                                    "Removed card from your list successfully.",
                                    binding.root
                                )
                                snackbar.duration = Snackbar.LENGTH_LONG
                                snackbar.show()
                                break
                            }
                        }
                    }
                }

            }
            craftyCardsCollectionRef =
                firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                    .collection(user!!.uid).document("doc")
                    .collection(AppUtils.CRAFTY_CARDS_COLLECTION)
            listenerRegistration =
                craftyCardsCollectionRef.addSnapshotListener(craftyCardsSnapshotListener)
        }


        //Show Card or element dialog when user opens a .cty or .ced file.
        if (intent.action == "android.intent.action.VIEW") {
            if (AppUtils.hasStoragePermission(this)) {
                val contentFileName = AppUtils.getContentFileName(this, intent.data!!)
                if (contentFileName.endsWith(".cty", false)) {
                    loadCardFromFile(intent.data!!)
                }
                if (contentFileName.endsWith(".ced", false)) {
                    loadElementFromFile(intent.data!!)
                }
            } else {
                AppUtils.buildStoragePermission(this).show()
            }
        }


        binding.createNewGreetrButton.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (user != null) {
                        val intent = Intent(this@MainActivity, CreatorActivity::class.java)
                        intent.putExtra("callType", "new")
                        intent.putExtra("creationTime", Calendar.getInstance().timeInMillis)
                        intent.putExtra("newId", kotlin.random.Random.nextInt(0, 1000000000))
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.activity_open,
                            R.anim.activity_pusher
                        )

                    } else {
                        val snackbar = AppUtils.buildSnackbar(
                            this@MainActivity,
                            "Sign in to start creating cards",
                            binding.root
                        )
                        snackbar.setAction("SIGN IN") {
                            signinLauncher.launch(signinIntent)
                        }
                        snackbar.show()
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            it.startAnimation(anim)
        }

        binding.userPic.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    profileLauncher.launch(intent)
                    overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }


        binding.mainGetPremiumImg.setOnClickListener {
            getPremium()
        }

     /*   binding.mainParent.doOnLayout {
            //Guided tour.
            val guidedList = loadGuidedTourList()
            val gson = Gson()
            val jsonList = gson.toJson(guidedList)
            val intent = Intent(this, OverLayer::class.java)
            intent.putExtra("guidedList", jsonList)
            intent.putExtra("from", "main")
            startActivity(intent)
            overridePendingTransition(R.anim.activity_stable, R.anim.activity_stable)
        }*/


        //Adjust dark mode.
        darkMode(AppUtils.isDarkMode(this))
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onFavChanged(isFavourite: Boolean, cardId: Int) {
        if (user != null) {
            firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                .collection(user!!.uid).document("doc").collection(AppUtils.CRAFTY_CARDS_COLLECTION)
                .document(cardId.toString()).update("favourite", isFavourite)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (isFavourite) {
                            AppUtils.buildSnackbar(
                                this,
                                "Added to your favourites.",
                                binding.root
                            ).show()
                        } else {
                            AppUtils.buildSnackbar(
                                this,
                                "Removed from your favourites.",
                                binding.root
                            ).show()
                        }
                    } else {
                        val snack = AppUtils.buildSnackbar(
                            this,
                            "Problem changing favourites, please try again later.",
                            binding.root
                        )
                        snack.duration = Snackbar.LENGTH_LONG
                        snack.show()
                    }
                }
        }
    }

    override fun onDownloadClicked(cardData: CardData) {
        val savingBar = AppUtils.buildSnackbar(
            this,
            "Downloading your card, please wait...",
            binding.root
        )
        savingBar.duration = Snackbar.LENGTH_INDEFINITE
        savingBar.show()

        CoroutineScope(Dispatchers.IO).launch {
            AppUtils.saveGreetrCardToDevice(
                this@MainActivity,
                AppUtils.uniqueContentNameGenerator(cardData.cardName),
                cardData,
                object : AppUtils.MediaListener {
                    override fun onMediaSaved(savedPath: String) {
                        val snack = AppUtils.buildSnackbar(
                            this@MainActivity,
                            "Card downloaded at 'Downloads/Crafty/Cards'",
                            binding.root
                        )
                        snack.duration = Snackbar.LENGTH_LONG
                        snack.setAction("Open") {
                            val intent = Intent(Intent.ACTION_GET_CONTENT)

                            intent.setDataAndType(
                                Uri.parse(
                                    (Environment.getExternalStorageDirectory().path
                                            + File.separator) + "Downloads" + File.separator + "Greetr/Cards"
                                ), "file/*"
                            )
                            startActivity(intent)
                        }
                        savingBar.dismiss()
                        snack.show()
                    }

                    override fun onMediaSaveProgress(progress: Int) {

                    }

                    override fun onMediaSaveFailed(reason: String) {
                        AppUtils.buildSnackbar(this@MainActivity, reason, binding.root)
                            .show()
                    }

                }
            )
        }
    }

    override fun onShareClicked(cardData: CardData) {
        val savingBar = AppUtils.buildSnackbar(
            this,
            "Preparing to share your card, please wait...",
            binding.root
        )
        savingBar.duration = Snackbar.LENGTH_INDEFINITE
        savingBar.show()
        CoroutineScope(Dispatchers.IO).launch {
            AppUtils.saveGreetrCardToDevice(
                this@MainActivity,
                AppUtils.uniqueContentNameGenerator(cardData.cardName),
                cardData,
                object : AppUtils.MediaListener {
                    override fun onMediaSaved(savedPath: String) {
                        savingBar.dismiss()
                        val share = Intent()
                        share.setAction(Intent.ACTION_SEND)
                        share.setType("*/*")
                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(savedPath))
                        startActivity(Intent.createChooser(share, "Share your card"))
                    }

                    override fun onMediaSaveProgress(progress: Int) {

                    }

                    override fun onMediaSaveFailed(reason: String) {
                        AppUtils.buildSnackbar(this@MainActivity, reason, binding.root)
                            .show()
                    }

                }
            )
        }

    }

    override fun onShowMessage(text: String) {
        AppUtils.buildSnackbar(this, text, binding.root).show()
    }

    fun uploadSearchableFonts() {
        val fontsList = kotlin.collections.ArrayList<FontData>()

        val eater = FontData()
        eater.id = kotlin.random.Random.nextInt(1000000000)
        eater.fontName = "Eater"
        eater.fontFamily = "Eater"
        eater.source = "Google Fonts"
        eater.style = "Regular"
        eater.weight = 400
        fontsList.add(eater)

        val electrolize = FontData()
        electrolize.id = kotlin.random.Random.nextInt(1000000000)
        electrolize.fontName = "Electrolize"
        electrolize.fontFamily = "Electrolize"
        electrolize.source = "Google Fonts"
        electrolize.style = "Regular"
        electrolize.weight = 400
        fontsList.add(electrolize)

        val ewert = FontData()
        ewert.id = kotlin.random.Random.nextInt(1000000000)
        ewert.fontName = "Ewert"
        ewert.fontFamily = "Ewert"
        ewert.source = "Google Fonts"
        ewert.style = "Regular"
        ewert.weight = 400
        fontsList.add(ewert)

        val fasterOne = FontData()
        fasterOne.id = kotlin.random.Random.nextInt(1000000000)
        fasterOne.fontName = "Faster One"
        fasterOne.fontFamily = "Faster One"
        fasterOne.source = "Google Fonts"
        fasterOne.style = "Regular"
        fasterOne.weight = 400
        fontsList.add(fasterOne)

        val fugazOne = FontData()
        fugazOne.id = kotlin.random.Random.nextInt(1000000000)
        fugazOne.fontName = "Fugaz One"
        fugazOne.fontFamily = "Fugaz One"
        fugazOne.source = "Google Fonts"
        fugazOne.style = "Regular"
        fugazOne.weight = 400
        fontsList.add(fugazOne)

        val IBMPM = FontData()
        IBMPM.id = kotlin.random.Random.nextInt(1000000000)
        IBMPM.fontName = "IBM Plex Mono Thin"
        IBMPM.fontFamily = "IBM Plex Mono"
        IBMPM.source = "Google Fonts"
        IBMPM.style = "Thin"
        IBMPM.weight = 100
        fontsList.add(IBMPM)

        val IBMPMTI = FontData()
        IBMPMTI.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMTI.fontName = "IBM Plex Mono Thin Italic"
        IBMPMTI.fontFamily = "IBM Plex Mono"
        IBMPMTI.source = "Google Fonts"
        IBMPMTI.style = "Thin"
        IBMPMTI.weight = 100
        IBMPMTI.italic = true
        fontsList.add(IBMPMTI)

        val IBMPMEL = FontData()
        IBMPMEL.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMEL.fontName = "IBM Plex Mono Extra Light"
        IBMPMEL.fontFamily = "IBM Plex Mono"
        IBMPMEL.source = "Google Fonts"
        IBMPMEL.style = "Extra Light"
        IBMPMEL.weight = 200
        fontsList.add(IBMPMEL)

        val IBMPMELI = FontData()
        IBMPMELI.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMELI.fontName = "IBM Plex Mono Extra Light Italic"
        IBMPMELI.fontFamily = "IBM Plex Mono"
        IBMPMELI.source = "Google Fonts"
        IBMPMELI.style = "Extra Light"
        IBMPMELI.weight = 200
        IBMPMELI.italic = true
        fontsList.add(IBMPMELI)

        val IBMPML = FontData()
        IBMPML.id = kotlin.random.Random.nextInt(1000000000)
        IBMPML.fontName = "IBM Plex Mono Light"
        IBMPML.fontFamily = "IBM Plex Mono"
        IBMPML.source = "Google Fonts"
        IBMPML.style = "Light"
        IBMPML.weight = 300
        fontsList.add(IBMPML)

        val IBMPMLI = FontData()
        IBMPMLI.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMLI.fontName = "IBM Plex Mono Light Italic"
        IBMPMLI.fontFamily = "IBM Plex Mono"
        IBMPMLI.source = "Google Fonts"
        IBMPMLI.style = "Light"
        IBMPMLI.weight = 300
        IBMPMLI.italic = true
        fontsList.add(IBMPMLI)

        val IBMPMR = FontData()
        IBMPMR.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMR.fontName = "IBM Plex Mono Regular"
        IBMPMR.fontFamily = "IBM Plex Mono"
        IBMPMR.source = "Google Fonts"
        IBMPMR.style = "Regular"
        IBMPMR.weight = 400
        fontsList.add(IBMPMR)

        val IBMPMRI = FontData()
        IBMPMRI.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMRI.fontName = "IBM Plex Mono Regular Italic"
        IBMPMRI.fontFamily = "IBM Plex Mono"
        IBMPMRI.source = "Google Fonts"
        IBMPMRI.style = "Regular"
        IBMPMRI.weight = 400
        IBMPMRI.italic = true
        fontsList.add(IBMPMRI)

        val IBMPMM = FontData()
        IBMPMM.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMM.fontName = "IBM Plex Mono Medium"
        IBMPMM.fontFamily = "IBM Plex Mono"
        IBMPMM.source = "Google Fonts"
        IBMPMM.style = "Medium"
        IBMPMM.weight = 500
        fontsList.add(IBMPMM)

        val IBMPMMI = FontData()
        IBMPMMI.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMMI.fontName = "IBM Plex Mono Medium Italic"
        IBMPMMI.fontFamily = "IBM Plex Mono"
        IBMPMMI.source = "Google Fonts"
        IBMPMMI.style = "Medium"
        IBMPMMI.weight = 500
        IBMPMMI.italic = true
        fontsList.add(IBMPMMI)

        val IBMPMSB = FontData()
        IBMPMSB.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMSB.fontName = "IBM Plex Mono Semi-bold"
        IBMPMSB.fontFamily = "IBM Plex Mono"
        IBMPMSB.source = "Google Fonts"
        IBMPMSB.style = "Semi-bold"
        IBMPMSB.weight = 600
        fontsList.add(IBMPMSB)

        val IBMPMSBI = FontData()
        IBMPMSBI.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMSBI.fontName = "IBM Plex Mono Semi-bold Italic"
        IBMPMSBI.fontFamily = "IBM Plex Mono"
        IBMPMSBI.source = "Google Fonts"
        IBMPMSBI.style = "Semi-bold"
        IBMPMSBI.weight = 600
        IBMPMSBI.italic = true
        fontsList.add(IBMPMSB)

        val IBMPMB = FontData()
        IBMPMB.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMB.fontName = "IBM Plex Mono Bold"
        IBMPMB.fontFamily = "IBM Plex Mono"
        IBMPMB.source = "Google Fonts"
        IBMPMB.style = "Bold"
        IBMPMB.weight = 700
        fontsList.add(IBMPMB)

        val IBMPMBI = FontData()
        IBMPMBI.id = kotlin.random.Random.nextInt(1000000000)
        IBMPMBI.fontName = "IBM Plex Mono Bold Italic"
        IBMPMBI.fontFamily = "IBM Plex Mono"
        IBMPMBI.source = "Google Fonts"
        IBMPMBI.style = "Bold"
        IBMPMBI.weight = 700
        IBMPMBI.italic = true
        fontsList.add(IBMPMBI)

        val iceberg = FontData()
        iceberg.id = kotlin.random.Random.nextInt(1000000000)
        iceberg.fontName = "Iceberg"
        iceberg.fontFamily = "Iceberg"
        iceberg.source = "Google Fonts"
        iceberg.style = "Regular"
        iceberg.weight = 400
        fontsList.add(iceberg)

        val majorMD = FontData()
        majorMD.id = kotlin.random.Random.nextInt(1000000000)
        majorMD.fontName = "Major Mono Display"
        majorMD.fontFamily = "Major Mono Display"
        majorMD.source = "Google Fonts"
        majorMD.style = "Regular"
        majorMD.weight = 400
        fontsList.add(majorMD)

        val mrsSheppards = FontData()
        mrsSheppards.id = kotlin.random.Random.nextInt(1000000000)
        mrsSheppards.fontName = "Mrs Sheppards"
        mrsSheppards.fontFamily = "Mrs Sheppards"
        mrsSheppards.source = "Google Fonts"
        mrsSheppards.style = "Regular"
        mrsSheppards.weight = 400
        fontsList.add(mrsSheppards)

        val newRocker = FontData()
        newRocker.id = kotlin.random.Random.nextInt(1000000000)
        newRocker.fontName = "New Rocker"
        newRocker.fontFamily = "New Rocker"
        newRocker.source = "Google Fonts"
        newRocker.style = "Regular"
        newRocker.weight = 400
        fontsList.add(newRocker)

        val nosifer = FontData()
        nosifer.id = kotlin.random.Random.nextInt(1000000000)
        nosifer.fontName = "Nosifer"
        nosifer.fontFamily = "Nosifer"
        nosifer.source = "Google Fonts"
        nosifer.style = "Regular"
        nosifer.weight = 400
        fontsList.add(nosifer)

        val notoSansThin = FontData()
        notoSansThin.id = kotlin.random.Random.nextInt(1000000000)
        notoSansThin.fontName = "Noto Sans Thin"
        notoSansThin.fontFamily = "Noto Sans"
        notoSansThin.source = "Google Fonts"
        notoSansThin.style = "Thin"
        notoSansThin.weight = 100
        fontsList.add(notoSansThin)

        val notoSansThinItalic = FontData()
        notoSansThinItalic.id = kotlin.random.Random.nextInt(1000000000)
        notoSansThinItalic.fontName = "Noto Sans Thin Italic"
        notoSansThinItalic.fontFamily = "Noto Sans"
        notoSansThinItalic.source = "Google Fonts"
        notoSansThinItalic.style = "Thin"
        notoSansThinItalic.weight = 100
        notoSansThinItalic.italic = true
        fontsList.add(notoSansThinItalic)

        val notoSansEL = FontData()
        notoSansEL.id = kotlin.random.Random.nextInt(1000000000)
        notoSansEL.fontName = "Noto Sans Extra Light"
        notoSansEL.fontFamily = "Noto Sans"
        notoSansEL.source = "Google Fonts"
        notoSansEL.style = "Extra Light"
        notoSansEL.weight = 200
        fontsList.add(notoSansEL)

        val notoSansELI = FontData()
        notoSansELI.id = kotlin.random.Random.nextInt(1000000000)
        notoSansELI.fontName = "Noto Sans Extra Light Italic"
        notoSansELI.fontFamily = "Noto Sans"
        notoSansELI.source = "Google Fonts"
        notoSansELI.style = "Extra Light"
        notoSansELI.weight = 200
        notoSansELI.italic = true
        fontsList.add(notoSansELI)

        val notoSansL = FontData()
        notoSansL.id = kotlin.random.Random.nextInt(1000000000)
        notoSansL.fontName = "Noto Sans Light"
        notoSansL.fontFamily = "Noto Sans"
        notoSansL.source = "Google Fonts"
        notoSansL.style = "Light"
        notoSansL.weight = 300
        fontsList.add(notoSansL)

        val notoSansLI = FontData()
        notoSansLI.id = kotlin.random.Random.nextInt(1000000000)
        notoSansLI.fontName = "Noto Sans Light Italic"
        notoSansLI.fontFamily = "Noto Sans"
        notoSansLI.source = "Google Fonts"
        notoSansLI.style = "Light"
        notoSansLI.weight = 300
        notoSansLI.italic = true
        fontsList.add(notoSansLI)

        val notoSans = FontData()
        notoSans.id = kotlin.random.Random.nextInt(1000000000)
        notoSans.fontName = "Noto Sans Regular"
        notoSans.fontFamily = "Noto Sans"
        notoSans.source = "Google Fonts"
        notoSans.style = "Regular"
        notoSans.weight = 400
        fontsList.add(notoSans)

        val notoSansItalic = FontData()
        notoSansItalic.id = kotlin.random.Random.nextInt(1000000000)
        notoSansItalic.fontName = "Noto Sans Regular Italic"
        notoSansItalic.fontFamily = "Noto Sans"
        notoSansItalic.source = "Google Fonts"
        notoSansItalic.style = "Regular"
        notoSansItalic.weight = 400
        notoSansItalic.italic = true
        fontsList.add(notoSansItalic)

        val notoSansM = FontData()
        notoSansM.id = kotlin.random.Random.nextInt(1000000000)
        notoSansM.fontName = "Noto Sans Medium"
        notoSansM.fontFamily = "Noto Sans"
        notoSansM.source = "Google Fonts"
        notoSansM.style = "Medium"
        notoSansM.weight = 500
        fontsList.add(notoSansM)

        val notoSansMI = FontData()
        notoSansMI.id = kotlin.random.Random.nextInt(1000000000)
        notoSansMI.fontName = "Noto Sans Medium Italic"
        notoSansMI.fontFamily = "Noto Sans"
        notoSansMI.source = "Google Fonts"
        notoSansMI.style = "Medium"
        notoSansMI.weight = 500
        notoSansMI.italic = true
        fontsList.add(notoSansMI)

        val notoSansSB = FontData()
        notoSansSB.id = kotlin.random.Random.nextInt(1000000000)
        notoSansSB.fontName = "Noto Sans Semi-bold"
        notoSansSB.fontFamily = "Noto Sans"
        notoSansSB.source = "Google Fonts"
        notoSansSB.style = "Semi-bold"
        notoSansSB.weight = 600
        fontsList.add(notoSansSB)

        val notoSansSBI = FontData()
        notoSansSBI.id = kotlin.random.Random.nextInt(1000000000)
        notoSansSBI.fontName = "Noto Sans Semi-bold Italic"
        notoSansSBI.fontFamily = "Noto Sans"
        notoSansSBI.source = "Google Fonts"
        notoSansSBI.style = "Semi-bold"
        notoSansSBI.weight = 600
        notoSansSBI.italic = true
        fontsList.add(notoSansSBI)

        val notoSansB = FontData()
        notoSansB.id = kotlin.random.Random.nextInt(1000000000)
        notoSansB.fontName = "Noto Sans Bold"
        notoSansB.fontFamily = "Noto Sans"
        notoSansB.source = "Google Fonts"
        notoSansB.style = "Bold"
        notoSansB.weight = 700
        fontsList.add(notoSansB)

        val notoSansBI = FontData()
        notoSansBI.id = kotlin.random.Random.nextInt(1000000000)
        notoSansBI.fontName = "Noto Sans Bold Italic"
        notoSansBI.fontFamily = "Noto Sans"
        notoSansBI.source = "Google Fonts"
        notoSansBI.style = "Bold"
        notoSansBI.weight = 700
        notoSansBI.italic = true
        fontsList.add(notoSansBI)

        val notoSansEB = FontData()
        notoSansEB.id = kotlin.random.Random.nextInt(1000000000)
        notoSansEB.fontName = "Noto Sans Extra Bold"
        notoSansEB.fontFamily = "Noto Sans"
        notoSansEB.source = "Google Fonts"
        notoSansEB.style = "Extra Bold"
        notoSansEB.weight = 800
        fontsList.add(notoSansEB)

        val notoSansEBI = FontData()
        notoSansEBI.id = kotlin.random.Random.nextInt(1000000000)
        notoSansEBI.fontName = "Noto Sans Extra Bold Italic"
        notoSansEBI.fontFamily = "Noto Sans"
        notoSansEBI.source = "Google Fonts"
        notoSansEBI.style = "Extra Bold"
        notoSansEBI.weight = 800
        notoSansEBI.italic = true
        fontsList.add(notoSansEBI)

        val notoSansBlack = FontData()
        notoSansBlack.id = kotlin.random.Random.nextInt(1000000000)
        notoSansBlack.fontName = "Noto Sans Black"
        notoSansBlack.fontFamily = "Noto Sans"
        notoSansBlack.source = "Google Fonts"
        notoSansBlack.style = "Black"
        notoSansBlack.weight = 900
        fontsList.add(notoSansBlack)

        val notoSansBlackItalic = FontData()
        notoSansBlackItalic.id = kotlin.random.Random.nextInt(1000000000)
        notoSansBlackItalic.fontName = "Noto Sans Black Italic"
        notoSansBlackItalic.fontFamily = "Noto Sans"
        notoSansBlackItalic.source = "Google Fonts"
        notoSansBlackItalic.style = "Black"
        notoSansBlackItalic.weight = 900
        notoSansBlackItalic.italic = true
        fontsList.add(notoSansBlackItalic)

        val notoSerif = FontData()
        notoSerif.id = kotlin.random.Random.nextInt(1000000000)
        notoSerif.fontName = "Noto Serif Regular"
        notoSerif.fontFamily = "Noto Serif"
        notoSerif.source = "Google Fonts"
        notoSerif.style = "Regular"
        notoSerif.weight = 400
        fontsList.add(notoSerif)

        val notoSerifItalic = FontData()
        notoSerifItalic.id = kotlin.random.Random.nextInt(1000000000)
        notoSerifItalic.fontName = "Noto Serif Regular Italic"
        notoSerifItalic.fontFamily = "Noto Serif"
        notoSerifItalic.source = "Google Fonts"
        notoSerifItalic.style = "Regular"
        notoSerifItalic.weight = 400
        notoSerifItalic.italic = true
        fontsList.add(notoSerifItalic)

        val notoSerifBold = FontData()
        notoSerifBold.id = kotlin.random.Random.nextInt(1000000000)
        notoSerifBold.fontName = "Noto Serif Bold"
        notoSerifBold.fontFamily = "Noto Serif"
        notoSerifBold.source = "Google Fonts"
        notoSerifBold.style = "Bold"
        notoSerifBold.weight = 700
        fontsList.add(notoSerifBold)

        val notoSerifBoldItalic = FontData()
        notoSerifBoldItalic.id = kotlin.random.Random.nextInt(1000000000)
        notoSerifBoldItalic.fontName = "Noto Serif Bold Italic"
        notoSerifBoldItalic.fontFamily = "Noto Serif"
        notoSerifBoldItalic.source = "Google Fonts"
        notoSerifBoldItalic.style = "Bold"
        notoSerifBoldItalic.weight = 700
        notoSerifBoldItalic.italic = true
        fontsList.add(notoSerifBoldItalic)

        val oi = FontData()
        oi.id = kotlin.random.Random.nextInt(1000000000)
        oi.fontName = "Oi"
        oi.fontFamily = "Oi"
        oi.source = "Google Fonts"
        oi.style = "Regular"
        oi.weight = 400
        fontsList.add(oi)

        val oleoSSC = FontData()
        oleoSSC.id = kotlin.random.Random.nextInt(1000000000)
        oleoSSC.fontName = "Oleo Script Swash Caps Regular"
        oleoSSC.fontFamily = "Oleo Script Swash Caps"
        oleoSSC.source = "Google Fonts"
        oleoSSC.style = "Regular"
        oleoSSC.weight = 400
        fontsList.add(oleoSSC)

        val oleoSSCB = FontData()
        oleoSSCB.id = kotlin.random.Random.nextInt(1000000000)
        oleoSSCB.fontName = "Oleo Script Swash Caps Bold"
        oleoSSCB.fontFamily = "Oleo Script Swash Caps"
        oleoSSCB.source = "Google Fonts"
        oleoSSCB.style = "Bold"
        oleoSSCB.weight = 700
        fontsList.add(oleoSSCB)

        val quintessential = FontData()
        quintessential.id = kotlin.random.Random.nextInt(1000000000)
        quintessential.fontName = "Quintessential"
        quintessential.fontFamily = "Quintessential"
        quintessential.source = "Google Fonts"
        quintessential.style = "Regular"
        quintessential.weight = 400
        fontsList.add(quintessential)

        val sarina = FontData()
        sarina.id = kotlin.random.Random.nextInt(1000000000)
        sarina.fontName = "Sarina"
        sarina.fontFamily = "Sarina"
        sarina.source = "Google Fonts"
        sarina.style = "Regular"
        sarina.weight = 400
        fontsList.add(sarina)

        val seaweedScript = FontData()
        seaweedScript.id = kotlin.random.Random.nextInt(1000000000)
        seaweedScript.fontName = "Seaweed Script"
        seaweedScript.fontFamily = "Seaweed Script"
        seaweedScript.source = "Google Fonts"
        seaweedScript.style = "Regular"
        seaweedScript.weight = 400
        fontsList.add(seaweedScript)

        val sedgwickAveDisplay = FontData()
        sedgwickAveDisplay.id = kotlin.random.Random.nextInt(1000000000)
        sedgwickAveDisplay.fontName = "Sedgwick Ave Display"
        sedgwickAveDisplay.fontFamily = "Sedgwick Ave Display"
        sedgwickAveDisplay.source = "Google Fonts"
        sedgwickAveDisplay.style = "Regular"
        sedgwickAveDisplay.weight = 400
        fontsList.add(sedgwickAveDisplay)

        val shojumaru = FontData()
        shojumaru.id = kotlin.random.Random.nextInt(1000000000)
        shojumaru.fontName = "Shojumaru"
        shojumaru.fontFamily = "Shojumaru"
        shojumaru.source = "Google Fonts"
        shojumaru.style = "Regular"
        shojumaru.weight = 400
        fontsList.add(shojumaru)

        val sonsieOne = FontData()
        sonsieOne.id = kotlin.random.Random.nextInt(1000000000)
        sonsieOne.fontName = "Sonsie One"
        sonsieOne.fontFamily = "Sonsie One"
        sonsieOne.source = "Google Fonts"
        sonsieOne.style = "Regular"
        sonsieOne.weight = 400
        fontsList.add(sonsieOne)

        val zhiMangXing = FontData()
        zhiMangXing.id = kotlin.random.Random.nextInt(1000000000)
        zhiMangXing.fontName = "Zhi Mang Xing"
        zhiMangXing.fontFamily = "Zhi Mang Xing"
        zhiMangXing.source = "Google Fonts"
        zhiMangXing.style = "Regular"
        zhiMangXing.weight = 400
        fontsList.add(zhiMangXing)

        for (data in fontsList) {
            firestore.collection(AppUtils.ALL_FONT_ASSETS_COLLECTION).document(data.fontName)
                .set(data)
        }

    }

    fun handleGreetrUiVisibility() {
        if (greetrList.isEmpty()) {
            binding.greetrHomeUiEmptyCard.visibility = View.VISIBLE
        } else {
            binding.greetrHomeUiEmptyCard.visibility = View.GONE

        }
    }


    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.mainParent.setBackgroundColor(Color.BLACK)
            binding.appHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.yourCardSh.setTextColor(colourUtils.darkModeDeepPurple)
            binding.someIdeasHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.createNewGreetrButton.setCardBackgroundColor(colourUtils.darkModeDeepPurple)
            binding.mainNativeAdCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)


            binding.mainGetPremiumImg.imageTintList =
                ColorStateList.valueOf(colourUtils.darkModeDeepPurple)

            binding.homeUIDotsIndicator.selectedDotColor = colourUtils.darkModeDeepPurple

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                var flags: Int =
                    window.decorView.getSystemUiVisibility()

                flags =
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

                window.decorView.setSystemUiVisibility(flags)

            }
            window.statusBarColor = Color.BLACK

        } else {
            binding.mainParent.setBackgroundColor(colourUtils.lightModeParentPurple)
            binding.appHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.yourCardSh.setTextColor(colourUtils.lightModeDeepPurple)
            binding.someIdeasHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.createNewGreetrButton.setCardBackgroundColor(colourUtils.lightModeDeepPurple)
            binding.mainNativeAdCard.setCardBackgroundColor(Color.WHITE)



            binding.homeUIDotsIndicator.selectedDotColor = colourUtils.lightModeDeepPurple

            binding.mainGetPremiumImg.imageTintList =
                ColorStateList.valueOf(colourUtils.lightModeDeepPurple)



            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                window.insetsController!!.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                var flags: Int = binding.root.getSystemUiVisibility()
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.setSystemUiVisibility(flags)
            }

            window.statusBarColor = colourUtils.lightModeParentPurple

        }
    }

    fun getPremium() {
        val premiumSheet = PremiumUserSheet(
            PremiumUserSheet.TYPE_GET,
            true,
            object : PremiumUserSheet.PremiumSheetListener {
                override fun getPremium() {
                    PremiumLauncher(
                        this@MainActivity,
                        object : PremiumLauncher.PremiumLauncherListener {
                            override fun alreadyPremium() {
                                AppUtils.buildSnackbar(
                                    this@MainActivity,
                                    "You are a premium user, refresh the app to see changes...",
                                    binding.root
                                ).show()
                            }

                            override fun onPurchaseSuccess() {
                                val newUserSheet =
                                    PremiumUserSheet(PremiumUserSheet.TYPE_GOT, true, null)
                                newUserSheet.show(supportFragmentManager, "UseCaseOne")

                                binding.mainGetPremiumImg.visibility = View.GONE
                                binding.mainBanner.visibility = View.GONE
                                binding.mainNativeTemplate.visibility = View.GONE
                            }

                            override fun onError(reason: String) {
                                AppUtils.buildSnackbar(this@MainActivity, reason, binding.root)
                                    .show()
                            }

                        }).startConnection()
                }

            })
        premiumSheet.show(supportFragmentManager, "UseCaseThree")
    }

    fun loadCardFromFile(uri: Uri) {
        try {
            val objectInputStream = ObjectInputStream(contentResolver.openInputStream(uri))
            val cardData = objectInputStream.readObject() as CardData

            val sheet = ImportCardSheet(cardData, object : ImportCardSheet.ImportSheetListener {
                override fun onCardImported() {
                    AppUtils.buildSnackbar(
                        this@MainActivity,
                        "Added the card to your list successfully!",
                        binding.root
                    ).show()
                }

                override fun onCardImportFailed() {
                    AppUtils.buildSnackbar(
                        this@MainActivity,
                        "Unable to add the card to your list, try again later...",
                        binding.root
                    ).show()
                }
            })

            sheet.show(supportFragmentManager, "UseCaseTwo")
        } catch (e: StreamCorruptedException) {
            AppUtils.buildSnackbar(this, "File is corrupted...", binding.root).show()
            e.printStackTrace()
        } catch (e: IOException) {
            AppUtils.buildSnackbar(
                this,
                "Unable to load the file, try again later...",
                binding.root
            ).show()
            e.printStackTrace()
        }
    }

    fun loadElementFromFile(uri: Uri) {
        try {
            val objectInputStream = ObjectInputStream(contentResolver.openInputStream(uri))
            val elementData = objectInputStream.readObject() as ElementData

            val sheet = ImportElementSheet(
                elementData,
                object : ImportElementSheet.ImportElementSheetListener {
                    override fun onElementImported(elementData: ElementData) {
                        AppUtils.buildSnackbar(
                            this@MainActivity,
                            "Added the element to your list successfully",
                            binding.root
                        ).show()
                    }

                    override fun onElementImportFailed(text: String) {
                        AppUtils.buildSnackbar(
                            this@MainActivity,
                            "Unable to add the element to your list, try again later...",
                            binding.root
                        ).show()
                    }
                })

            sheet.show(supportFragmentManager, "UseCaseTwo")
        } catch (e: StreamCorruptedException) {
            AppUtils.buildSnackbar(this, "File is corrupted...", binding.root).show()
            e.printStackTrace()
        } catch (e: IOException) {
            AppUtils.buildSnackbar(
                this,
                "Unable to load the file, try again later...",
                binding.root
            ).show()
            e.printStackTrace()
        }
    }

    fun validator(premiumData: PremiumData) {
        val data = hashMapOf(
            "token" to premiumData.purchaseToken,
        )
        functions
            .getHttpsCallable("premiumValidator")
            .call(data).addOnSuccessListener {
                val result = it.data as HashMap<*, *>
                val status = result["status"] as Int
                if (status == 200) {
                    val purchaseData = result["data"] as HashMap<*, *>
                    val purchaseState = purchaseData["purchaseState"] as Int

                    //Check if the purchase is valid and update the data.
                    if (purchaseState == AppUtils.PURCHASE_STATE_CANCELLED) {
                        //The user has cancelled the purchase. Revoke the premium for this user.
                        firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid)
                            .get().addOnCompleteListener {
                            if (it.result.exists()) {
                                val data = it.result.toObject<PremiumData>()!!
                                data.isValid = false

                                //Store this data in cancelled premium for future use cases.
                                firestore.collection(AppUtils.CANCELLED_PREMIUM_USERS_COLLECTION)
                                    .document(user!!.uid).set(data).addOnSuccessListener {
                                    firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION)
                                        .document(user!!.uid).delete()
                                }
                            }
                        }
                    }
                }
            }
    }

    fun askRating() {
        val theme = if (AppUtils.isDarkMode(this)) {
            R.style.DarkCustomDialogTheme
        } else {
            R.style.CustomDialogTheme
        }
        val builder = AlertDialog.Builder(this, theme)
        builder.setTitle("Like the app?")
        builder.setMessage("Please give us a review if you enjoy using the app")
        builder.setPositiveButton("sure", DialogInterface.OnClickListener { dialogInterface, i ->
            val appurl = Uri.parse("https://play.google.com/store/apps/details?id=com.rb.crafty")
            val intent = Intent(Intent.ACTION_VIEW, appurl)
            startActivity(intent)
        })
        builder.setNegativeButton("nope", DialogInterface.OnClickListener { dialogInterface, i ->

        })
        builder.show()
    }

    fun loadGuidedTourList(): MutableList<GuideData> {
        val guideList = ArrayList<GuideData>()
        val step1 = GuideData(
            "Welcome to Crafty, Would you like to go through a quick guide tour to get yourself familiar?",
            binding.mainParent.width.toFloat() / 2,
            binding.mainParent.height.toFloat() / 2
        )
        guideList.add(step1)


        val step2 = GuideData(
            "Let's start by creating a new card, click the 'CREATE NEW' button",
            binding.createNewGreetrButton.x,
            binding.createNewGreetrButton.y
        )
        guideList.add(step2)
        return guideList

    }
}