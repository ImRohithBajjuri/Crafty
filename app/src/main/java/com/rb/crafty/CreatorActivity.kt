package com.rb.crafty

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.*
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaMuxer.OutputFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.provider.FontsContractCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.core.view.setPadding
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerControlView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.makeramen.roundedimageview.RoundedImageView
import com.rb.crafty.adapters.AddedElementsAdapter
import com.rb.crafty.adapters.ElementOptionsAdapter
import com.rb.crafty.audioControls.AudioInputControlsFragment
import com.rb.crafty.blockControls.BlockColorControlsFragment
import com.rb.crafty.dataObjects.*
import com.rb.crafty.databinding.*
import com.rb.crafty.elementControls.*
import com.rb.crafty.gradientControls.GradientControlsFragment
import com.rb.crafty.imageControls.*
import com.rb.crafty.mainControls.*
import com.rb.crafty.patternControls.PatternDesignsFragment
import com.rb.crafty.sheets.ElementInfoSheet
import com.rb.crafty.sheets.ElementsSheet
import com.rb.crafty.sheets.SavingSheet
import com.rb.crafty.textControls.*
import com.rb.crafty.utils.*
import com.rb.crafty.utils.Elements.Companion.AUDIO_BACKGROUND
import com.rb.crafty.utils.Elements.Companion.AUDIO_CORNERS
import com.rb.crafty.utils.Elements.Companion.AUDIO_INPUT
import com.rb.crafty.utils.Elements.Companion.AUDIO_ROTATION
import com.rb.crafty.utils.Elements.Companion.AUDIO_SHADOW
import com.rb.crafty.utils.Elements.Companion.AUDIO_SIZE
import com.rb.crafty.utils.Elements.Companion.AUDIO_STROKE
import com.rb.crafty.utils.Elements.Companion.AUDIO_TYPE
import com.rb.crafty.utils.Elements.Companion.AUDIO_ZOOM
import com.rb.crafty.utils.Elements.Companion.BLOCK_BACKGROUND
import com.rb.crafty.utils.Elements.Companion.BLOCK_COLOR
import com.rb.crafty.utils.Elements.Companion.BLOCK_CORNERS
import com.rb.crafty.utils.Elements.Companion.BLOCK_ROTATION
import com.rb.crafty.utils.Elements.Companion.BLOCK_SHADOW
import com.rb.crafty.utils.Elements.Companion.BLOCK_SIZE
import com.rb.crafty.utils.Elements.Companion.BLOCK_STROKE
import com.rb.crafty.utils.Elements.Companion.BLOCK_TYPE
import com.rb.crafty.utils.Elements.Companion.BLOCK_ZOOM
import com.rb.crafty.utils.Elements.Companion.GRADIENT_BACKGROUND
import com.rb.crafty.utils.Elements.Companion.GRADIENT_COLORS
import com.rb.crafty.utils.Elements.Companion.GRADIENT_CORNERS
import com.rb.crafty.utils.Elements.Companion.GRADIENT_ROTATION
import com.rb.crafty.utils.Elements.Companion.GRADIENT_SHADOW
import com.rb.crafty.utils.Elements.Companion.GRADIENT_SIZE
import com.rb.crafty.utils.Elements.Companion.GRADIENT_STROKE
import com.rb.crafty.utils.Elements.Companion.GRADIENT_TYPE
import com.rb.crafty.utils.Elements.Companion.GRADIENT_ZOOM
import com.rb.crafty.utils.Elements.Companion.IMAGE_BACKGROUND
import com.rb.crafty.utils.Elements.Companion.IMAGE_COMPRESSION
import com.rb.crafty.utils.Elements.Companion.IMAGE_CORNERS
import com.rb.crafty.utils.Elements.Companion.IMAGE_CROP
import com.rb.crafty.utils.Elements.Companion.IMAGE_FILTERS
import com.rb.crafty.utils.Elements.Companion.IMAGE_INPUT
import com.rb.crafty.utils.Elements.Companion.IMAGE_ROTATION
import com.rb.crafty.utils.Elements.Companion.IMAGE_SCALE_TYPE
import com.rb.crafty.utils.Elements.Companion.IMAGE_SIZE
import com.rb.crafty.utils.Elements.Companion.IMAGE_TYPE
import com.rb.crafty.utils.Elements.Companion.IMAGE_ZOOM
import com.rb.crafty.utils.Elements.Companion.PATTERN_BACKGROUND
import com.rb.crafty.utils.Elements.Companion.PATTERN_CORNERS
import com.rb.crafty.utils.Elements.Companion.PATTERN_DESIGN
import com.rb.crafty.utils.Elements.Companion.PATTERN_ROTATION
import com.rb.crafty.utils.Elements.Companion.PATTERN_SHADOW
import com.rb.crafty.utils.Elements.Companion.PATTERN_SIZE
import com.rb.crafty.utils.Elements.Companion.PATTERN_STROKE
import com.rb.crafty.utils.Elements.Companion.PATTERN_TYPE
import com.rb.crafty.utils.Elements.Companion.PATTERN_ZOOM
import com.rb.crafty.utils.Elements.Companion.TEXT_BACKGROUND
import com.rb.crafty.utils.Elements.Companion.TEXT_COLOR
import com.rb.crafty.utils.Elements.Companion.TEXT_DEPTH
import com.rb.crafty.utils.Elements.Companion.TEXT_FONT
import com.rb.crafty.utils.Elements.Companion.TEXT_INPUT
import com.rb.crafty.utils.Elements.Companion.TEXT_ROTATE
import com.rb.crafty.utils.Elements.Companion.TEXT_SIZE
import com.rb.crafty.utils.Elements.Companion.TEXT_TYPE
import com.rb.crafty.utils.Elements.Companion.TEXT_ZOOM
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.abs
import kotlin.random.Random


class CreatorActivity : AppCompatActivity(),
    ElementOptionsAdapter.ElementOptionsAdapterListener {
    lateinit var binding: ActivityCreatorBinding

    lateinit var elementsList: MutableList<ElementData>

    lateinit var tempImageList: MutableList<TempUiImageData>

    lateinit var adapter: AddedElementsAdapter

    var headerVisible: Boolean = true

    lateinit var creatorGestureDetector: GestureDetector

    lateinit var creatorScaleDetector: ScaleGestureDetector

    lateinit var imageLauncher: ActivityResultLauncher<Intent>

    lateinit var audioLauncher: ActivityResultLauncher<Intent>

    lateinit var mainOptionsList: MutableList<ElementOptionData>

    lateinit var textOptionsList: MutableList<ElementOptionData>
    lateinit var imageOptionsList: MutableList<ElementOptionData>
    lateinit var blockOptionsList: MutableList<ElementOptionData>
    lateinit var gradientOptionsList: MutableList<ElementOptionData>
    lateinit var patternOptionsList: MutableList<ElementOptionData>
    lateinit var audioOptionsList: MutableList<ElementOptionData>


    lateinit var mainOptionsAdapter: ElementOptionsAdapter
    lateinit var textOptionsAdapter: ElementOptionsAdapter
    lateinit var imageOptionsAdapter: ElementOptionsAdapter
    lateinit var blockOptionsAdapter: ElementOptionsAdapter
    lateinit var gradientOptionsAdapter: ElementOptionsAdapter
    lateinit var patternOptionsAdapter: ElementOptionsAdapter
    lateinit var audioOptionsAdapter: ElementOptionsAdapter


    lateinit var scaleTypeList: MutableList<ImageScaleTypeData>

    lateinit var imageFilters: ImageFilters

    lateinit var behaviour: BottomSheetBehavior<View>

    var maxHeight = 0f
    var minimumHeight = 0f
    var sheetHeight = 0f

    lateinit var flingY: FlingAnimation

    var CURRENT_SELECTED_MAIN_OPTION: ElementOptionData? = null
    lateinit var mainCardData: MainCardData

    var creatorMaxHeight = 0f
    var creatorMaxWidth = 0f

    var isMainFocused = false

    lateinit var cardData: CardData

    lateinit var firebaseAuth: FirebaseAuth
    var user: FirebaseUser? = null
    lateinit var firestore: FirebaseFirestore
    lateinit var storageReference: StorageReference

    var CALL_TYPE_NEW = "new"
    var CALL_TYPE_EDIT = "edit"

    var callType: String = "new"

    lateinit var elements: Elements

    lateinit var colourUtils: ColourUtils

    lateinit var interstitalAd: InterstitialAd

    lateinit var elementsMedium: ElementsMedium

    lateinit var elementsControlsMedium: ElementsControlsMedium

    @SuppressLint("ClickableViewAccessibility", "UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatorBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.deepPurpleLight)

        //Get the type and adjust the UI.
        callType = intent.getStringExtra("callType")!!


        //Initialize firebase
        firebaseAuth = Firebase.auth
        user = firebaseAuth.currentUser
        firestore = Firebase.firestore
        storageReference = Firebase.storage.reference

        //Initialize ads if required.
        if (user != null) {
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get()
                .addOnCompleteListener {
                    if (!it.result.exists()) {
                        val adRequest = AdRequest.Builder().build()
                        binding.creatorBanner.loadAd(adRequest)
                        binding.creatorBanner.visibility = View.VISIBLE
                    } else {
                        binding.creatorBanner.visibility = View.GONE
                    }
                }
        } else {
            binding.creatorBanner.visibility = View.VISIBLE
        }



        colourUtils = ColourUtils(this)

        elements = Elements(this)
        imageFilters = ImageFilters(this)


        elementsList = ArrayList()
        tempImageList = ArrayList()
        mainOptionsList = AppUtils.mainOptions()
        textOptionsList = elements.textOptions()
        imageOptionsList = elements.imageOptions()
        blockOptionsList = elements.blockOptions()
        gradientOptionsList = elements.gradientOptions()
        patternOptionsList = elements.patternOptions()
        audioOptionsList = elements.audioOptions()

        scaleTypeList = AppUtils.scaleTypeItems()

        elementsMedium = ElementsMedium()
        elementsControlsMedium = ElementsControlsMedium()

        if (callType == CALL_TYPE_NEW) {
            //Create the main card data.
            mainCardData = MainCardData()
            CURRENT_SELECTED_MAIN_OPTION = mainOptionsList[0]


            //Create the greetr card data and add the main card data and elements list to it.
            cardData = CardData()
            cardData.mainCardData = mainCardData
            cardData.elementsList = elementsList
            cardData.createdOn = intent.getLongExtra("creationTime", 0)
            cardData.id = intent.getIntExtra("newId", 0)


            //Set the creator details.
            if (user != null) {
                cardData.by = user!!.displayName!!
                cardData.creatorId = user!!.uid
            }

        }

        if (callType == CALL_TYPE_EDIT) {
            //Get the data.
            cardData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("cardData", CardData::class.java)!!
            } else {
                intent.getSerializableExtra("cardData") as CardData
            }


            mainCardData = cardData.mainCardData!!
            elementsList = cardData.elementsList!!

            //Set the default selected option for main card.
            CURRENT_SELECTED_MAIN_OPTION = mainOptionsList[0]

            //Set the name of the card to the input header.
            binding.creatorCardName.setText(cardData.cardName)

        }

        //Set the main card current and max height. Also set the data to the main card data.
        binding.creatorCard.doOnLayout {
            //Set the width and height only when the user is creating a new card, cause in case of  editing, then those are already available.
            if (callType == CALL_TYPE_NEW) {
                mainCardData.height = binding.creatorCard.height
                mainCardData.width = binding.creatorCard.width
            }


            creatorMaxHeight = mainCardData.maxHeight.toFloat()
            creatorMaxWidth = mainCardData.maxWidth.toFloat()

            //Update the UI.
            if (callType == "edit") {
                //Build the main card.
                elementsControlsMedium.updateMainCardWidth(mainCardData.width)

                elementsControlsMedium.updateMainCardHeight(mainCardData.height)

                elementsControlsMedium.updateMainCardZoomX(mainCardData.scaleX)

                elementsControlsMedium.updateMainCardZoomY(mainCardData.scaleY)

                elementsControlsMedium.updateMainCardRotation(mainCardData.rotation)

                elementsControlsMedium.updateMainCardCorners(mainCardData.cornerRadius)

                elementsControlsMedium.updateMainCardStrokeWidth(mainCardData.strokeWidth)

                elementsControlsMedium.updateMainCardStrokeColor(Color.parseColor(mainCardData.strokeColor))

                elementsControlsMedium.updateMainCardForegroundType(mainCardData.foregroundType)

                when (mainCardData.foregroundType) {
                    AppUtils.MAIN_FOREGROUND_COLOR -> {
                        elementsControlsMedium.updateMainCardColor(Color.parseColor(mainCardData.color))
                    }

                    AppUtils.MAIN_FOREGROUND_GRADIENT -> {
                        elementsControlsMedium.updateMainCardGradColors(mainCardData.gradientColors!!)

                        elementsControlsMedium.updateMainCardGradAngle(mainCardData.gradientAngle)
                    }

                    AppUtils.MAIN_FOREGROUND_PATTERN -> {
                        elementsControlsMedium.updateMainCardPattern(mainCardData.patternData!!)
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    loadAvailableElements()


                    //Update the UI.
                    //Adjust the elements bar.
                    adjustElementsBar()

                    //Adjust the focused Element
                    setFocusedElement(elementsList.size - 1)
                }


            }
        }

        //Set the padding to the creator card once the options bar pops up.
        binding.elementOptionsRecy.doOnLayout {
            behaviour.peekHeight = binding.elementOptionsRecy.height + 10

            val bottomPadding = binding.elementOptionsRecy.height + 20
            (binding.creatorCard.layoutParams as ConstraintLayout.LayoutParams).bottomMargin =
                bottomPadding
        }


        //Image input activity for result.
        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val loadingSB = AppUtils.buildSnackbar(
                        this,
                        "Loading the image, please wait...",
                        binding.creatorElementOptionsHolder
                    )
                    loadingSB.duration = Snackbar.LENGTH_INDEFINITE
                    loadingSB.show()

                    runBlocking {
                        CoroutineScope(Dispatchers.IO).launch {
                            val inputStream = contentResolver.openInputStream(it.data!!.data!!)
                            val bit = BitmapFactory.decodeStream(inputStream)

                            //Write the bitmap to cache.
                            val cachePath =
                                cacheDir.toString() + "/" + "Crafty/" + elementsList[adapter.currentFocusedElement].id.toString() + ".png"
                            val folder = File("$cacheDir/Crafty/")
                            if (!folder.exists()) {
                                folder.mkdir()
                            }

                            val file = File(cachePath)
                            var fos: FileOutputStream? = null
                            withContext(Dispatchers.IO) {
                                try {
                                    fos = FileOutputStream(file)
                                    bit.compress(Bitmap.CompressFormat.PNG, 100, fos!!)
                                } catch (e: Exception) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        AppUtils.buildSnackbar(
                                            this@CreatorActivity,
                                            "Unable to load the image, try again...",
                                            binding.creatorElementOptionsHolder
                                        ).show()
                                    }
                                    e.printStackTrace()
                                } finally {
                                    fos!!.flush()
                                    fos!!.close()
                                }
                            }



                            withContext(Dispatchers.Main) {
                                elementsControlsMedium.updateImageInput(
                                    elementsList[adapter.currentFocusedElement],
                                    cachePath,
                                    bit
                                )

                                AppUtils.buildSnackbar(
                                    this@CreatorActivity,
                                    "Image loaded successfully!",
                                    binding.creatorElementOptionsHolder
                                ).show()
                            }
                        }
                    }
                }
            }


        //Audio input activity for result.
        audioLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val audioUri = it.data!!.data


                    val loadingSB = AppUtils.buildSnackbar(
                        this,
                        "Loading the audio file, please wait...",
                        binding.creatorElementOptionsHolder
                    )
                    loadingSB.duration = Snackbar.LENGTH_INDEFINITE
                    loadingSB.show()

                    //Save the audio file to cache.
                    runBlocking {
                        CoroutineScope(Dispatchers.IO).launch {
                            val inputStream = contentResolver.openInputStream(audioUri!!)


                            //Write the audio file to cache.
                            val cachePath =
                                cacheDir.toString() + "/" + "Crafty/" + elementsList[adapter.currentFocusedElement].id.toString() + ".mp3"

                            val folder = File("$cacheDir/Crafty/")
                            if (!folder.exists()) {
                                folder.mkdir()
                            }

                            val file = File(cachePath)
                            var fos: FileOutputStream? = null
                            withContext(Dispatchers.IO) {

                                try {
                                    fos = FileOutputStream(file)
                                    fos!!.write(inputStream!!.readBytes())
                                } catch (e: Exception) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        AppUtils.buildSnackbar(
                                            this@CreatorActivity,
                                            "Unable to load the audio file, try again...",
                                            binding.creatorElementOptionsHolder
                                        ).show()
                                    }
                                    e.printStackTrace()
                                } finally {
                                    inputStream?.close()

                                    fos!!.flush()
                                    fos!!.close()

                                }


                            }



                            withContext(Dispatchers.Main) {
                                elementsControlsMedium.updateAudioInput(
                                    elementsList[adapter.currentFocusedElement],
                                    cachePath
                                )


                                AppUtils.buildSnackbar(
                                    this@CreatorActivity,
                                    "Audio file loaded successfully!",
                                    binding.creatorElementOptionsHolder
                                ).show()
                            }

                        }
                    }
                }
            }


        val lm = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.creatorAddedElementsRecy.layoutManager = lm

        adapter = AddedElementsAdapter(this, elementsList)
        binding.creatorAddedElementsRecy.adapter = adapter


        adjustElementsBar()


        behaviour =
            BottomSheetBehavior.from(binding.elementOptionsSheet)



        behaviour.isHideable = false



        behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })


        //Create max and min values for height and width of the overall activity.
        binding.elementOptionsSheet.viewTreeObserver.addOnDrawListener {
            sheetHeight = binding.elementOptionsSheet.height.toFloat()
            minimumHeight = binding.root.height - binding.elementOptionsSheet.height.toFloat()
            maxHeight = binding.root.height - (binding.elementOptionsRecy.height.toFloat() + 10f)

            flingY = FlingAnimation(binding.elementOptionsSheet, FlingAnimation.Y).apply {
                friction = 0.7f
                setMinValue(minimumHeight)
                setMaxValue(maxHeight)

            }
        }


        //Initialize the options adapters.
        val elementOptionsLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.elementOptionsRecy.layoutManager =
            elementOptionsLayoutManager



        mainOptionsAdapter =
            ElementOptionsAdapter(this, mainOptionsList, AppUtils.mainOptionDrawables(), this)
        textOptionsAdapter =
            ElementOptionsAdapter(this, textOptionsList, elements.textOptionDrawables(), this)
        binding.elementOptionsRecy.adapter = textOptionsAdapter
        ViewCompat.setNestedScrollingEnabled(binding.elementOptionsRecy, false)

        imageOptionsAdapter =
            ElementOptionsAdapter(this, imageOptionsList, elements.imageOptionDrawables(), this)
        blockOptionsAdapter =
            ElementOptionsAdapter(this, blockOptionsList, elements.blockOptionDrawables(), this)
        gradientOptionsAdapter = ElementOptionsAdapter(
            this,
            gradientOptionsList,
            elements.gradientOptionDrawables(),
            this
        )
        patternOptionsAdapter =
            ElementOptionsAdapter(this, patternOptionsList, elements.patternOptionDrawable(), this)

        audioOptionsAdapter =
            ElementOptionsAdapter(this, audioOptionsList, elements.audioOptionDrawables(), this)


        //Handle visibility of element options bar.
        handleElementsOptionsBarVisibility()


        adapter.setListener(object : AddedElementsAdapter.ClickListener {
            override fun onClick(position: Int) {
                setFocusedElement(position)
            }

        })






        binding.creatorOptions.setOnClickListener {
            //Load pop up menus.
            val popupMenu =
                PopupMenu(ContextThemeWrapper(this, R.style.PopupStyle), binding.creatorOptions)
            popupMenu.inflate(R.menu.creator_options)
            popupMenu.setForceShowIcon(true)

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.clearAll -> {
                        elementsMedium.clearAllElements()
                    }

                    R.id.mergeAll -> {
                        elementsMedium.mergeAllElements()
                    }

                    R.id.randomizeElementsPos -> {
                        elementsMedium.randomizePosition()
                    }

                    R.id.saveCard -> {
                        saveCard()
                    }

                    R.id.exportAsJPG -> {
                        exportAsJPG()
                    }

                    R.id.exportAsPNG -> {
                        exportAsPNG()
                    }

                    R.id.exportAsVid -> {
                        exportAsMp4()
                    }

                }
                true
            }
            popupMenu.show()
        }


        binding.creatorCloseButton.setOnClickListener {
            backPressDialog()
        }



        binding.creatorCardName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    cardData.cardName = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })



        creatorGestureDetector =
            GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    handleHeaderSpaceVisibility()
                    return super.onDoubleTap(e)
                }

                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    setFocusedElement(-1)

                    return super.onSingleTapUp(e)
                }

            })

        creatorScaleDetector = ScaleGestureDetector(this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    var scaleFactorX = binding.creatorCard.scaleX
                    var scaleFactorY = binding.creatorCard.scaleY

                    scaleFactorX *= abs(detector.scaleFactor)
                    scaleFactorY *= abs(detector.scaleFactor)



                    elementsControlsMedium.updateMainCardZoomX(scaleFactorX)
                    elementsControlsMedium.updateMainCardZoomY(scaleFactorY)
                    return true
                }
            })

        var movedX = 0f
        var movedY = 0f

        binding.creatorCard.setOnTouchListener { v, event ->
            when (event.action and MotionEvent.ACTION_MASK) {

                MotionEvent.ACTION_DOWN -> {
                    movedX = v.x - event.rawX
                    movedY = v.y - event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    val xValue = event.rawX.toInt() + movedX
                    val yValue = event.rawY.toInt() + movedY

                    //Add half of width and height to x and y values respectively to shift the points to middle of the text.
                    val mX = xValue + v.width / 2
                    val mY = yValue + v.height / 2

                    if (mX < creatorMaxWidth && mX > 0f &&
                        mY > 0f && mY < creatorMaxHeight
                    ) {
                        v.x = xValue
                        v.y = yValue

                        mainCardData.xPosition = xValue
                        mainCardData.yPosition = yValue
                    }
                }

            }
            v.invalidate()


            creatorScaleDetector.onTouchEvent(event)
            creatorGestureDetector.onTouchEvent(event)
            true
        }


        binding.creatorCardName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //Clear focus here from edittext
                binding.creatorCardName.clearFocus()
            }
            false
        }


        binding.root.setOnClickListener {
            //Remove element focus.
            setFocusedElement(-1)
        }

        //Highlight the add element button to user if there are no elements and the user is not doing anything.
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            if (elementsList.isEmpty()) {
                val scaler = ScaleAnimation(
                    1f,
                    1.3f,
                    1f,
                    1.3f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                scaler.duration = 300
                scaler.repeatMode = Animation.REVERSE
                scaler.repeatCount = Animation.INFINITE
                binding.creatorAddElement.startAnimation(scaler)
            }
        }

        val showTip = getSharedPreferences("appPrefs", MODE_PRIVATE).getBoolean("showTip", true)
        if (showTip) {
            val snackbar = AppUtils.buildSnackbar(
                this,
                "Tip: Double tap on your card to hide/show the header space",
                binding.creatorCard
            )
            snackbar.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    getSharedPreferences("appPrefs", MODE_PRIVATE).edit()
                        .putBoolean("showTip", false).apply()
                }
            })
            snackbar.setAction("OK") {
                snackbar.dismiss()
            }
            snackbar.duration = Snackbar.LENGTH_INDEFINITE
            snackbar.show()
        }

        //Dark mode.
        darkMode(AppUtils.isDarkMode(this))

        /*binding.root.doOnLayout {
             //Guided tour.
             val guidedList = loadFirstHalfGuidedTourList()
             val gson = Gson()
             val jsonList = gson.toJson(guidedList)
             val intent = Intent(this, OverLayer::class.java)
             intent.putExtra("guidedList", jsonList)
             intent.putExtra("from", "creator")
             startActivity(intent)
             overridePendingTransition(R.anim.activity_stable, R.anim.activity_stable)
         }*/
    }

    inner class ElementsMedium : ElementsSheet.ElementsSheetListener {
        init {

            binding.creatorAddElement.setOnClickListener {
                val elementsSheet = ElementsSheet(this)
                elementsSheet.show(supportFragmentManager, "UseCaseOne")

                //Stop any animations if playing.
                it.clearAnimation()
            }

        }

        override fun onElementSelected(type: String) {
            addElement(type)
        }

        @SuppressLint("UnsafeOptInUsageError")
        override fun onSavedElementSelected(elementData: ElementData) {
            //Create gesture detector
            val gestureDetector = createElementGestureDetector()
            val scaleDetector = createElementScaleDetector()

            val gson = Gson()


            //Notify the elements bar to add the new element.
            elementsList.add(elementData)
            adapter.notifyItemInserted(elementsList.size - 1)

            try {
                when (elementData.elementType) {
                    TEXT_TYPE -> {
                        val hashMapTree = gson.toJsonTree(elementData.data)
                        val data = gson.fromJson(hashMapTree, TextData::class.java)
                        elementData.data = data


                        //Create the text element.
                        createTextElement(
                            elementData,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(elementData)
                        )

                        //Update all the required attributes.
                        elementsControlsMedium.updateTextInput(elementData, data.text)
                        elementsControlsMedium.updateTextSize(elementData, data.size)

                        if (data.font != null) {
                            elementsControlsMedium.updateTextFont(elementData, data.font!!)
                        }

                        elementsControlsMedium.updateTextColor(
                            elementData,
                            Color.parseColor(data.color)
                        )
                        elementsControlsMedium.updateTextDepthColor(
                            elementData,
                            Color.parseColor(data.depthColor)
                        )
                        elementsControlsMedium.updateTextDepthHorizontal(
                            elementData,
                            data.horzDepth
                        )
                        elementsControlsMedium.updateTextDepthVertical(elementData, data.vertDepth)
                        elementsControlsMedium.updateTextDepthRadius(elementData, data.depthRadius)

                        elementsControlsMedium.updateElementZoomX(elementData, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(elementData, data.scaleY)
                        elementsControlsMedium.updateElementRotation(elementData, data.rotation)
                        elementsControlsMedium.updateElementBackgroundPadding(
                            elementData,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            elementData,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            elementData,
                            Color.parseColor(data.backgroundColor)
                        )

                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(elementData.id).x =
                                elementData.xPosition
                            binding.creatorCardLayout.findViewById<View>(elementData.id).y =
                                elementData.yPosition
                        }

                    }

                    IMAGE_TYPE -> {
                        val hashMapTree = gson.toJsonTree(elementData.data)
                        val data = gson.fromJson(hashMapTree, ImageData::class.java)
                        elementData.data = data


                        //Create the image element.
                        createImageElement(
                            elementData,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(elementData)
                        )

                        //Get, load and set the image.
                        if (AppUtils.hasStoragePermission(this@CreatorActivity)) {
                            val file = File(data.localPath)
                            if (file.exists()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val inputStream = FileInputStream(file.path)
                                    val bit = BitmapFactory.decodeStream(inputStream)

                                    val tempData = TempUiImageData()
                                    tempData.bitmap = bit
                                    tempData.elementId = elementData.id
                                    tempImageList.add(tempData)

                                    withContext(Dispatchers.Main) {
                                        elementsControlsMedium.updateImageInput(
                                            elementData,
                                            data.localPath,
                                            bit
                                        )
                                    }
                                }
                            } else {
                                if (user == null) {
                                    return
                                }
                                val path = data.storagePath.ifEmpty {
                                    "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${user!!.uid}/${AppUtils.IMAGE_ELEMENTS_REFERENCE}/${elementData.id}"
                                }


                                val ref = storageReference.child(path)


                                try {
                                    ref.getStream { state, stream ->
                                        if (state.error != null) {
                                            return@getStream
                                        }
                                        val bitmap = BitmapFactory.decodeStream(stream)

                                        val tempData = TempUiImageData()
                                        tempData.bitmap = bitmap
                                        tempData.elementId = elementData.id
                                        tempImageList.add(tempData)

                                        elementsControlsMedium.updateImageInput(
                                            elementData,
                                            data.localPath,
                                            bitmap
                                        )
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                } catch (e: StorageException) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            AppUtils.buildStoragePermission(this@CreatorActivity).show()
                        }


                        //Get the right filter with the name and set it.
                        CoroutineScope(Dispatchers.IO).launch {
                            imageFilters.getFilterWithName(
                                data.filterName,
                                object : ImageFilters.ImageFiltersListener {
                                    override fun onFiltersWithImageLoaded(loadedFilters: MutableList<Bitmap>) {

                                    }

                                    override fun onFilterReadyWithName(filter: ImageFilterData) {
                                        elementsControlsMedium.updateImageFilter(
                                            elementData,
                                            filter
                                        )
                                    }

                                })
                        }

                        elementsControlsMedium.updateImageScaleType(elementData, data.scaleType!!)


                        elementsControlsMedium.updateElementSizeWidth(elementData, data.width)
                        elementsControlsMedium.updateElementSizeHeight(elementData, data.height)
                        elementsControlsMedium.updateElementZoomX(elementData, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(elementData, data.scaleY)
                        elementsControlsMedium.updateElementRotation(elementData, data.rotation)
                        elementsControlsMedium.updateElementCorners(elementData, data.corners)
                        elementsControlsMedium.updateElementBackgroundPadding(
                            elementData,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            elementData,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            elementData,
                            Color.parseColor(data.backgroundColor)
                        )


                        //Set positions.
                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(elementData.id).x =
                                elementData.xPosition
                            binding.creatorCardLayout.findViewById<View>(elementData.id).y =
                                elementData.yPosition
                        }

                    }

                    BLOCK_TYPE -> {
                        val hashMapTree = gson.toJsonTree(elementData.data)
                        val data = gson.fromJson(hashMapTree, BlockData::class.java)
                        elementData.data = data


                        //Create the block element.
                        createBlockElement(
                            elementData,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(elementData)
                        )

                        elementsControlsMedium.updateBlockColor(
                            elementData,
                            Color.parseColor(data.color)
                        )

                        elementsControlsMedium.updateElementSizeWidth(elementData, data.width)
                        elementsControlsMedium.updateElementSizeHeight(elementData, data.height)
                        elementsControlsMedium.updateElementZoomY(elementData, data.scaleY)
                        elementsControlsMedium.updateElementRotation(elementData, data.rotation)
                        elementsControlsMedium.updateElementCorners(elementData, data.corners)
                        elementsControlsMedium.updateElementShadowRadius(
                            elementData,
                            data.shadowRadius
                        )
                        elementsControlsMedium.updateElementStrokeWidth(
                            elementData,
                            data.strokeWidth
                        )
                        elementsControlsMedium.updateElementStrokeColour(
                            elementData,
                            Color.parseColor(data.strokeColor)
                        )
                        elementsControlsMedium.updateElementBackgroundPadding(
                            elementData,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            elementData,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            elementData,
                            Color.parseColor(data.backgroundColor)
                        )

                        //Set positions.
                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(elementData.id).x =
                                elementData.xPosition
                            binding.creatorCardLayout.findViewById<View>(elementData.id).y =
                                elementData.yPosition
                        }


                    }

                    GRADIENT_TYPE -> {
                        val hashMapTree = gson.toJsonTree(elementData.data)
                        val data = gson.fromJson(hashMapTree, GradientData::class.java)
                        elementData.data = data


                        //Create the gradient element.
                        createGradientElement(
                            elementData,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(elementData)
                        )

                        elementsControlsMedium.updateGradientColors(elementData, data.colors!!)
                        elementsControlsMedium.updateGradientAngle(elementData, data.gradientAngle)


                        elementsControlsMedium.updateElementSizeWidth(elementData, data.width)
                        elementsControlsMedium.updateElementSizeHeight(elementData, data.height)
                        elementsControlsMedium.updateElementZoomX(elementData, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(elementData, data.scaleY)
                        elementsControlsMedium.updateElementRotation(elementData, data.rotation)
                        elementsControlsMedium.updateElementCorners(elementData, data.corners)
                        elementsControlsMedium.updateElementShadowRadius(
                            elementData,
                            data.shadowRadius
                        )
                        elementsControlsMedium.updateElementStrokeWidth(
                            elementData,
                            data.strokeWidth
                        )
                        elementsControlsMedium.updateElementStrokeColour(
                            elementData,
                            Color.parseColor(data.strokeColor)
                        )
                        elementsControlsMedium.updateElementBackgroundPadding(
                            elementData,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            elementData,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            elementData,
                            Color.parseColor(data.backgroundColor)
                        )

                        //Set positions.
                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(elementData.id).x =
                                elementData.xPosition
                            binding.creatorCardLayout.findViewById<View>(elementData.id).y =
                                elementData.yPosition
                        }
                    }

                    PATTERN_TYPE -> {
                        val hashMapTree = gson.toJsonTree(elementData.data)
                        val data = gson.fromJson(hashMapTree, PatternElementData::class.java)
                        elementData.data = data


                        //Create the pattern element.
                        createPatternElement(
                            elementData,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(elementData)
                        )

                        elementsControlsMedium.updatePatternDesign(elementData, data.pattern!!)

                        elementsControlsMedium.updateElementSizeWidth(elementData, data.width)
                        elementsControlsMedium.updateElementSizeHeight(elementData, data.height)
                        elementsControlsMedium.updateElementZoomX(elementData, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(elementData, data.scaleY)
                        elementsControlsMedium.updateElementRotation(elementData, data.rotation)
                        elementsControlsMedium.updateElementCorners(elementData, data.corners)
                        elementsControlsMedium.updateElementShadowRadius(
                            elementData,
                            data.shadowRadius
                        )
                        elementsControlsMedium.updateElementStrokeWidth(
                            elementData,
                            data.strokeWidth
                        )
                        elementsControlsMedium.updateElementStrokeColour(
                            elementData,
                            Color.parseColor(data.strokeColor)
                        )
                        elementsControlsMedium.updateElementBackgroundPadding(
                            elementData,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            elementData,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            elementData,
                            Color.parseColor(data.backgroundColor)
                        )

                        //Set positions.
                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(elementData.id).x =
                                elementData.xPosition
                            binding.creatorCardLayout.findViewById<View>(elementData.id).y =
                                elementData.yPosition
                        }
                    }

                    AUDIO_TYPE -> {
                        val hashMapTree = gson.toJsonTree(elementData.data)
                        val data = gson.fromJson(hashMapTree, AudioData::class.java)
                        elementData.data = data


                        //Create the pattern element.
                        createAudioElement(
                            elementData,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(elementData)
                        )

                        //Check if the file is available locally or needs to get from storage and then load it.
                        if (AppUtils.hasStoragePermission(this@CreatorActivity)) {
                            val file = File(data.localPath)
                            if (file.exists()) {
                                elementsControlsMedium.updateAudioInput(elementData, data.localPath)
                            }
                            else {
                                if (user == null) {
                                    return
                                }
                                //Get the storage file if there's any.
                                val path = data.storagePath.ifEmpty {
                                    "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${user!!.uid}/${AppUtils.AUDIO_ELEMENTS_REFERENCE}/${elementData.id}"
                                }
                                val ref = storageReference.child(path)

                                //Load the audio file into the cache.
                                val cachePath =
                                    cacheDir.toString() + "/" + "Crafty/" + elementData.id.toString() + ".mp3"
                                val folder = File("$cacheDir/Crafty/")
                                if (!folder.exists()) {
                                    folder.mkdir()
                                }

                                //Set the local path to the data.
                                data.localPath = cachePath

                                val cacheFile = File(cachePath)
                               val loadingSb =  AppUtils.buildSnackbar(this@CreatorActivity, "Loading the audio, please wait...", binding.root)
                                loadingSb.duration = Snackbar.LENGTH_INDEFINITE
                                loadingSb.show()
                                ref.getFile(cacheFile).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        elementsControlsMedium.updateAudioInput(elementData, data.localPath)
                                        AppUtils.buildSnackbar(this@CreatorActivity, "Audio file loaded successfully", binding.root).show()
                                    }
                                    else {
                                        AppUtils.buildSnackbar(this@CreatorActivity, "Unable to load the audio file, try again...", binding.root).show()

                                    }
                                }
                            }
                        }
                        else {
                            AppUtils.buildStoragePermission(this@CreatorActivity).show()
                        }

                        elementsControlsMedium.updateElementSizeWidth(elementData, data.width)
                        elementsControlsMedium.updateElementSizeHeight(elementData, data.height)
                        elementsControlsMedium.updateElementZoomX(elementData, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(elementData, data.scaleY)
                        elementsControlsMedium.updateElementRotation(elementData, data.rotation)
                        elementsControlsMedium.updateElementCorners(elementData, data.corners)
                        elementsControlsMedium.updateElementShadowRadius(
                            elementData,
                            data.shadowRadius
                        )
                        elementsControlsMedium.updateElementStrokeWidth(
                            elementData,
                            data.strokeWidth
                        )
                        elementsControlsMedium.updateElementStrokeColour(
                            elementData,
                            Color.parseColor(data.strokeColor)
                        )
                        elementsControlsMedium.updateElementBackgroundPadding(
                            elementData,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            elementData,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            elementData,
                            Color.parseColor(data.backgroundColor)
                        )

                        //Set positions.
                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(elementData.id).x =
                                elementData.xPosition
                            binding.creatorCardLayout.findViewById<View>(elementData.id).y =
                                elementData.yPosition
                        }

                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            //Adjust the elements bar.
            adjustElementsBar()

            //Adjust the focused Element
            setFocusedElement(elementsList.size - 1)

            //Handle elements options bar
            handleElementsOptionsBarVisibility()
        }

        override fun onShowMessage(text: String) {
            AppUtils.buildSnackbar(this@CreatorActivity, text, binding.root).show()
        }

        //Element options.
        fun clearAllElements() {
            val iterator = elementsList.iterator()
            while (iterator.hasNext()) {
                val element = iterator.next()
                val position = elementsList.indexOf(element)
                val view = binding.creatorCardLayout.findViewById<View>(element.id)
                binding.creatorCardLayout.removeView(view)
                iterator.remove()
                adapter.notifyItemRemoved(position)

                if (elementsList.isEmpty()) {
                    adapter.currentFocusedElement = 0
                    handleElementsOptionsBarVisibility()
                    AppUtils.buildSnackbar(
                        this@CreatorActivity,
                        "All elements removed",
                        binding.root
                    ).show()
                }
            }
        }

        fun mergeAllElements() {
            //Update the data.
            for (element in elementsList) {
                element.xPosition =
                    mainCardData.width.toFloat() / 2 - binding.creatorCardLayout.findViewById<View>(
                        element.id
                    ).width / 2
                element.yPosition =
                    mainCardData.height.toFloat() / 2 - binding.creatorCardLayout.findViewById<View>(
                        element.id
                    ).height / 2

                //Update the UI.
                binding.creatorCardLayout.findViewById<View>(element.id).x = element.xPosition
                binding.creatorCardLayout.findViewById<View>(element.id).y = element.yPosition

                AppUtils.buildSnackbar(
                    this@CreatorActivity,
                    "All elements merged to center",
                    binding.root
                ).show()

            }
        }

        fun randomizePosition() {
            for (element in elementsList) {
                element.xPosition = Random.nextInt(
                    0,
                    mainCardData.width - binding.creatorCardLayout.findViewById<View>(element.id).width / 2
                ).toFloat()
                element.yPosition = Random.nextInt(
                    0,
                    mainCardData.height - binding.creatorCardLayout.findViewById<View>(element.id).height / 2
                ).toFloat()
                binding.creatorCardLayout.findViewById<View>(element.id).x = element.xPosition
                binding.creatorCardLayout.findViewById<View>(element.id).y = element.yPosition
            }
        }

        fun saveElement(elementData: ElementData) {

            if (user != null) {
                val ref = firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                    .collection(user!!.uid)
                    .document("doc").collection(AppUtils.CRAFTY_ELEMENTS_COLLECTION)

                //Save the element to user's list.
                ref.document(elementData.id.toString()).set(elementData)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            AppUtils.buildSnackbar(
                                this@CreatorActivity,
                                "Element saved successfully to your list!",
                                binding.root
                            ).show()

                        } else {
                            AppUtils.buildSnackbar(
                                this@CreatorActivity,
                                "Unable to save the element, try again later",
                                binding.root
                            ).show()
                        }
                    }
            }
        }

        fun exportElement(elementData: ElementData, formatType: String) {
            //Get the element bitmap.
            val view = getElementById(elementData) ?: return
            val bitmap =
                Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bitmap)

            //Remove the focus so the outliner will not get drawn.
            setFocusedElement(-1)

            view.draw(canvas)

            var name = "name"
            name = if (TextUtils.isEmpty(elementData.elementName.trim())) {
                elementData.elementType
            } else {
                elementData.elementName
            }
            val savingSheet = SavingSheet()
            savingSheet.show(supportFragmentManager, "UseCaseFour")

            val mediaListener = object : AppUtils.MediaListener {
                override fun onMediaSaved(savedPath: String) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(5000)
                        savingSheet.dismiss()
                        val snackbar =
                            AppUtils.buildSnackbar(
                                this@CreatorActivity,
                                "Element saved at Pictures/Crafty",
                                binding.greetrCreatorParent
                            )
                        snackbar.setAction("open") {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(savedPath)
                            startActivity(intent)
                        }
                        snackbar.show()
                    }

                }

                override fun onMediaSaveProgress(progress: Int) {

                }

                override fun onMediaSaveFailed(reason: String) {
                    savingSheet.dismiss()
                }

            }

            val savingFileName = AppUtils.uniqueContentNameGenerator(name)
            CoroutineScope(Dispatchers.IO).launch {
                AppUtils.saveImageBitmap(
                    this@CreatorActivity,
                    savingFileName,
                    formatType,
                    bitmap,
                    mediaListener
                )
            }
        }

        fun exportElementAsMp4(elementData: ElementData) {

            var name = "name"
            name = if (TextUtils.isEmpty(elementData.elementName.trim())) {
                elementData.elementType
            } else {
                elementData.elementName
            }

            //Set the audio path if it's audio element.
            var audioPath = "No audio"

            if (elementData.elementType == AUDIO_TYPE) {
                audioPath = (elementData.data as AudioData).localPath
            }


            CoroutineScope(Dispatchers.IO).launch {
                var bitmap: Bitmap? = null
                withContext(Dispatchers.Main) {
                    //Get the element bitmap.
                    val view = getElementById(elementData) ?: return@withContext
                     bitmap =
                        Bitmap.createBitmap(if (view.width.rem(2) == 0) {
                            view.width
                        } else {
                            view.width + 1
                        },
                            if (view.height.rem(2) == 0) {
                                view.height
                            } else {
                                view.height + 1
                            }, Bitmap.Config.ARGB_8888)

                    val canvas = Canvas(bitmap!!)

                    //Remove the focus so the outliner will not get drawn.
                    setFocusedElement(-1)

                    view.draw(canvas)
                }

                val fileName = AppUtils.uniqueContentNameGenerator(name)

                val savingSheet = SavingSheet()
                withContext(Dispatchers.Main) {
                    savingSheet.show(supportFragmentManager, "UseCaseFour")
                }

                AppUtils.saveCardAsMp4(
                    this@CreatorActivity,
                    fileName,
                    bitmap!!,
                    audioPath,
                    object : AppUtils.MediaListener {
                        override fun onMediaSaved(savedPath: String) {
                            CoroutineScope(Dispatchers.Main).launch {

                                savingSheet.dismiss()
                                val snackbar = AppUtils.buildSnackbar(
                                    this@CreatorActivity,
                                    "Element saved as video successfully at 'Movies/Crafty'",
                                    binding.root
                                )
                                snackbar.setAction("Open") {
                                    val fileUri = FileProvider.getUriForFile(
                                        this@CreatorActivity,
                                        BuildConfig.APPLICATION_ID + ".provider", File(savedPath)
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(fileUri, "video/*")
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    startActivity(intent)
                                }
                                snackbar.show()
                            }

                        }

                        override fun onMediaSaveProgress(progress: Int) {
                            CoroutineScope(Dispatchers.Main).launch {
                                savingSheet.updateProgress(progress)
                            }
                        }

                        override fun onMediaSaveFailed(reason: String) {
                            CoroutineScope(Dispatchers.Main).launch {
                                savingSheet.dismiss()
                                AppUtils.buildSnackbar(this@CreatorActivity, reason, binding!!.root)
                                    .show()
                            }
                        }
                    })
            }

        }


        fun getElementById(elementData: ElementData): View? {
            try {
                return binding.creatorCardLayout.findViewById<View>(elementData.id)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun removeElement(elementData: ElementData) {
            //Get the iterator.
            val iterator = elementsList.iterator()
            //Iterate using the while loop.
            while (iterator.hasNext()) {
                val data = iterator.next()
                //Find the matching element data.
                if (data.id == elementData.id) {
                    //Get the position of the element in the list.
                    val position = elementsList.indexOf(data)
                    //Remove the element from the UI.
                    binding.creatorCardLayout.removeView(getElementById(elementData))

                    //Remove the element form added elements list.
                    iterator.remove()
                    adapter.notifyItemRemoved(position)

                    //Change current focused position.
                    if (elementsList.isNotEmpty()) {
                        adapter.currentFocusedElement = 0
                    }

                    //Shift the focus to the main card.
                    setFocusedElement(-1)

                    //Stop the while loop.
                    break
                }
            }

            //If the element is image type, then remove it's temp image.
            if (elementData.elementType == IMAGE_TYPE) {
                val tempIterator = tempImageList.iterator()
                while (tempIterator.hasNext()) {
                    val tempData = tempIterator.next()
                    if (tempData.elementId == elementData.id) {
                        tempIterator.remove()
                    }
                }
            }

        }

        fun showElementPopUp() {
            val elementOptionsMenu = androidx.appcompat.widget.PopupMenu(
                ContextThemeWrapper(
                    this@CreatorActivity,
                    R.style.PopupStyle
                ), getElementById(elementsList[adapter.currentFocusedElement])!!
            )
            elementOptionsMenu.inflate(R.menu.element_options)
            elementOptionsMenu.setForceShowIcon(true)
            elementOptionsMenu.menu.findItem(R.id.elementName).title =
                elementsList[adapter.currentFocusedElement].elementName



            elementOptionsMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.setElementName -> {
                        //Shoe the dialog to set the name.
                        elementNameDialog(elementsList[adapter.currentFocusedElement])
                    }

                    R.id.unselectElement -> {
                        //Shift the focus to main card.
                        setFocusedElement(-1)
                    }

                    R.id.elementVisible -> {
                        if (user != null) {
                            //Make the element visible. Only for UI purpose not for saving this as a preference to the element.
                            val view =
                                getElementById(elementsList[adapter.currentFocusedElement])
                            if (view != null) {
                                view.visibility = View.VISIBLE
                            }
                        }

                    }

                    R.id.elementInvisible -> {
                        if (user != null) {
                            val view =
                                getElementById(elementsList[adapter.currentFocusedElement])
                            if (view != null) {
                                //Make the element invisible. Only for UI purpose not for saving this as a preference to the element.
                                view.visibility = View.INVISIBLE
                            }
                        }
                    }

                    R.id.removeElement -> {
                        //Remove the element.
                        removeElement(elementsList[adapter.currentFocusedElement])
                    }

                    R.id.randomizeElementPos -> {
                        //Randomize the element position.
                        val view = getElementById(elementsList[adapter.currentFocusedElement])
                        if (view != null) {
                            view.x =
                                Random.nextInt(0, binding.creatorCard.width - view.width / 2)
                                    .toFloat()
                            view.y = Random.nextInt(0, binding.creatorCard.height - view.height / 2)
                                .toFloat()
                        }
                    }

                    R.id.saveElement -> {
                        saveElement(elementsList[adapter.currentFocusedElement])
                    }

                    R.id.exportElementAsJPG -> {
                        if (user != null) {
                            //Export the element as image.
                            exportElement(
                                elementsList[adapter.currentFocusedElement],
                                AppUtils.JPG
                            )
                        }
                    }

                    R.id.exportElementAsPNG -> {
                        if (user != null) {
                            //Export the element as image.
                            exportElement(
                                elementsList[adapter.currentFocusedElement],
                                AppUtils.PNG
                            )
                        }
                    }

                    R.id.exportElementAsMP4 -> {
                        if (user != null) {
                            //Export element as mp4.
                            exportElementAsMp4(elementsList[adapter.currentFocusedElement])
                        }
                    }

                    R.id.exportElementAsElement -> {
                        if (user != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                elements.saveElementToDevice(
                                    elementsList[adapter.currentFocusedElement],
                                    object : AppUtils.MediaListener {
                                        override fun onMediaSaved(savedPath: String) {
                                            val snack = AppUtils.buildSnackbar(
                                                this@CreatorActivity,
                                                "Element downloaded at 'Downloads/Crafty/Elements'",
                                                binding.root
                                            )
                                            snack.duration = Snackbar.LENGTH_LONG
                                            snack.setAction("Open") {
                                                val intent =
                                                    Intent(Intent.ACTION_GET_CONTENT)

                                                intent.setDataAndType(
                                                    Uri.parse(
                                                        (Environment.getExternalStorageDirectory().path
                                                                + File.separator) + "Downloads" + File.separator + "Greetr/Elements"
                                                    ), "file/*"
                                                )
                                                startActivity(intent)
                                            }
                                            snack.show()
                                        }

                                        override fun onMediaSaveProgress(progress: Int) {

                                        }

                                        override fun onMediaSaveFailed(reason: String) {
                                            AppUtils.buildSnackbar(
                                                this@CreatorActivity,
                                                reason,
                                                binding.root
                                            )
                                                .show()
                                        }
                                    })
                            }

                        }
                    }

                    R.id.elementInfo -> {
                        val sheet = ElementInfoSheet(elementsList[adapter.currentFocusedElement])
                        sheet.show(supportFragmentManager, "UseCaseOne")
                    }
                }
                true
            }
            elementOptionsMenu.show()
        }

        fun createElementGestureDetector(): GestureDetector {
            //Create gesture detector
            val gestureDetector =
                GestureDetector(
                    this@CreatorActivity,
                    object : GestureDetector.SimpleOnGestureListener() {
                        @SuppressLint("ResourceType", "UnsafeOptInUsageError")
                        override fun onSingleTapUp(e: MotionEvent): Boolean {
                            CoroutineScope(Dispatchers.Main).launch {
                                //Add a temporary text to indicate the element name.

                                val elementData = elementsList[adapter.currentFocusedElement]
                                if (elementData.elementType == AUDIO_TYPE) {
                                    //Play or pause the audio for audio element.
                                    val playerController =
                                        binding.creatorCardLayout.findViewById<FrameLayout>(
                                            elementData.id
                                        )
                                            .findViewById<PlayerControlView>(R.id.creatorAudioElementPlayer)

                                    if (playerController.player != null) {
                                        if (playerController.player!!.isPlaying) {
                                            playerController.player!!.pause()
                                        } else {
                                            playerController.player!!.play()
                                        }
                                        playerController.show()
                                    }
                                }


                            }
                            return super.onSingleTapUp(e)
                        }

                        override fun onDoubleTap(e: MotionEvent): Boolean {
                            return super.onDoubleTap(e)
                        }

                        override fun onLongPress(e: MotionEvent) {
                            super.onLongPress(e)

                            val isShow = getSharedPreferences(
                                AppUtils.APP_PREFS,
                                MODE_PRIVATE
                            ).getBoolean(AppUtils.ELEMENT_MENU_KEY, true)
                            if (isShow && e.action != MotionEvent.ACTION_MOVE) {
                                showElementPopUp()
                            }
                        }
                    })
            return gestureDetector
        }

        fun createElementScaleDetector(): ScaleGestureDetector {
            val scaleDetector = ScaleGestureDetector(this@CreatorActivity,
                object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        val element =
                            binding.creatorCardLayout.findViewById<View>(elementsList[adapter.currentFocusedElement].id)
                        var scaleFactorX = element.scaleX
                        var scaleFactorY = element.scaleY

                        scaleFactorX *= abs(detector.scaleFactor)
                        scaleFactorY *= abs(detector.scaleFactor)

                        val elementData = elementsList[adapter.currentFocusedElement]


                        when (elementData.currentSelection!!.optionType) {
                            TEXT_ZOOM -> elementsControlsMedium.textZoomSelection()
                            IMAGE_ZOOM -> elementsControlsMedium.imageZoomSelection()
                            BLOCK_ZOOM -> elementsControlsMedium.blockZoomSelection()
                            GRADIENT_ZOOM -> elementsControlsMedium.gradientZoomSelection()
                            PATTERN_ZOOM -> elementsControlsMedium.patternZoomSelection()
                            AUDIO_ZOOM -> elementsControlsMedium.audioZoomSelection()
                        }

                        elementsControlsMedium.updateElementZoomX(
                            elementsList[adapter.currentFocusedElement],
                            scaleFactorX
                        )
                        elementsControlsMedium.updateElementZoomY(
                            elementsList[adapter.currentFocusedElement],
                            scaleFactorY
                        )
                        return true
                    }
                })
            return scaleDetector
        }

    }

    inner class ElementsControlsMedium : AppUtils.ElementOptionsControlsListener {
        init {

        }

        override fun controlsInFocus() {
            binding.elementOptionsSheet.backgroundTintList =
                ColorStateList.valueOf(Color.TRANSPARENT)
            binding.elementOptionsRecy.visibility = View.INVISIBLE
        }

        override fun controlsNotInFocus() {
            binding.elementOptionsSheet.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this@CreatorActivity,
                        R.color.deepPurpleLight
                    )
                )
            binding.elementOptionsRecy.visibility = View.VISIBLE
        }


        override fun onMainWidthUpdate(newWidth: Int) {
            updateMainCardWidth(newWidth)
        }

        override fun onMainHeightUpdate(newHeight: Int) {
            updateMainCardHeight(newHeight)
        }

        override fun onMainZoomXUpdate(zoomX: Float) {
            updateMainCardZoomX(zoomX)
        }

        override fun onMainZoomYUpdate(zoomY: Float) {
            updateMainCardZoomY(zoomY)
        }

        override fun onMainStrokeWidthUpdate(newWidth: Int) {
            updateMainCardStrokeWidth(newWidth)
        }

        override fun onMainStrokeColorUpdate(newColor: Int) {
            updateMainCardStrokeColor(newColor)

            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        override fun onMainCornersUpdate(newCorners: Int) {
            updateMainCardCorners(newCorners)
        }

        override fun onMainRotateUpdate(newValue: Int) {
            updateMainCardRotation(newValue)
        }

        override fun onMainColorUpdate(newColor: Int) {
            updateMainCardColor(newColor)

            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        override fun onMainGradientColorsUpdate(newColors: MutableList<String>) {
            updateMainCardGradColors(newColors)
        }

        override fun onMainGradientAngleUpdate(newAngle: Int) {
            updateMainCardGradAngle(newAngle)
        }

        override fun onMainForegroundTypeUpdate(newType: String) {
            updateMainCardForegroundType(newType)
        }


        override fun onTextUpdate(newText: String) {
            updateTextInput(elementsList[adapter.currentFocusedElement], newText.toString())
        }

        override fun onTextColorUpdate(newColor: Int) {
            val elementData = elementsList[adapter.currentFocusedElement]
            updateTextColor(elementData, newColor)

            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        override fun onTextSizeUpdate(newSize: Int) {
            val elementData = elementsList[adapter.currentFocusedElement]

            updateTextSize(elementData, newSize)
        }

        override fun onTextFontUpdate(newFont: FontData) {
            val elementData = elementsList[adapter.currentFocusedElement]

            updateTextFont(elementData, newFont)
        }

        override fun onTextDepthRadiusUpdate(newRadius: Int) {
            val elementData = elementsList[adapter.currentFocusedElement]
            updateTextDepthRadius(elementData, newRadius)
        }

        override fun onTextDepthVertUpdate(newValue: Int) {
            val elementData = elementsList[adapter.currentFocusedElement]
            updateTextDepthVertical(elementData, newValue)
        }

        override fun onTextDepthHorzUpdate(newValue: Int) {
            val elementData = elementsList[adapter.currentFocusedElement]
            updateTextDepthHorizontal(elementData, newValue)
        }

        override fun onTextDepthColorUpdate(newColor: Int) {
            val elementData = elementsList[adapter.currentFocusedElement]
            updateTextDepthColor(elementData, newColor)

            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        override fun onImageUpdate(newUri: String) {
            val elementData = elementsList[adapter.currentFocusedElement]

            CoroutineScope(Dispatchers.IO).launch {
                val inputStream = contentResolver.openInputStream(Uri.parse(newUri))
                val bit = BitmapFactory.decodeStream(inputStream)
                withContext(Dispatchers.Main) {
                    updateImageInput(
                        elementData,
                        newUri,
                        bit
                    )
                }
            }

            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        override fun onImageCropUpdate(croppedImage: Bitmap) {
            val elementData = elementsList[adapter.currentFocusedElement]
            updateImageCrop(elementData, croppedImage)

            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        override fun onImageCompressUpdate(newValue: Int) {
            updateImageCompression(elementsList[adapter.currentFocusedElement], newValue)
        }

        override fun onImageFilterUpdate(newFilter: ImageFilterData) {
            val elementData = elementsList[adapter.currentFocusedElement]
            updateImageFilter(elementData, newFilter)
        }

        override fun onImageScaleTypeUpdate(newScaleType: ImageScaleTypeData) {
            val elementData = elementsList[adapter.currentFocusedElement]
            updateImageScaleType(elementData, newScaleType)
        }


        override fun onBlockColorUpdate(newColor: Int) {
            val elementData = elementsList[adapter.currentFocusedElement]
            updateBlockColor(elementData, newColor)

            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        override fun onGradientColorsUpdate(newColors: MutableList<String>) {
            updateGradientColors(elementsList[adapter.currentFocusedElement], newColors)

        }

        override fun onGradientAngleUpdate(newAngle: Int) {
            updateGradientAngle(elementsList[adapter.currentFocusedElement], newAngle)
        }

        override fun onGradientShapeUpdate(newShape: String) {
        }


        override fun onPatternDesignUpdate(newPattern: PatternData, patternsFor: String) {
            when (patternsFor) {
                "element" -> {
                    updatePatternDesign(elementsList[adapter.currentFocusedElement], newPattern)
                }

                "main" -> {
                    updateMainCardPattern(newPattern)
                }
            }

            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        override fun onPatternShapeUpdate(newShape: String) {
        }


        override fun onAudioInputUpdate(newUri: Uri) {


            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        override fun onElementRotationUpdate(newRotation: Int) {
            updateElementRotation(elementsList[adapter.currentFocusedElement], newRotation)
        }

        override fun onElementWidthUpdate(newWidth: Int) {
            updateElementSizeWidth(elementsList[adapter.currentFocusedElement], newWidth)
        }

        override fun onElementHeightUpdate(newHeight: Int) {
            updateElementSizeHeight(elementsList[adapter.currentFocusedElement], newHeight)
        }

        override fun onElementZoomXUpdate(zoomX: Float) {
            updateElementZoomX(elementsList[adapter.currentFocusedElement], zoomX)
        }

        override fun onElementZoomYUpdate(zoomY: Float) {
            updateElementZoomY(elementsList[adapter.currentFocusedElement], zoomY)
        }

        override fun onElementCornerUpdate(newCorners: Int) {
            updateElementCorners(elementsList[adapter.currentFocusedElement], newCorners)
        }

        override fun onElementShadowRadiusUpdate(newRadius: Int) {
            updateElementShadowRadius(elementsList[adapter.currentFocusedElement], newRadius)
        }

        override fun onElementStrokeWidthUpdate(newWidth: Int) {
            updateElementStrokeWidth(elementsList[adapter.currentFocusedElement], newWidth)
        }

        override fun onElementStrokeColorUpdate(newColor: Int) {
            updateElementStrokeColour(elementsList[adapter.currentFocusedElement], newColor)

            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        override fun onElementBackgroundPaddingUpdate(newValue: Int) {
            updateElementBackgroundPadding(elementsList[adapter.currentFocusedElement], newValue)
        }

        override fun onElementBackgroundCornersUpdate(newCorners: Int) {
            updateElementBackgroundCorners(elementsList[adapter.currentFocusedElement], newCorners)
        }

        override fun onElementBackgroundColorUpdate(newColor: Int) {
            updateElementBackgroundColor(elementsList[adapter.currentFocusedElement], newColor)

            //Collapse the controls sheet.
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        fun handleMainOptionSelection(selection: ElementOptionData) {
            when (selection.optionType) {
                AppUtils.MAIN_SIZE -> {
                    //Update the UI.
                    mainSizeSelection()
                }

                AppUtils.MAIN_ZOOM -> {
                    //Update the UI.
                    mainZoomSelection()
                }

                AppUtils.MAIN_CORNERS -> {
                    //Update the UI.
                    mainCornersSelection()
                }

                AppUtils.MAIN_STROKE -> {
                    //Update the UI.
                    mainStrokeSelection()
                }

                AppUtils.MAIN_ROTATION -> {
                    //Update the UI.
                    mainRotateSelection()
                }

                AppUtils.MAIN_FOREGROUND -> {
                    //Update the UI.
                    mainForegroundSelection()
                }
            }

            //Update current selection value.
            CURRENT_SELECTED_MAIN_OPTION = selection
        }

        fun handleTextOptionSelection(selection: ElementOptionData) {
            when (selection.optionType) {
                TEXT_INPUT -> {
                    //Update the UI.
                    textInputSelection()
                }

                TEXT_COLOR -> {
                    //Update the UI.
                    textColorSelection()
                }

                TEXT_SIZE -> {
                    //Update the UI.
                    textSizeSelection()
                }

                TEXT_ZOOM -> {
                    //Update the UI.
                    textZoomSelection()
                }

                TEXT_FONT -> {
                    //Update the UI.
                    textFontSelection()
                }

                TEXT_ROTATE -> {
                    textRotateSelection()
                }

                TEXT_DEPTH -> {
                    textDepthSelection()
                }

                TEXT_BACKGROUND -> {
                    textBackgroundSelection()
                }
            }

            //Update current selection value.
            elementsList[adapter.currentFocusedElement].currentSelection = selection
        }

        fun handleImageOptionSelection(selection: ElementOptionData) {
            when (selection.optionType) {
                IMAGE_INPUT -> {
                    imageInputSelection()
                }

                IMAGE_CROP -> {
                    imageCropSelection()
                }

                IMAGE_COMPRESSION -> {
                    imageCompressSelection()
                }

                IMAGE_FILTERS -> {
                    imageFilterSelection()
                }

                IMAGE_ROTATION -> {
                    imageRotationSelection()
                }

                IMAGE_CORNERS -> {
                    imageCornersOption()
                }

                IMAGE_SCALE_TYPE -> {
                    imageScaleTypeOption()
                }

                IMAGE_SIZE -> {
                    imageSizeSelection()
                }

                IMAGE_ZOOM -> {
                    imageZoomSelection()
                }

                IMAGE_BACKGROUND -> {
                    imageBackgroundSelection()
                }
            }

            //Update current selection value.
            elementsList[adapter.currentFocusedElement].currentSelection = selection
        }

        fun handleBlockOptionSelection(selection: ElementOptionData) {
            when (selection.optionType) {
                BLOCK_COLOR -> {
                    blockColorSelection()
                }

                BLOCK_SIZE -> {
                    blockSizeSelection()
                }

                BLOCK_ZOOM -> {
                    blockZoomSelection()
                }

                BLOCK_ROTATION -> {
                    blockRotationSelection()
                }

                BLOCK_STROKE -> {
                    blockStrokeSelection()
                }

                BLOCK_CORNERS -> {
                    blockCornersSelection()
                }

                BLOCK_SHADOW -> {
                    blockShadowSelection()
                }

                BLOCK_BACKGROUND -> {
                    blockBackgroundSelection()
                }
            }

            //Update current selection value.
            elementsList[adapter.currentFocusedElement].currentSelection = selection
        }

        fun handleGradientOptionSelection(selection: ElementOptionData) {
            when (selection.optionType) {
                GRADIENT_COLORS -> {
                    gradientColorsSelection()
                }

                GRADIENT_SIZE -> {
                    gradientSizeSelection()
                }

                GRADIENT_ZOOM -> {
                    gradientZoomSelection()
                }

                GRADIENT_CORNERS -> {
                    gradientCornersSelection()
                }

                GRADIENT_SHADOW -> {
                    gradientShadowSelection()
                }

                GRADIENT_ROTATION -> {
                    gradientRotationSelection()
                }

                GRADIENT_STROKE -> {
                    gradientStrokeSelection()
                }

                GRADIENT_BACKGROUND -> {
                    gradientBackgroundSelection()
                }
            }


            //Update current selection value.
            elementsList[adapter.currentFocusedElement].currentSelection = selection
        }

        fun handlePatternOptionSelection(selection: ElementOptionData) {
            when (selection.optionType) {
                PATTERN_DESIGN -> {
                    patternDesignSelection()
                }

                PATTERN_SIZE -> {
                    patternSizeSelection()
                }

                PATTERN_ZOOM -> {
                    patternZoomSelection()
                }

                PATTERN_ROTATION -> {
                    patternRotationSelection()
                }

                PATTERN_CORNERS -> {
                    patternCornersSelection()
                }

                PATTERN_STROKE -> {
                    patternStrokeSelection()
                }

                PATTERN_SHADOW -> {
                    patternShadowSelection()
                }

                PATTERN_BACKGROUND -> {
                    patternBackgroundSelection()
                }
            }

            //Update current selection value.
            elementsList[adapter.currentFocusedElement].currentSelection = selection
        }

        fun handleAudioOptionSelection(selection: ElementOptionData) {
            when (selection.optionType) {
                AUDIO_INPUT -> {
                    audioInputSelection()
                }

                AUDIO_SIZE -> {
                    audioSizeSelection()
                }

                AUDIO_ZOOM -> {
                    audioZoomSelection()
                }

                AUDIO_ROTATION -> {
                    audioRotationSelection()
                }

                AUDIO_STROKE -> {
                    audioStrokeSelection()
                }

                AUDIO_CORNERS -> {
                    audioCornersSelection()
                }

                AUDIO_SHADOW -> {
                    audioShadowSelection()
                }

                AUDIO_BACKGROUND -> {
                    audioBackgroundSelection()
                }
            }

            //Update current selection value.
            elementsList[adapter.currentFocusedElement].currentSelection = selection
        }


        //Selection functions here...
        //Main selection functions.
        fun mainSizeSelection() {
            val mainSizeControlFragment =
                MainSizeControlFragment(this, mainCardData)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, mainSizeControlFragment)
                .runOnCommit { mainSizeControlFragment.setCurrentMainSize(mainCardData) }
                .commit()
        }

        fun mainZoomSelection() {
            val mainZoomControlsFragment =
                MainZoomControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, mainZoomControlsFragment)
                .runOnCommit {
                    mainZoomControlsFragment.setCurrentZoom(
                        mainCardData.scaleX,
                        mainCardData.scaleY
                    )
                }
                .commit()
        }

        fun mainCornersSelection() {

            val mainCornersControlsFragment = MainCornersControlFragment(this)


            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, mainCornersControlsFragment)
                .runOnCommit { mainCornersControlsFragment.setCurrentMainCorners(mainCardData) }
                .commit()
        }

        fun mainStrokeSelection() {
            val mainStrokeControlsFragment = MainStrokeControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, mainStrokeControlsFragment)
                .runOnCommit { mainStrokeControlsFragment.setCurrentMainStroke(mainCardData) }
                .commit()
        }

        fun mainRotateSelection() {
            val mainRotationControlFragment = MainRotationControlFragment(this)


            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, mainRotationControlFragment)
                .runOnCommit { mainRotationControlFragment.setCurrentMainRotation(mainCardData) }
                .commit()
        }

        fun mainForegroundSelection() {
            val mainForegroundControlsFragment =
                MainForegroundControlsFragment(this)


            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, mainForegroundControlsFragment)
                .runOnCommit { mainForegroundControlsFragment.setCurrentMainForeground(mainCardData) }
                .commit()

            //Update the UI card foreground.
            updateMainCardForegroundType(mainCardData.foregroundType)
        }


        //Text selection functions.
        fun textInputSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val textInputFragment = TextInputFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, textInputFragment)
                .runOnCommit { textInputFragment.setCurrentText((elementData.data as TextData).text) }
                .commit()

        }

        fun textColorSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val textColorControlFragment = TextColorControlFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, textColorControlFragment)
                .runOnCommit { textColorControlFragment.setCurrentColor(elementData.data as TextData) }
                .commit()
        }

        fun textSizeSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val textSizeControlsFragment = TextSizeControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, textSizeControlsFragment)
                .runOnCommit {
                    textSizeControlsFragment.setCurrentSize(elementData.data as TextData)
                }.commit()
        }

        fun textZoomSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val textData = elementData.data as TextData

            val zoomControlsFragment = ElementZoomControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, zoomControlsFragment).runOnCommit {
                    zoomControlsFragment.setCurrentZoom(textData.scaleX, textData.scaleY)
                }.commit()
        }

        fun textFontSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val textFontControlFragment = TextFontControlFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, textFontControlFragment).runOnCommit {
                    textFontControlFragment.setCurrentTextFont(elementData)
                }.commit()
        }

        fun textRotateSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val elementRotationControlsFragment = ElementRotationControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementRotationControlsFragment)
                .runOnCommit { elementRotationControlsFragment.setCurrentRotation((elementData.data as TextData).rotation) }
                .commit()
        }

        fun textDepthSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val textDepthControlsFragment = TextDepthControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, textDepthControlsFragment)
                .runOnCommit { textDepthControlsFragment.setCurrentDepth(elementData.data as TextData) }
                .commit()

        }

        fun textBackgroundSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val textData = elementData.data as TextData

            val elementBackgroundControlsFragment = ElementBackgroundControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementBackgroundControlsFragment)
                .runOnCommit {
                    elementBackgroundControlsFragment.setCurrentBackground(
                        textData.backgroundPadding,
                        textData.backgroundCorners,
                        textData.backgroundColor
                    )
                }
                .commit()
        }


        //Image selection functions.
        fun imageInputSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val imageInputControlsFragment =
                ImageInputControlsFragment(this, imageLauncher)


            for (data in tempImageList) {
                if (data.elementId == elementData.id) {
                    //Update the image controls container.
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.elementOptionControlsContainer, imageInputControlsFragment)
                        .runOnCommit {
                            imageInputControlsFragment.setCurrentImageInput(
                                data,
                                this@CreatorActivity
                            )
                        }.commit()
                    break
                }
            }

        }

        fun imageCropSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val imageCropControlsFragment = ImageCropControlsFragment(this)


            for (data in tempImageList) {
                if (data.elementId == elementData.id) {
                    //Update the image controls container.
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.elementOptionControlsContainer, imageCropControlsFragment)
                        .runOnCommit { imageCropControlsFragment.setCurrentImageCrop(data) }
                        .commit()
                    break
                }
            }
        }

        fun imageCompressSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val imageCompressionControls = ImageCompressionControls(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, imageCompressionControls)
                .runOnCommit { imageCompressionControls.setCurrentCompression(elementData) }
                .commit()
        }

        fun imageFilterSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val imageFiltersControlFragment =
                ImageFiltersControlFragment(this, imageFilters)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, imageFiltersControlFragment)
                .runOnCommit { imageFiltersControlFragment.setCurrentFilter(elementData) }
                .commit()
        }

        fun imageRotationSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val elementRotationControlsFragment = ElementRotationControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementRotationControlsFragment)
                .runOnCommit { elementRotationControlsFragment.setCurrentRotation((elementData.data as ImageData).rotation) }
                .commit()
        }

        fun imageCornersOption() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val elementCornerControlsBinding = ElementCornerControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementCornerControlsBinding)
                .runOnCommit { elementCornerControlsBinding.setCurrentCorners((elementData.data as ImageData).corners) }
                .commit()
        }

        fun imageScaleTypeOption() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val imageScaleTypeControlsFragment =
                ImageScaleTypeControlsFragment(this, scaleTypeList)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, imageScaleTypeControlsFragment)
                .runOnCommit { imageScaleTypeControlsFragment.setCurrentScaleType(elementData.data as ImageData) }
                .commit()
        }

        fun imageSizeSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val data = elementData.data as ImageData

            val elementSizeControlsFragment = ElementSizeControlsFragment(this)


            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementSizeControlsFragment)
                .runOnCommit { elementSizeControlsFragment.setCurrentSize(data.width, data.height) }
                .commit()
        }

        fun imageZoomSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val imageData = elementData.data as ImageData

            val zoomControlsFragment = ElementZoomControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, zoomControlsFragment).runOnCommit {
                    zoomControlsFragment.setCurrentZoom(imageData.scaleX, imageData.scaleY)
                }.commit()
        }

        fun imageBackgroundSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val imageData = elementData.data as ImageData

            val elementBackgroundControlsFragment = ElementBackgroundControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementBackgroundControlsFragment)
                .runOnCommit {
                    elementBackgroundControlsFragment.setCurrentBackground(
                        imageData.backgroundPadding,
                        imageData.backgroundCorners,
                        imageData.backgroundColor
                    )
                }
                .commit()

        }


        //Block selection functions.
        fun blockColorSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val blockColorControlsFragment = BlockColorControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, blockColorControlsFragment)
                .runOnCommit { blockColorControlsFragment.setCurrentBlockColor(elementData) }
                .commit()
        }

        fun blockSizeSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val data = elementData.data as BlockData

            val elementSizeControlsFragment = ElementSizeControlsFragment(this)


            //Update the block controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementSizeControlsFragment)
                .runOnCommit { elementSizeControlsFragment.setCurrentSize(data.width, data.height) }
                .commit()
        }

        fun blockZoomSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val blockData = elementData.data as BlockData

            val zoomControlsFragment = ElementZoomControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, zoomControlsFragment).runOnCommit {
                    zoomControlsFragment.setCurrentZoom(blockData.scaleX, blockData.scaleY)
                }.commit()
        }

        fun blockRotationSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val blockData = elementData.data as BlockData

            val elementRotationControlsFragment = ElementRotationControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementRotationControlsFragment)
                .runOnCommit { elementRotationControlsFragment.setCurrentRotation(blockData.rotation) }
                .commit()
        }

        fun blockStrokeSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val blockData = elementData.data as BlockData

            val elementStrokeControlsFragment = ElementStrokeControlsFragment(this)

            //Update the block controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementStrokeControlsFragment)
                .runOnCommit {
                    elementStrokeControlsFragment.setCurrentStroke(
                        blockData.strokeWidth,
                        blockData.strokeColor
                    )
                }.commit()
        }

        fun blockCornersSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val elementCornerControlsBinding = ElementCornerControlsFragment(this)

            //Update the block controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementCornerControlsBinding)
                .runOnCommit { elementCornerControlsBinding.setCurrentCorners((elementData.data as BlockData).corners) }
                .commit()
        }

        fun blockShadowSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val elementShadowControlsFragment = ElementShadowControlsFragment(this)

            //Update the block controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementShadowControlsFragment)
                .runOnCommit { elementShadowControlsFragment.setCurrentShadow((elementData.data as BlockData).shadowRadius) }
                .commit()
        }

        fun blockBackgroundSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val blockData = elementData.data as BlockData

            val elementBackgroundControlsFragment = ElementBackgroundControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementBackgroundControlsFragment)
                .runOnCommit {
                    elementBackgroundControlsFragment.setCurrentBackground(
                        blockData.backgroundPadding,
                        blockData.backgroundCorners,
                        blockData.backgroundColor
                    )
                }
                .commit()
        }


        //Gradient selection functions.
        fun gradientColorsSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val gradientColorsControlFragment = GradientControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, gradientColorsControlFragment)
                .runOnCommit { gradientColorsControlFragment.setCurrentGradientColors(elementData) }
                .commit()
        }

        fun gradientSizeSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val data = elementData.data as GradientData

            val elementSizeControlsFragment = ElementSizeControlsFragment(this)

            //Update the gradient controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementSizeControlsFragment)
                .runOnCommit { elementSizeControlsFragment.setCurrentSize(data.width, data.height) }
                .commit()
        }

        fun gradientZoomSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val gradientData = elementData.data as GradientData

            val zoomControlsFragment = ElementZoomControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, zoomControlsFragment).runOnCommit {
                    zoomControlsFragment.setCurrentZoom(gradientData.scaleX, gradientData.scaleY)
                }.commit()
        }

        fun gradientCornersSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val elementCornerControlsBinding = ElementCornerControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementCornerControlsBinding)
                .runOnCommit { elementCornerControlsBinding.setCurrentCorners((elementData.data as GradientData).corners) }
                .commit()
        }

        fun gradientShadowSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val elementShadowControlsFragment = ElementShadowControlsFragment(this)

            //Update the gradient controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementShadowControlsFragment)
                .runOnCommit { elementShadowControlsFragment.setCurrentShadow((elementData.data as GradientData).shadowRadius) }
                .commit()
        }

        fun gradientRotationSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val gradientData = elementData.data as GradientData

            val elementRotationControlsFragment = ElementRotationControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementRotationControlsFragment)
                .runOnCommit { elementRotationControlsFragment.setCurrentRotation(gradientData.rotation) }
                .commit()
        }

        fun gradientStrokeSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val gradientData = elementData.data as GradientData

            val elementStrokeControlsFragment = ElementStrokeControlsFragment(this)

            //Update the block controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementStrokeControlsFragment)
                .runOnCommit {
                    elementStrokeControlsFragment.setCurrentStroke(
                        gradientData.strokeWidth,
                        gradientData.strokeColor
                    )
                }.commit()
        }

        fun gradientBackgroundSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val gradientData = elementData.data as GradientData

            val elementBackgroundControlsFragment = ElementBackgroundControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementBackgroundControlsFragment)
                .runOnCommit {
                    elementBackgroundControlsFragment.setCurrentBackground(
                        gradientData.backgroundPadding,
                        gradientData.backgroundCorners,
                        gradientData.backgroundColor
                    )
                }
                .commit()
        }


        //Pattern selection functions.
        fun patternDesignSelection() {
            val patternDesignsFragment = PatternDesignsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, patternDesignsFragment)
                .runOnCommit {
                    patternDesignsFragment.setCurrentPatternDesign(elementsList[adapter.currentFocusedElement])
                }
                .commit()
        }

        fun patternSizeSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val data = elementData.data as PatternElementData

            val elementSizeControlsFragment = ElementSizeControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementSizeControlsFragment)
                .runOnCommit { elementSizeControlsFragment.setCurrentSize(data.width, data.height) }
                .commit()
        }

        fun patternZoomSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val patternData = elementData.data as PatternElementData

            val zoomControlsFragment = ElementZoomControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, zoomControlsFragment).runOnCommit {
                    zoomControlsFragment.setCurrentZoom(patternData.scaleX, patternData.scaleY)
                }.commit()
        }

        fun patternRotationSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val patternElementData = elementData.data as PatternElementData

            val elementRotationControlsFragment = ElementRotationControlsFragment(this)


            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementRotationControlsFragment)
                .runOnCommit {
                    elementRotationControlsFragment.setCurrentRotation(patternElementData.rotation)
                }
                .commit()
        }

        fun patternCornersSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val elementCornerControlsBinding = ElementCornerControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementCornerControlsBinding)
                .runOnCommit { elementCornerControlsBinding.setCurrentCorners((elementData.data as PatternElementData).corners) }
                .commit()
        }

        fun patternStrokeSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val patternElementData = elementData.data as PatternElementData

            val elementStrokeControlsFragment = ElementStrokeControlsFragment(this)

            //Update the block controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementStrokeControlsFragment)
                .runOnCommit {
                    elementStrokeControlsFragment.setCurrentStroke(
                        patternElementData.strokeWidth,
                        patternElementData.strokeColor
                    )
                }.commit()
        }

        fun patternShadowSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val elementShadowControlsFragment = ElementShadowControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementShadowControlsFragment)
                .runOnCommit { elementShadowControlsFragment.setCurrentShadow((elementData.data as PatternElementData).shadowRadius) }
                .commit()
        }

        fun patternBackgroundSelection() {
            //Update the image controls container.
            val elementData = elementsList[adapter.currentFocusedElement]
            val patternElementData = elementData.data as PatternElementData

            val elementBackgroundControlsFragment = ElementBackgroundControlsFragment(this)

            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementBackgroundControlsFragment)
                .runOnCommit {
                    elementBackgroundControlsFragment.setCurrentBackground(
                        patternElementData.backgroundPadding,
                        patternElementData.backgroundCorners,
                        patternElementData.backgroundColor
                    )
                }
                .commit()
        }

        //Audio selection functions.
        fun audioInputSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val audioInputControlsFragment = AudioInputControlsFragment(this, audioLauncher)

            val playerController =
                binding.creatorCardLayout.findViewById<FrameLayout>(elementData.id)
                    .findViewById<PlayerControlView>(R.id.creatorAudioElementPlayer)
            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, audioInputControlsFragment)
                .runOnCommit {
                    audioInputControlsFragment.setCurrentAudioInput(elementData, playerController)
                }
                .commit()
        }

        fun audioSizeSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val data = elementData.data as AudioData

            val elementSizeControlsFragment = ElementSizeControlsFragment(this)


            //Update the block controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementSizeControlsFragment)
                .runOnCommit { elementSizeControlsFragment.setCurrentSize(data.width, data.height) }
                .commit()
        }

        fun audioZoomSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val audioData = elementData.data as AudioData

            val zoomControlsFragment = ElementZoomControlsFragment(this)

            //Update the text controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, zoomControlsFragment).runOnCommit {
                    zoomControlsFragment.setCurrentZoom(audioData.scaleX, audioData.scaleY)
                }.commit()
        }

        fun audioRotationSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val audioData = elementData.data as AudioData

            val elementRotationControlsFragment = ElementRotationControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementRotationControlsFragment)
                .runOnCommit { elementRotationControlsFragment.setCurrentRotation(audioData.rotation) }
                .commit()
        }

        fun audioStrokeSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val audioData = elementData.data as AudioData

            val elementStrokeControlsFragment = ElementStrokeControlsFragment(this)

            //Update the block controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementStrokeControlsFragment)
                .runOnCommit {
                    elementStrokeControlsFragment.setCurrentStroke(
                        audioData.strokeWidth,
                        audioData.strokeColor
                    )
                }.commit()
        }

        fun audioCornersSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val elementCornerControlsBinding = ElementCornerControlsFragment(this)

            //Update the block controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementCornerControlsBinding)
                .runOnCommit { elementCornerControlsBinding.setCurrentCorners((elementData.data as AudioData).corners) }
                .commit()

            AppUtils.PREMIUM_USERS_COLLECTION
        }


        fun audioShadowSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]

            val elementShadowControlsFragment = ElementShadowControlsFragment(this)

            //Update the block controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementShadowControlsFragment)
                .runOnCommit { elementShadowControlsFragment.setCurrentShadow((elementData.data as AudioData).shadowRadius) }
                .commit()
        }

        fun audioBackgroundSelection() {
            val elementData = elementsList[adapter.currentFocusedElement]
            val audioData = elementData.data as AudioData

            val elementBackgroundControlsFragment = ElementBackgroundControlsFragment(this)

            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(R.id.elementOptionControlsContainer, elementBackgroundControlsFragment)
                .runOnCommit {
                    elementBackgroundControlsFragment.setCurrentBackground(
                        audioData.backgroundPadding,
                        audioData.backgroundCorners,
                        audioData.backgroundColor
                    )
                }
                .commit()
        }


        //Main card UI update functions here...
        fun updateMainCardWidth(newWidth: Int) {
            val originalWidth = mainCardData.width
            //Update the data.
            mainCardData.width = newWidth

            //Update the UI.
            binding.creatorCard.layoutParams.width = newWidth
            binding.creatorCard.invalidate()
            binding.creatorCard.requestLayout()

            //Update the elements position.
            for (element in elementsList) {
                val view = binding.creatorCardLayout.findViewById<View>(element.id)
                if (view != null) {
                    view.x = element.xPosition
                    view.y = element.yPosition
                }
            }

            if (mainCardData.foregroundType == AppUtils.MAIN_FOREGROUND_PATTERN) {
                if (mainCardData.patternData != null) {
                    updateMainCardPattern(mainCardData.patternData!!)
                }
            }

        }

        fun updateMainCardHeight(newHeight: Int) {
            val originalHeight = mainCardData.height
            //Update the data.
            mainCardData.height = newHeight

            //Update the UI.
            binding.creatorCard.layoutParams.height = newHeight
            binding.creatorCard.invalidate()
            binding.creatorCard.requestLayout()

            //Update the elements position.
            for (element in elementsList) {
                val view = binding.creatorCardLayout.findViewById<View>(element.id)
                if (view != null) {
                    view.x = element.xPosition
                    view.y = element.yPosition
                }
            }

            if (mainCardData.foregroundType == AppUtils.MAIN_FOREGROUND_PATTERN) {
                if (mainCardData.patternData != null) {
                    updateMainCardPattern(mainCardData.patternData!!)
                }
            }
        }

        fun updateMainCardZoomX(zoomX: Float) {
            //Update the data.
            mainCardData.scaleX = zoomX

            //Update the UI.
            binding.creatorCard.scaleX = zoomX
        }

        fun updateMainCardZoomY(zoomY: Float) {
            //Update the data.
            mainCardData.scaleY = zoomY

            //Update the UI.
            binding.creatorCard.scaleY = zoomY
        }

        fun updateMainCardCorners(newCorners: Int) {
            //Update the data.
            mainCardData.cornerRadius = newCorners

            //Update the UI.
            binding.creatorCard.radius = AppUtils.pxToDp(this@CreatorActivity, newCorners).toFloat()
        }

        fun updateMainCardStrokeWidth(newWidth: Int) {
            //Update the data.
            mainCardData.strokeWidth = newWidth

            //Update the UI.
            binding.creatorCard.strokeWidth = AppUtils.pxToDp(this@CreatorActivity, newWidth)


        }

        fun updateMainCardStrokeColor(newColor: Int) {
            //Update the data.
            mainCardData.strokeColor = AppUtils.intToHex(newColor)

            //Update the UI.
            binding.creatorCard.strokeColor = newColor
        }

        fun updateMainCardRotation(newValue: Int) {
            //Update the data.
            mainCardData.rotation = newValue

            //Update the UI.
            binding.creatorCard.rotation = newValue.toFloat()
        }

        fun updateMainCardColor(newColor: Int) {
            //Update the data.
            mainCardData.color = AppUtils.intToHex(newColor)

            //Update the UI.
            binding.creatorCard.setCardBackgroundColor(newColor)
        }

        fun updateMainCardGradColors(newColors: MutableList<String>) {
            //Update the data.
            mainCardData.gradientColors = newColors

            val intColorArray = intArrayOf(
                Color.parseColor(mainCardData.gradientColors!![0]),
                Color.parseColor(mainCardData.gradientColors!![1])
            )

            //Update the UI.
            val gradient = GradientDrawable()
            gradient.colors = intColorArray
            when (mainCardData.gradientAngle) {
                0 -> {
                    gradient.orientation = GradientDrawable.Orientation.LEFT_RIGHT
                }

                45 -> {
                    gradient.orientation = GradientDrawable.Orientation.TL_BR
                }

                90 -> {
                    gradient.orientation = GradientDrawable.Orientation.TOP_BOTTOM
                }

                135 -> {
                    gradient.orientation = GradientDrawable.Orientation.TR_BL
                }

                180 -> {
                    gradient.orientation = GradientDrawable.Orientation.RIGHT_LEFT
                }

                225 -> {
                    gradient.orientation = GradientDrawable.Orientation.BR_TL
                }

                270 -> {
                    gradient.orientation = GradientDrawable.Orientation.BOTTOM_TOP
                }

                315 -> {
                    gradient.orientation = GradientDrawable.Orientation.BL_TR
                }
            }

            binding.creatorCardLayout.background = gradient
        }

        fun updateMainCardGradAngle(newAngle: Int) {
            //Update the data.
            mainCardData.gradientAngle = newAngle

            val intColorArray = intArrayOf(
                Color.parseColor(mainCardData.gradientColors!![0]),
                Color.parseColor(mainCardData.gradientColors!![1])
            )

            //Update the UI.
            val gradient = GradientDrawable()
            gradient.colors = intColorArray
            when (mainCardData.gradientAngle) {
                0 -> {
                    gradient.orientation = GradientDrawable.Orientation.LEFT_RIGHT
                }

                45 -> {
                    gradient.orientation = GradientDrawable.Orientation.TL_BR
                }

                90 -> {
                    gradient.orientation = GradientDrawable.Orientation.TOP_BOTTOM
                }

                135 -> {
                    gradient.orientation = GradientDrawable.Orientation.TR_BL
                }

                180 -> {
                    gradient.orientation = GradientDrawable.Orientation.RIGHT_LEFT
                }

                225 -> {
                    gradient.orientation = GradientDrawable.Orientation.BR_TL
                }

                270 -> {
                    gradient.orientation = GradientDrawable.Orientation.BOTTOM_TOP
                }

                315 -> {
                    gradient.orientation = GradientDrawable.Orientation.BL_TR
                }
            }

            binding.creatorCardLayout.background = gradient
        }

        fun updateMainCardForegroundType(newType: String) {
            //Update the data.
            mainCardData.foregroundType = newType

            //Update the UI.
            when (newType) {
                AppUtils.MAIN_FOREGROUND_COLOR -> {
                    binding.creatorCardLayout.background = ColorDrawable(Color.TRANSPARENT)
                    binding.creatorCardLayout.findViewWithTag<View>("patternView").visibility =
                        View.GONE
                    updateMainCardColor(Color.parseColor(mainCardData.color))
                }

                AppUtils.MAIN_FOREGROUND_GRADIENT -> {
                    binding.creatorCard.setCardBackgroundColor(Color.TRANSPARENT)
                    binding.creatorCardLayout.findViewWithTag<View>("patternView").visibility =
                        View.GONE
                    updateMainCardGradColors(mainCardData.gradientColors!!)
                }

                AppUtils.MAIN_FOREGROUND_PATTERN -> {
                    binding.creatorCard.setCardBackgroundColor(Color.TRANSPARENT)
                    binding.creatorCardLayout.background = ColorDrawable(Color.TRANSPARENT)
                    binding.creatorCardLayout.findViewWithTag<View>("patternView").visibility =
                        View.VISIBLE
                    if (mainCardData.patternData != null) {
                        updateMainCardPattern(mainCardData.patternData!!)
                    }
                }
            }
        }

        fun updateMainCardPattern(newPattern: PatternData) {
            //Update the data.
            mainCardData.patternData = newPattern

            //Update the UI.
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    val params =
                        binding.creatorCardLayout.findViewWithTag<View>("patternView").layoutParams as ConstraintLayout.LayoutParams
                    val view = Patterns(this@CreatorActivity).loadPatternWithViewId(
                        newPattern,
                        mainCardData.width,
                        mainCardData.height
                    )!!
                    view.tag = "patternView"
                    withContext(Dispatchers.Main) {
                        binding.creatorCardLayout.removeView(
                            binding.creatorCardLayout.findViewWithTag<View>(
                                "patternView"
                            )
                        )
                        binding.creatorCardLayout.addView(view, 0, params)
                    }
                }
            }
        }


        //Common element UI update functions here.
        fun updateElementRotation(elementData: ElementData, newRotation: Int) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.TEXT_TYPE -> {
                    val textData = elementData.data as TextData

                    //Update the data.
                    textData.rotation = newRotation
                }

                Elements.IMAGE_TYPE -> {
                    val imageData = elementData.data as ImageData

                    //Update the data.
                    imageData.rotation = newRotation
                }

                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData

                    //Update the data.
                    blockData.rotation = newRotation
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.rotation = newRotation
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.rotation = newRotation
                }

                AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.rotation = newRotation
                }
            }

            //Update the focused element rotation.
            val view = elementsMedium.getElementById(elementData) ?: return
            view.rotation =
                newRotation.toFloat()
        }

        fun updateElementSizeWidth(elementData: ElementData, newWidth: Int) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.IMAGE_TYPE -> {
                    val imageData = elementData.data as ImageData

                    //Update the data.
                    imageData.width = newWidth


                    //Update the focused element width.
                    val view = elementsMedium.getElementById(elementData) as ImageView
                    view.layoutParams.width =
                        AppUtils.pxToDp(this@CreatorActivity, newWidth)
                    view.invalidate()
                    view.requestLayout()

                    //Set the scaleType to adjust the image to new width.
                    updateImageScaleType(elementData, imageData.scaleType!!)
                }

                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData

                    //Update the data.
                    blockData.width = newWidth


                    //Update the focused element width.
                    val view = elementsMedium.getElementById(elementData) ?: return
                    view.layoutParams.width =
                        AppUtils.pxToDp(this@CreatorActivity, newWidth)
                    view.invalidate()
                    view.requestLayout()
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.width = newWidth


                    //Update the focused element width.
                    val view = elementsMedium.getElementById(elementData) ?: return
                    view.layoutParams.width =
                        AppUtils.pxToDp(this@CreatorActivity, newWidth)
                    view.invalidate()
                    view.requestLayout()
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.width = newWidth


                    //Update the focused element width.
                    val view = elementsMedium.getElementById(elementData) ?: return
                    view.layoutParams.width =
                        AppUtils.pxToDp(this@CreatorActivity, newWidth)
                    view.invalidate()
                    view.requestLayout()
                }

                AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.width = newWidth


                    //Update the focused element width.
                    val view = elementsMedium.getElementById(elementData) ?: return
                    view.layoutParams.width =
                        AppUtils.pxToDp(this@CreatorActivity, newWidth)
                    view.invalidate()
                    view.requestLayout()
                }
            }
        }

        fun updateElementSizeHeight(elementData: ElementData, newHeight: Int) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.IMAGE_TYPE -> {
                    val imageData = elementData.data as ImageData

                    //Update the data.
                    imageData.height = newHeight

                    //Update the focused element width.
                    val view = elementsMedium.getElementById(elementData) as ImageView ?: return
                    view.layoutParams.height =
                        AppUtils.pxToDp(this@CreatorActivity, newHeight)
                    view.invalidate()
                    view.requestLayout()

                    //Set the scaleType to adjust the image to new height.
                    updateImageScaleType(elementData, imageData.scaleType!!)
                }

                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData

                    //Update the data.
                    blockData.height = newHeight

                    //Update the focused element width.
                    val view = elementsMedium.getElementById(elementData) ?: return
                    view.layoutParams.height =
                        AppUtils.pxToDp(this@CreatorActivity, newHeight)
                    view.invalidate()
                    view.requestLayout()
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.height = newHeight

                    //Update the focused element width.
                    val view = elementsMedium.getElementById(elementData) ?: return
                    view.layoutParams.height =
                        AppUtils.pxToDp(this@CreatorActivity, newHeight)
                    view.invalidate()
                    view.requestLayout()
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.height = newHeight

                    //Update the focused element width.
                    val view = elementsMedium.getElementById(elementData) ?: return
                    view.layoutParams.height =
                        AppUtils.pxToDp(this@CreatorActivity, newHeight)
                    view.invalidate()
                    view.requestLayout()
                }

                AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.height = newHeight


                    //Update the focused element width.
                    val view = elementsMedium.getElementById(elementData) ?: return
                    view.layoutParams.height =
                        AppUtils.pxToDp(this@CreatorActivity, newHeight)
                    view.invalidate()
                    view.requestLayout()
                }
            }
        }

        fun updateElementZoomX(elementData: ElementData, zoomX: Float) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.TEXT_TYPE -> {
                    val textData = elementData.data as TextData

                    //Update the data.
                    textData.scaleX = zoomX

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleX = zoomX
                }

                Elements.IMAGE_TYPE -> {
                    val imageData = elementData.data as ImageData

                    //Update the data.
                    imageData.scaleX = zoomX

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!

                    view.scaleX = zoomX
                }

                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData


                    //Update the data.
                    blockData.scaleX = zoomX

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleX = zoomX
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.scaleX = zoomX

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleX = zoomX
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.scaleX = zoomX

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleX = zoomX
                }

                Elements.AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.scaleX = zoomX

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleX = zoomX
                }
            }
        }

        fun updateElementZoomY(elementData: ElementData, zoomY: Float) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.TEXT_TYPE -> {
                    val textData = elementData.data as TextData

                    //Update the data.
                    textData.scaleY = zoomY

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleY = zoomY
                }

                Elements.IMAGE_TYPE -> {
                    val imageData = elementData.data as ImageData

                    //Update the data.
                    imageData.scaleY = zoomY

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleY = zoomY

                }

                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData


                    //Update the data.
                    blockData.scaleY = zoomY

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleY = zoomY
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.scaleY = zoomY

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleY = zoomY
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.scaleY = zoomY

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleY = zoomY
                }

                Elements.AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.scaleY = zoomY

                    //Update the UI.
                    val view = elementsMedium.getElementById(elementData)!!
                    view.scaleY = zoomY
                }
            }
        }

        fun updateElementCorners(elementData: ElementData, newCorners: Int) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.IMAGE_TYPE -> {
                    val imageData = elementData.data as ImageData

                    //Update the data.
                    imageData.corners = newCorners

                    //Update the focused element corners.
                    val view = elementsMedium.getElementById(elementData) as RoundedImageView
                    view.cornerRadius = AppUtils.pxToDp(this@CreatorActivity, newCorners).toFloat()
                }

                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData

                    //Update the data.
                    blockData.corners = newCorners

                    //Update the focused block corners.
                    val block = elementsMedium.getElementById(elementData) as ImageView
                    val drawable = block.drawable
                    (DrawableCompat.wrap(drawable) as GradientDrawable).cornerRadius =
                        AppUtils.pxToDp(this@CreatorActivity, newCorners).toFloat()
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.corners = newCorners

                    //Update the focused gradient corners.
                    val gradient = elementsMedium.getElementById(elementData) as ImageView
                    val drawable = gradient.drawable
                    (DrawableCompat.wrap(drawable) as GradientDrawable).cornerRadius =
                        AppUtils.pxToDp(this@CreatorActivity, newCorners).toFloat()
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.corners = newCorners

                    //Update the focused pattern corners.
                    binding.creatorCardLayout.findViewById<FrameLayout>(elementData.id)
                        .findViewById<MaterialCardView>(R.id.creatorPatternElementCard).radius =
                        AppUtils.pxToDp(this@CreatorActivity, newCorners).toFloat()
                }

                Elements.AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.corners = newCorners

                    //Update the focused pattern corners.
                    binding.creatorCardLayout.findViewById<FrameLayout>(elementData.id)
                        .findViewById<MaterialCardView>(R.id.creatorAudioElementCard).radius =
                        AppUtils.pxToDp(this@CreatorActivity, newCorners).toFloat()
                }
            }
        }

        fun updateElementShadowRadius(elementData: ElementData, newRadius: Int) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData

                    //Update the data.
                    blockData.shadowRadius = newRadius
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.shadowRadius = newRadius
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.shadowRadius = newRadius
                }

                Elements.AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.shadowRadius = newRadius
                }

            }


            //Update the focused element width.
            val view = elementsMedium.getElementById(elementData) ?: return
            view.elevation =
                AppUtils.pxToDp(this@CreatorActivity, newRadius).toFloat()
        }

        fun updateElementStrokeWidth(elementData: ElementData, newWidth: Int) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData

                    //Update the data.
                    blockData.strokeWidth = newWidth

                    //Update the focused block stroke width.
                    val block = elementsMedium.getElementById(elementData) as ImageView
                    val drawable = block.drawable
                    (DrawableCompat.wrap(drawable) as GradientDrawable).setStroke(
                        AppUtils.pxToDp(
                            this@CreatorActivity,
                            newWidth
                        ), Color.parseColor(blockData.strokeColor)
                    )
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.strokeWidth = newWidth

                    //Update the focused gradient stroke width.
                    val drawable =
                        binding.creatorCardLayout.findViewById<ImageView>(elementData.id).drawable
                    (DrawableCompat.wrap(drawable) as GradientDrawable).setStroke(
                        AppUtils.pxToDp(
                            this@CreatorActivity,
                            newWidth
                        ), Color.parseColor(gradientData.strokeColor)
                    )

                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.strokeWidth = newWidth

                    //Update the focused element stroke width.
                    binding.creatorCardLayout.findViewById<FrameLayout>(elementData.id)
                        .findViewById<MaterialCardView>(R.id.creatorPatternElementCard).strokeWidth =
                        AppUtils.pxToDp(this@CreatorActivity, newWidth)
                }

                Elements.AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.strokeWidth = newWidth

                    //Update the focused element stroke width.
                    binding.creatorCardLayout.findViewById<FrameLayout>(elementData.id)
                        .findViewById<MaterialCardView>(R.id.creatorAudioElementCard).strokeWidth =
                        AppUtils.pxToDp(this@CreatorActivity, newWidth)
                }
            }
        }

        fun updateElementStrokeColour(elementData: ElementData, newColor: Int) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData

                    //Update the data.
                    blockData.strokeColor = AppUtils.intToHex(newColor)

                    //Update the focused block stroke colour.
                    val block = elementsMedium.getElementById(elementData) as ImageView

                    val drawable = block.drawable
                    (DrawableCompat.wrap(drawable) as GradientDrawable).setStroke(
                        AppUtils.pxToDp(
                            this@CreatorActivity,
                            blockData.strokeWidth
                        ), newColor
                    )
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.strokeColor = AppUtils.intToHex(newColor)

                    //Update the focused gradient stroke color.
                    val drawable =
                        binding.creatorCardLayout.findViewById<ImageView>(elementData.id).drawable
                    (DrawableCompat.wrap(drawable) as GradientDrawable).setStroke(
                        AppUtils.pxToDp(
                            this@CreatorActivity,
                            gradientData.strokeWidth
                        ), newColor
                    )
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.strokeColor = AppUtils.intToHex(newColor)

                    //Update the focused element stroke color.
                    binding.creatorCardLayout.findViewById<FrameLayout>(elementData.id)
                        .findViewById<MaterialCardView>(R.id.creatorPatternElementCard).strokeColor =
                        newColor
                }

                Elements.AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.strokeColor = AppUtils.intToHex(newColor)

                    //Update the focused element stroke color.
                    binding.creatorCardLayout.findViewById<FrameLayout>(elementData.id)
                        .findViewById<MaterialCardView>(R.id.creatorAudioElementCard).strokeColor =
                        newColor
                }
            }
        }

        fun updateElementBackgroundPadding(elementData: ElementData, newPadding: Int) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.TEXT_TYPE -> {
                    val textData = elementData.data as TextData

                    //Update the data.
                    textData.backgroundPadding = newPadding
                }

                Elements.IMAGE_TYPE -> {
                    val imageData = elementData.data as ImageData

                    //Update the data.
                    imageData.backgroundPadding = newPadding
                }

                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData

                    //Update the data.
                    blockData.backgroundPadding = newPadding
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.backgroundPadding = newPadding
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.backgroundPadding = newPadding
                }

                Elements.AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.backgroundPadding = newPadding
                }
            }

            //Update the focused element background padding.
            val view = elementsMedium.getElementById(elementData) ?: return

            view.setPadding(AppUtils.pxToDp(this@CreatorActivity, newPadding))

        }

        fun updateElementBackgroundCorners(elementData: ElementData, newCorners: Int) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.TEXT_TYPE -> {
                    val textData = elementData.data as TextData

                    //Update the data.
                    textData.backgroundCorners = newCorners
                }

                Elements.IMAGE_TYPE -> {
                    val imageData = elementData.data as ImageData

                    //Update the data.
                    imageData.backgroundCorners = newCorners
                }

                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData

                    //Update the data.
                    blockData.backgroundCorners = newCorners
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.backgroundCorners = newCorners
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.backgroundCorners = newCorners
                }

                Elements.AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.backgroundCorners = newCorners
                }
            }

            //Update the focused element background corners.
            val view = elementsMedium.getElementById(elementData) ?: return
            val drawable =
                (view.background) as LayerDrawable
            (drawable.getDrawable(1) as GradientDrawable).cornerRadius =
                AppUtils.pxToDp(this@CreatorActivity, newCorners).toFloat()
            view.background = drawable

        }

        fun updateElementBackgroundColor(elementData: ElementData, newColor: Int) {
            //Get the data and update it according to the current element type.
            when (elementData.elementType) {
                Elements.TEXT_TYPE -> {
                    val textData = elementData.data as TextData

                    //Update the data.
                    textData.backgroundColor = AppUtils.intToHex(newColor)
                }

                Elements.IMAGE_TYPE -> {
                    val imageData = elementData.data as ImageData

                    //Update the data.
                    imageData.backgroundColor = AppUtils.intToHex(newColor)
                }

                Elements.BLOCK_TYPE -> {
                    val blockData = elementData.data as BlockData

                    //Update the data.
                    blockData.backgroundColor = AppUtils.intToHex(newColor)
                }

                Elements.GRADIENT_TYPE -> {
                    val gradientData = elementData.data as GradientData

                    //Update the data.
                    gradientData.backgroundColor = AppUtils.intToHex(newColor)
                }

                Elements.PATTERN_TYPE -> {
                    val patternData = elementData.data as PatternElementData

                    //Update the data.
                    patternData.backgroundColor = AppUtils.intToHex(newColor)
                }

                Elements.AUDIO_TYPE -> {
                    val audioData = elementData.data as AudioData

                    //Update the data.
                    audioData.backgroundColor = AppUtils.intToHex(newColor)
                }
            }

            //Update the focused element background color.
            val view = elementsMedium.getElementById(elementData) ?: return
            val drawable =
                (view.background) as LayerDrawable
            (DrawableCompat.wrap(drawable.getDrawable(1)) as GradientDrawable).setColor(newColor)

            view.background = drawable
        }


        //Text UI update functions here...
        fun updateTextColor(elementData: ElementData, newColor: Int) {
            val textData = elementData.data as TextData
            //Update the color hex in the data.
            val colorHex = AppUtils.intToHex(newColor)
            textData.color = colorHex

            //Update the focused text color
            binding.creatorCardLayout.findViewById<TextView>(elementData.id).setTextColor(newColor)

        }

        fun updateTextSize(elementData: ElementData, newSize: Int) {
            val textData = elementData.data as TextData
            //Update the data.
            textData.size = newSize

            //Update the focused text size.
            binding.creatorCardLayout.findViewById<TextView>(elementData.id).textSize =
                AppUtils.pxToSp(this@CreatorActivity, newSize).toFloat()
        }

        fun updateTextInput(elementData: ElementData, newText: String) {
            val textData = elementData.data as TextData
            //Update the data.
            textData.text = newText

            //Update the focused text size.
            binding.creatorCardLayout.findViewById<TextView>(elementData.id).text = newText

        }

        fun updateTextFont(elementData: ElementData, newFont: FontData) {
            val textData = elementData.data as TextData
            //Update the data.
            textData.font = newFont

            //Update the focused text font.
            CoroutineScope(Dispatchers.IO).launch {
                val callback = object : FontsContractCompat.FontRequestCallback() {
                    override fun onTypefaceRetrieved(typeface: Typeface) {
                        binding.creatorCardLayout.findViewById<TextView>(elementData.id).typeface =
                            typeface
                    }

                    override fun onTypefaceRequestFailed(reason: Int) {
                    }
                }
                AppUtils.getTypeFace(this@CreatorActivity, newFont, callBack = callback)
            }
        }

        fun updateTextDepthRadius(elementData: ElementData, newRadius: Int) {
            val textData = elementData.data as TextData
            //Update the data.
            textData.depthRadius = newRadius

            //Update the focused text depth radius.
            binding.creatorCardLayout.findViewById<TextView>(elementData.id).setShadowLayer(
                newRadius.toFloat(),
                textData.horzDepth.toFloat(),
                textData.vertDepth.toFloat(),
                Color.parseColor(textData.depthColor)
            )
        }

        fun updateTextDepthVertical(elementData: ElementData, newValue: Int) {
            val textData = elementData.data as TextData
            //Modify the value.
            val vertValue = -100 + newValue

            //Update the data.
            textData.vertDepth = vertValue

            //Update the focused text vertical depth.
            binding.creatorCardLayout.findViewById<TextView>(elementData.id).setShadowLayer(
                textData.depthRadius.toFloat(),
                textData.horzDepth.toFloat(),
                textData.vertDepth.toFloat(),
                Color.parseColor(textData.depthColor)
            )
        }

        fun updateTextDepthHorizontal(elementData: ElementData, newValue: Int) {
            val textData = elementData.data as TextData
            //Modify the value.
            val horzValue = -100 + newValue

            //Update the data.
            textData.horzDepth = horzValue

            //Update the focused text vertical depth.
            binding.creatorCardLayout.findViewById<TextView>(elementData.id).setShadowLayer(
                textData.depthRadius.toFloat(),
                textData.horzDepth.toFloat(),
                textData.vertDepth.toFloat(),
                Color.parseColor(textData.depthColor)
            )
        }

        fun updateTextDepthColor(elementData: ElementData, newColor: Int) {
            val textData = elementData.data as TextData
            //Update the data.
            textData.depthColor = AppUtils.intToHex(newColor)

            //Update the focused text depth color.
            binding.creatorCardLayout.findViewById<TextView>(elementData.id).setShadowLayer(
                textData.depthRadius.toFloat(),
                textData.horzDepth.toFloat(),
                textData.vertDepth.toFloat(),
                Color.parseColor(textData.depthColor)
            )
        }


        //Image UI update functions here...
        fun updateImageInput(elementData: ElementData, newImageUri: String, bitmap: Bitmap) {
            val imageData = elementData.data as ImageData

            //Update the storage path.
            if (user != null) {
                imageData.storagePath =
                    "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${user!!.uid}/${AppUtils.IMAGE_ELEMENTS_REFERENCE}/${elementData.id}"
            }


            //Update the data with local path
            imageData.localPath = newImageUri

            //Reset the compression and filter.
            imageData.quality = 100
            imageData.filterName = ImageFilters(this@CreatorActivity).ORIGINAL

            //Update the data in the temp image list.
            val imageView = elementsMedium.getElementById(elementData) as ImageView
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    for (data in tempImageList) {
                        if (data.elementId == elementData.id) {
                            data.bitmap = bitmap
                            withContext(Dispatchers.Main) {
                                //Update the focused image input.
                                imageView.setImageBitmap(bitmap)

//                            handleImageOptionSelection(elementData.currentSelection!!)

                                val imageInputControlsFragment =
                                    ImageInputControlsFragment(
                                        this@ElementsControlsMedium,
                                        imageLauncher
                                    )

                                //Update the image controls container.
                                supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.elementOptionControlsContainer,
                                        imageInputControlsFragment
                                    )
                                    .runOnCommit {
                                        imageInputControlsFragment.setCurrentImageInput(
                                            data,
                                            this@CreatorActivity
                                        )
                                    }.commit()
                            }
                            break
                        }
                    }
                }
            }
        }

        fun updateImageCrop(elementData: ElementData, newImage: Bitmap) {
            val imageData = elementData.data as ImageData
            //Update the data in the temp list.
            for (data in tempImageList) {
                if (data.elementId == elementData.id) {
                    data.bitmap = newImage
                    break
                }
            }


            //Update the focused image.
            val imageView = elementsMedium.getElementById(elementData) as ImageView

            //Apply the filter and compression.
            runBlocking {
                if (imageData.filterName == ImageFilters(this@CreatorActivity).ORIGINAL) {
                    Glide.with(this@CreatorActivity).asBitmap().load(newImage)
                        .apply(
                            RequestOptions().encodeFormat(Bitmap.CompressFormat.JPEG)
                                .encodeQuality(imageData.quality)
                        ).into(imageView)
                } else {
                    CoroutineScope(Dispatchers.Default).launch {
                        ImageFilters(this@CreatorActivity).getFilterWithName(
                            imageData.filterName,
                            object : ImageFilters.ImageFiltersListener {
                                override fun onFiltersWithImageLoaded(loadedFilters: MutableList<Bitmap>) {
                                }

                                override fun onFilterReadyWithName(filter: ImageFilterData) {
                                    //Set the filter and apply compression.
                                    val gpuImage = GPUImage(this@CreatorActivity)
                                    gpuImage.setImage(newImage)
                                    gpuImage.setFilter(filter.filter)

                                    Glide.with(this@CreatorActivity).asBitmap()
                                        .load(gpuImage.bitmapWithFilterApplied)
                                        .apply(
                                            RequestOptions().encodeFormat(Bitmap.CompressFormat.JPEG)
                                                .encodeQuality(imageData.quality)
                                        ).into(imageView)
                                }

                            })
                    }
                }
            }
        }

        fun updateImageCompression(elementData: ElementData, newValue: Int) {
            val imageData = elementData.data as ImageData

            //Update the data.
            imageData.quality = newValue

            //Update the UI.
            val imageView = elementsMedium.getElementById(elementData) as ImageView

            //Apply the filter and then set the compression.
            runBlocking {
                CoroutineScope(Dispatchers.Default).launch {
                    ImageFilters(this@CreatorActivity).getFilterWithName(
                        imageData.filterName,
                        object : ImageFilters.ImageFiltersListener {
                            override fun onFiltersWithImageLoaded(loadedFilters: MutableList<Bitmap>) {
                            }

                            override fun onFilterReadyWithName(filter: ImageFilterData) {
                                //Set the filter and apply compression.
                                for (temp in tempImageList) {
                                    if (temp.elementId == elementData.id && temp.bitmap != null) {
                                        if (imageData.filterName == ImageFilters(this@CreatorActivity).ORIGINAL) {
                                            Glide.with(this@CreatorActivity).asBitmap()
                                                .load(temp.bitmap)
                                                .apply(
                                                    RequestOptions().encodeFormat(Bitmap.CompressFormat.JPEG)
                                                        .encodeQuality(newValue)
                                                ).into(imageView)
                                        } else {
                                            //Update the filter and set the compression.
                                            val gpuImage = GPUImage(this@CreatorActivity)
                                            gpuImage.setImage(temp.bitmap)
                                            gpuImage.setFilter(filter.filter)

                                            Glide.with(this@CreatorActivity).asBitmap()
                                                .load(gpuImage.bitmapWithFilterApplied)
                                                .apply(
                                                    RequestOptions().encodeFormat(Bitmap.CompressFormat.JPEG)
                                                        .encodeQuality(newValue)
                                                ).into(imageView)
                                        }
                                    }
                                }
                            }
                        })
                }
            }
        }

        fun updateImageFilter(elementData: ElementData, newFilter: ImageFilterData) {
            val imageData = elementData.data as ImageData
            //Update the data.
            imageData.filterName = newFilter.filterName

            //Update the focused image.
            val imageView = elementsMedium.getElementById(elementData) as ImageView

            runBlocking {
                CoroutineScope(Dispatchers.Default).launch {
                    val gpuImage = GPUImage(this@CreatorActivity)
                    for (data in tempImageList) {
                        if (data.elementId == elementData.id && data.bitmap != null) {
                            //If the new filter is null then it is original.
                            if (newFilter.filter == null) {
                                withContext(Dispatchers.Main) {
                                    Glide.with(this@CreatorActivity).asBitmap().load(data.bitmap)
                                        .apply(
                                            RequestOptions().encodeFormat(Bitmap.CompressFormat.JPEG)
                                                .encodeQuality(imageData.quality)
                                        )
                                        .into(imageView)
                                }
                            } else {
                                gpuImage.setImage(data.bitmap)
                                gpuImage.setFilter(newFilter.filter)
                                val bitmap = gpuImage.bitmapWithFilterApplied

                                //Apply the compression and update the UI.
                                withContext(Dispatchers.Main) {
                                    Glide.with(this@CreatorActivity).asBitmap().load(bitmap)
                                        .apply(
                                            RequestOptions().encodeFormat(Bitmap.CompressFormat.JPEG)
                                                .encodeQuality(imageData.quality)
                                        )
                                        .into(imageView)
                                }
                            }
                            break
                        }
                    }
                }
            }
        }

        fun updateImageScaleType(elementData: ElementData, newScaleTypeData: ImageScaleTypeData) {
            val imageData = elementData.data as ImageData
            val imageView = binding.creatorCardLayout.findViewById<ImageView>(elementData.id)

            //Update the data.
            imageData.scaleType = newScaleTypeData

            //Update the focused image scale type.
            imageView.scaleType = newScaleTypeData.scaleType
            /* CoroutineScope(Dispatchers.IO).launch {
                 for (data in tempImageList) {
                     if (data.elementId == elementData.id) {
                         if (data.bitmap != null) {
                             withContext(Dispatchers.Main) {
                                 imageView.setImageBitmap(data.bitmap)
                             }
                         }
                         break
                     }
                 }
             }*/

            //Apply the filter and then set the compression.
            runBlocking {
                CoroutineScope(Dispatchers.Default).launch {
                    ImageFilters(this@CreatorActivity).getFilterWithName(
                        imageData.filterName,
                        object : ImageFilters.ImageFiltersListener {
                            override fun onFiltersWithImageLoaded(loadedFilters: MutableList<Bitmap>) {
                            }

                            override fun onFilterReadyWithName(filter: ImageFilterData) {
                                //Set the filter and apply compression.
                                for (temp in tempImageList) {
                                    if (temp.elementId == elementData.id && temp.bitmap != null) {
                                        if (imageData.filterName == ImageFilters(this@CreatorActivity).ORIGINAL) {
                                            Glide.with(this@CreatorActivity).asBitmap()
                                                .load(temp.bitmap)
                                                .apply(
                                                    RequestOptions().encodeFormat(Bitmap.CompressFormat.JPEG)
                                                        .encodeQuality(imageData.quality)
                                                ).into(imageView)
                                        } else {
                                            //Update the filter and set the compression.
                                            val gpuImage = GPUImage(this@CreatorActivity)
                                            gpuImage.setImage(temp.bitmap)
                                            gpuImage.setFilter(filter.filter)

                                            Glide.with(this@CreatorActivity).asBitmap()
                                                .load(gpuImage.bitmapWithFilterApplied)
                                                .apply(
                                                    RequestOptions().encodeFormat(Bitmap.CompressFormat.JPEG)
                                                        .encodeQuality(imageData.quality)
                                                ).into(imageView)
                                        }
                                    }
                                }
                            }
                        })
                }
            }
        }


        //Block update functions here...
        fun updateBlockColor(elementData: ElementData, newColor: Int) {
            val blockData = elementData.data as BlockData
            //Update the data.
            blockData.color = AppUtils.intToHex(newColor)

            //Update the focused block color.
            val block = elementsMedium.getElementById(elementData) as ImageView
            val drawable = block.drawable
            (DrawableCompat.wrap(drawable) as GradientDrawable).color =
                ColorStateList.valueOf(newColor)
        }


        //Gradient update functions here...
        fun updateGradientColors(elementData: ElementData, newColors: MutableList<String>) {
            val gradientData = elementData.data as GradientData
            val intColorArray = intArrayOf(
                Color.parseColor(gradientData.colors!![0]),
                Color.parseColor(gradientData.colors!![1])
            )


            //Update the data.
            gradientData.colors = newColors

            //Update the focused gradient colors.
            val drawable =
                binding.creatorCardLayout.findViewById<ImageView>(elementData.id).drawable
            (DrawableCompat.wrap(drawable) as GradientDrawable).colors = intColorArray
        }

        fun updateGradientAngle(elementData: ElementData, newAngle: Int) {
            val gradientData = elementData.data as GradientData

            //Set the data.
            gradientData.gradientAngle = newAngle

            //Update the focused gradient angle.
            val drawable =
                binding.creatorCardLayout.findViewById<ImageView>(elementData.id).drawable
            val wrapDrawable = DrawableCompat.wrap(drawable) as GradientDrawable
            when (newAngle) {
                0 -> {
                    wrapDrawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT
                }

                45 -> {
                    wrapDrawable.orientation = GradientDrawable.Orientation.TL_BR
                }

                90 -> {
                    wrapDrawable.orientation = GradientDrawable.Orientation.TOP_BOTTOM
                }

                135 -> {
                    wrapDrawable.orientation = GradientDrawable.Orientation.TR_BL
                }

                180 -> {
                    wrapDrawable.orientation = GradientDrawable.Orientation.RIGHT_LEFT
                }

                225 -> {
                    wrapDrawable.orientation = GradientDrawable.Orientation.BR_TL
                }

                270 -> {
                    wrapDrawable.orientation = GradientDrawable.Orientation.BOTTOM_TOP
                }

                315 -> {
                    wrapDrawable.orientation = GradientDrawable.Orientation.BL_TR
                }
            }
        }


        //Pattern update functions here...
        fun updatePatternDesign(elementData: ElementData, newPattern: PatternData) {
            val patternElementData = elementData.data as PatternElementData

            //Update the data.
            patternElementData.pattern = newPattern

            //Update the focused pattern design.
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    val view = Patterns(this@CreatorActivity).loadPatternWithViewId(
                        newPattern,
                        patternElementData.width,
                        patternElementData.height
                    )
                    withContext(Dispatchers.Main) {
                        binding.creatorCardLayout.findViewById<FrameLayout>(elementData.id)
                            .findViewById<MaterialCardView>(R.id.creatorPatternElementCard)
                            .removeAllViews()
                        binding.creatorCardLayout.findViewById<FrameLayout>(elementData.id)
                            .findViewById<MaterialCardView>(R.id.creatorPatternElementCard)
                            .addView(view)
                    }
                }
            }
        }


        //Audio update functions here...
        @androidx.media3.common.util.UnstableApi
        fun updateAudioInput(elementData: ElementData, cachePath: String) {
            val audioData = elementData.data as AudioData

            //Update the data.
            audioData.localPath = cachePath

            //Update the storage path.
            if (user != null) {
                audioData.storagePath =
                    "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${user!!.uid}/${AppUtils.AUDIO_ELEMENTS_REFERENCE}/${elementData.id}"
            }


            val playerController =
                binding.creatorCardLayout.findViewById<FrameLayout>(elementData.id)
                    .findViewById<PlayerControlView>(R.id.creatorAudioElementPlayer)

            //Update the creator element UI.
            val player = ExoPlayer.Builder(this@CreatorActivity).build()


            val item = MediaItem.Builder()
            item.setUri(Uri.parse(cachePath))
            item.setMimeType(MimeTypes.AUDIO_MP4)
            val menuItem = item.build()

            val source =
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this@CreatorActivity))
                    .createMediaSource(menuItem)

            player.addMediaItem(menuItem)
            player.prepare()

            playerController.player = player

            //Update the controls.
            val audioInputControlsFragment =
                AudioInputControlsFragment(this@ElementsControlsMedium, audioLauncher)


            //Update the image controls container.
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.elementOptionControlsContainer,
                    audioInputControlsFragment
                )
                .runOnCommit {
                    audioInputControlsFragment.setCurrentAudioInput(
                        elementData, playerController
                    )
                }.commit()
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_puller, R.anim.activity_close)

    }


    override fun optionSelected(elementOptionData: ElementOptionData) {
        when (elementOptionData.elementType) {
            Elements.MAIN -> {
                val currentPos = mainOptionsList.indexOf(elementOptionData)

                val scrollRecyTo =
                    if (currentPos != (binding.elementOptionsRecy.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()) {
                        currentPos + 1
                    } else {
                        currentPos - 1
                    }

                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    if (scrollRecyTo != -1) {
                        binding.elementOptionsRecy.smoothScrollToPosition(scrollRecyTo)
                    }
                }

                elementsControlsMedium.handleMainOptionSelection(elementOptionData)
            }

            Elements.TEXT_TYPE -> {

                val currentPos = textOptionsList.indexOf(elementOptionData)

                val scrollRecyTo =
                    if (currentPos != (binding.elementOptionsRecy.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()) {
                        currentPos + 1
                    } else {
                        currentPos - 1
                    }

                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    if (scrollRecyTo != -1) {
                        binding.elementOptionsRecy.smoothScrollToPosition(scrollRecyTo)
                    }
                }

                elementsControlsMedium.handleTextOptionSelection(elementOptionData)
            }

            Elements.IMAGE_TYPE -> {
                val currentPos = imageOptionsList.indexOf(elementOptionData)

                val scrollRecyTo =
                    if (currentPos != (binding.elementOptionsRecy.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()) {
                        currentPos + 1
                    } else {
                        currentPos - 1
                    }

                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    if (scrollRecyTo != -1) {
                        binding.elementOptionsRecy.smoothScrollToPosition(scrollRecyTo)
                    }
                }

                elementsControlsMedium.handleImageOptionSelection(elementOptionData)
            }

            Elements.BLOCK_TYPE -> {
                val currentPos = blockOptionsList.indexOf(elementOptionData)

                val scrollRecyTo =
                    if (currentPos != (binding.elementOptionsRecy.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()) {
                        currentPos + 1
                    } else {
                        currentPos - 1
                    }

                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    if (scrollRecyTo != -1) {
                        binding.elementOptionsRecy.smoothScrollToPosition(scrollRecyTo)
                    }
                }

                elementsControlsMedium.handleBlockOptionSelection(elementOptionData)
            }

            Elements.GRADIENT_TYPE -> {
                val currentPos = gradientOptionsList.indexOf(elementOptionData)

                val scrollRecyTo =
                    if (currentPos != (binding.elementOptionsRecy.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()) {
                        currentPos + 1
                    } else {
                        currentPos - 1
                    }

                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    if (scrollRecyTo != -1) {
                        binding.elementOptionsRecy.smoothScrollToPosition(scrollRecyTo)
                    }
                }

                elementsControlsMedium.handleGradientOptionSelection(elementOptionData)
            }

            Elements.PATTERN_TYPE -> {
                val currentPos = patternOptionsList.indexOf(elementOptionData)

                val scrollRecyTo =
                    if (currentPos != (binding.elementOptionsRecy.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()) {
                        currentPos + 1
                    } else {
                        currentPos - 1
                    }

                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    if (scrollRecyTo != -1) {
                        binding.elementOptionsRecy.smoothScrollToPosition(scrollRecyTo)
                    }
                }

                elementsControlsMedium.handlePatternOptionSelection(elementOptionData)
            }

            AUDIO_TYPE -> {
                val currentPos = audioOptionsList.indexOf(elementOptionData)

                val scrollRecyTo =
                    if (currentPos != (binding.elementOptionsRecy.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()) {
                        currentPos + 1
                    } else {
                        currentPos - 1
                    }

                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    if (scrollRecyTo != -1) {
                        binding.elementOptionsRecy.smoothScrollToPosition(scrollRecyTo)
                    }
                }

                elementsControlsMedium.handleAudioOptionSelection(elementOptionData)
            }
        }

        //Expand the options sheet.
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.keyCode == KeyEvent.KEYCODE_BACK) {
            backPressDialog()
        }
        return false
    }


    //Theme.
    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.greetrCreatorParent.setBackgroundColor(Color.BLACK)
        } else {
            binding.greetrCreatorParent.setBackgroundColor(
                colourUtils.lightModeParentPurple
            )
        }
    }

    fun loadFirstHalfGuidedTourList(): MutableList<GuideData> {
        val guideList = ArrayList<GuideData>()
        val step1 = GuideData(
            "This is your main card and it is the main canvas on which you can add, edit and remove various kinds of elements",
            binding.creatorCard.x,
            binding.creatorCard.y
        )
        guideList.add(step1)

        val step2 = GuideData(
            "Elements are different kind of inputs that can be added to customize your card in various ways",
            binding.creatorCard.x,
            binding.creatorCard.y
        )
        guideList.add(step2)

        val step3 = GuideData(
            "Now lets add an element by clicking the plus button here",
            binding.creatorAddElement.x,
            binding.creatorAddElement.y
        )
        guideList.add(step3)
        return guideList
    }


    fun saveCard() {
        if (user == null) {
            AppUtils.buildSnackbar(
                this,
                "Sign in to save your cards.",
                binding.greetrCreatorParent
            ).show()
            return
        }

        val savingSheet = SavingSheet()
        savingSheet.show(supportFragmentManager, "UseCaseCreator")

        //Save the images to storage if any.
        runBlocking {
            CoroutineScope(Dispatchers.Default).launch {
                for (data in tempImageList) {
                    if (data.bitmap != null) {
                        val os = ByteArrayOutputStream()
                        data.bitmap!!.compress(Bitmap.CompressFormat.PNG, 0, os)
                        storageReference.child(AppUtils.USER_STORAGE_ASSETS_REFERENCE)
                            .child(user!!.uid)
                            .child(AppUtils.IMAGE_ELEMENTS_REFERENCE)
                            .child(data.elementId.toString())
                            .putBytes(os.toByteArray()).addOnCompleteListener {
                                os.flush()
                                os.close()
                            }
                    }
                }
            }
        }

        //Save the audio files to storage if any.
        runBlocking {
            CoroutineScope(Dispatchers.Default).launch {
                for (element in elementsList) {
                    if (element.elementType == AUDIO_TYPE) {
                        //Upload the audio to storage.
                        val data = element.data as AudioData
                        val path = data.localPath

                        //Upload only if user has added any audio file.
                        val file = File(path)

                        if (!file.exists()) {
                            continue
                        } else {
                            //Go ahead and upload the audio file to user's storage.
                            storageReference.child(AppUtils.USER_STORAGE_ASSETS_REFERENCE)
                                .child(user!!.uid).child(AppUtils.AUDIO_ELEMENTS_REFERENCE)
                                .child(element.id.toString()).putFile(Uri.fromFile(file)).addOnCompleteListener {

                                }
                        }
                    }
                }
            }
        }

        cardData.updatedOn = Calendar.getInstance().timeInMillis

        //Save the card data to the users location.
        firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc").collection(user!!.uid)
            .document("doc").collection(AppUtils.CRAFTY_CARDS_COLLECTION)
            .document(cardData.id.toString()).set(cardData).addOnCompleteListener {
                if (it.isSuccessful) {
                    AppUtils.buildSnackbar(
                        this,
                        "Card saved successfully!",
                        binding.greetrCreatorParent
                    ).show()

                    firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid)
                        .get().addOnCompleteListener { it2 ->
                            if (!it2.result.exists()) {
                                val adRequest = AdRequest.Builder().build()

                                InterstitialAd.load(
                                    this,
                                    getString(R.string.card_saved_interstitial),
                                    adRequest,
                                    object : InterstitialAdLoadCallback() {
                                        override fun onAdFailedToLoad(adError: LoadAdError) {
                                        }

                                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                delay(3000)
                                                interstitialAd.show(this@CreatorActivity)
                                            }
                                        }
                                    })

                            }
                        }

                } else {
                    AppUtils.buildSnackbar(
                        this,
                        "Can't save your card now, please try again later.",
                        binding.greetrCreatorParent
                    ).show()
                }
                savingSheet.dismiss()
            }
    }

    fun exportAsJPG() {
        //Get the entire card bitmap.
        val bitmap =
            Bitmap.createBitmap(
                mainCardData.width,
                mainCardData.height,
                Bitmap.Config.ARGB_8888
            )

        val canvas = Canvas(bitmap)

        binding.creatorCard.draw(canvas)
        val savingSheet = SavingSheet()
        savingSheet.show(supportFragmentManager, "UseCaseOne")

        val mediaListener = object : AppUtils.MediaListener {
            override fun onMediaSaved(savedPath: String) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    savingSheet.dismiss()
                    val snackbar =
                        AppUtils.buildSnackbar(
                            this@CreatorActivity,
                            "Card saved at Pictures/Crafty",
                            binding.greetrCreatorParent
                        )
                    snackbar.setAction("Open") {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(savedPath)
                        startActivity(intent)
                    }
                    snackbar.show()
                }
            }

            override fun onMediaSaveProgress(progress: Int) {

            }

            override fun onMediaSaveFailed(reason: String) {
                CoroutineScope(Dispatchers.Main).launch {
                    savingSheet.dismiss()
                }
            }

        }

        val savingFileName = AppUtils.uniqueContentNameGenerator(cardData.cardName)
        CoroutineScope(Dispatchers.IO).launch {
            AppUtils.saveImageBitmap(
                this@CreatorActivity,
                savingFileName,
                AppUtils.JPG,
                bitmap,
                mediaListener
            )
        }
    }

    fun exportAsPNG() {
        //Get the entire card bitmap.
        val bitmap =
            Bitmap.createBitmap(
                binding.creatorCard.width,
                binding.creatorCard.height,
                Bitmap.Config.ARGB_8888
            )

        val canvas = Canvas(bitmap)

        binding.creatorCard.draw(canvas)

        val savingSheet = SavingSheet()
        savingSheet.show(supportFragmentManager, "UseCaseTwo")

        val mediaListener = object : AppUtils.MediaListener {
            override fun onMediaSaved(savedPath: String) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    savingSheet.dismiss()
                    val snackbar =
                        AppUtils.buildSnackbar(
                            this@CreatorActivity,
                            "Card saved at Pictures/Crafty",
                            binding.greetrCreatorParent
                        )
                    snackbar.setAction("Open") {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(savedPath)
                        startActivity(intent)
                    }
                    snackbar.show()
                }
            }

            override fun onMediaSaveProgress(progress: Int) {

            }

            override fun onMediaSaveFailed(reason: String) {
                CoroutineScope(Dispatchers.Main).launch {
                    savingSheet.dismiss()
                }
            }

        }

        val savingFileName = AppUtils.uniqueContentNameGenerator(cardData.cardName)
        CoroutineScope(Dispatchers.IO).launch {
            AppUtils.saveImageBitmap(
                this@CreatorActivity,
                savingFileName,
                AppUtils.PNG,
                bitmap,
                mediaListener
            )
        }
    }

    fun exportAsMp4() {

        //Get the very first audio element.
        var audioPath = "No audio"
        for (element in elementsList) {
            if (element.elementType == AUDIO_TYPE) {
                audioPath = (element.data as AudioData).localPath
                break
            }
        }

        //Mux the audio and video, then give the output file.
        CoroutineScope(Dispatchers.IO).launch {

            var bitmap: Bitmap

            withContext(Dispatchers.Main) {
                //Get the entire card bitmap.
                //Divide the height and width by 2 to make sure the width and height are multiples of 2.
                bitmap =
                    Bitmap.createBitmap(
                        if (binding.creatorCard.width.rem(2) == 0) {
                            binding.creatorCard.width
                        } else {
                            binding.creatorCard.width + 1
                        },
                        if (binding.creatorCard.height.rem(2) == 0) {
                            binding.creatorCard.height
                        } else {
                            binding.creatorCard.height + 1
                        },
                        Bitmap.Config.ARGB_8888
                    )

                val canvas = Canvas(bitmap)

                binding.creatorCard.draw(canvas)
            }

            //Show saving sheet.
            val savingSheet = SavingSheet()
            withContext(Dispatchers.Main) {
                savingSheet.show(supportFragmentManager, "ExportAsMp4")
            }

            val fileName = AppUtils.uniqueContentNameGenerator(cardData.cardName)

            //Check if the audio file is in proper format for muxing and then proceed.
            if (File(audioPath).exists()) {
                val extractor = MediaExtractor()
                extractor.setDataSource(audioPath)
                val inputFormat = extractor.getTrackFormat(0)
                val mime = inputFormat.getString(MediaFormat.KEY_MIME)
                if (mime == "audio/mp4a-latm") {
                    AppUtils.saveCardAsMp4(
                        this@CreatorActivity,
                        fileName,
                        bitmap,
                        audioPath,
                        object : AppUtils.MediaListener {
                            override fun onMediaSaved(savedPath: String) {
                                CoroutineScope(Dispatchers.Main).launch {

                                    savingSheet.dismiss()
                                    val snackbar = AppUtils.buildSnackbar(
                                        this@CreatorActivity,
                                        "Card saved as video successfully at 'Movies/Crafty'",
                                        binding.root
                                    )
                                    snackbar.setAction("Open") {
                                        val fileUri = FileProvider.getUriForFile(
                                            this@CreatorActivity,
                                            BuildConfig.APPLICATION_ID + ".provider", File(savedPath)
                                        )
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.setDataAndType(fileUri, "video/*")
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        startActivity(intent)
                                    }
                                    snackbar.show()
                                }

                            }

                            override fun onMediaSaveProgress(progress: Int) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    savingSheet.updateProgress(progress)
                                }
                            }

                            override fun onMediaSaveFailed(reason: String) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    savingSheet.dismiss()
                                    AppUtils.buildSnackbar(this@CreatorActivity, reason, binding!!.root)
                                        .show()
                                }
                            }
                        })
                }
                else {
                    //Convert the audio file to prepare it for muxing.
                    val cachePath =
                        cacheDir.toString() + "/" + "Crafty/" + "tempConvertedAudioTunnel" + ".mp3"

                    AppUtils.convertAudioToM4A(audioPath, cachePath)
                    AppUtils.saveCardAsMp4(
                        this@CreatorActivity,
                        fileName,
                        bitmap,
                        cachePath,
                        object : AppUtils.MediaListener {
                            override fun onMediaSaved(savedPath: String) {
                                CoroutineScope(Dispatchers.Main).launch {

                                    savingSheet.dismiss()
                                    val snackbar = AppUtils.buildSnackbar(
                                        this@CreatorActivity,
                                        "Card saved as video successfully at 'Movies/Crafty'",
                                        binding.root
                                    )
                                    snackbar.setAction("Open") {
                                        val fileUri = FileProvider.getUriForFile(
                                            this@CreatorActivity,
                                            BuildConfig.APPLICATION_ID + ".provider", File(savedPath)
                                        )
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.setDataAndType(fileUri, "video/*")
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        startActivity(intent)
                                    }
                                    snackbar.show()
                                }

                            }

                            override fun onMediaSaveProgress(progress: Int) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    savingSheet.updateProgress(progress)
                                }
                            }

                            override fun onMediaSaveFailed(reason: String) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    savingSheet.dismiss()
                                    AppUtils.buildSnackbar(this@CreatorActivity, reason, binding!!.root)
                                        .show()
                                }
                            }
                        })
                }
            }
            else {
                AppUtils.saveCardAsMp4(
                    this@CreatorActivity,
                    fileName,
                    bitmap,
                    audioPath,
                    object : AppUtils.MediaListener {
                        override fun onMediaSaved(savedPath: String) {
                            CoroutineScope(Dispatchers.Main).launch {

                                savingSheet.dismiss()
                                val snackbar = AppUtils.buildSnackbar(
                                    this@CreatorActivity,
                                    "Card saved as video successfully at 'Movies/Crafty'",
                                    binding.root
                                )
                                snackbar.setAction("Open") {
                                    val fileUri = FileProvider.getUriForFile(
                                        this@CreatorActivity,
                                        BuildConfig.APPLICATION_ID + ".provider", File(savedPath)
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(fileUri, "video/*")
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    startActivity(intent)
                                }
                                snackbar.show()
                            }

                        }

                        override fun onMediaSaveProgress(progress: Int) {
                            CoroutineScope(Dispatchers.Main).launch {
                                savingSheet.updateProgress(progress)
                            }
                        }

                        override fun onMediaSaveFailed(reason: String) {
                            CoroutineScope(Dispatchers.Main).launch {
                                savingSheet.dismiss()
                                AppUtils.buildSnackbar(this@CreatorActivity, reason, binding!!.root)
                                    .show()
                            }
                        }
                    })
            }

        }
    }

    @SuppressLint("WrongConstant")
    suspend fun mux(audi: String, vid: String, out: String) {
        // Extract audi.
        val audiExtractor = MediaExtractor()
        audiExtractor.setDataSource(audi)
        audiExtractor.selectTrack(0)
        val audiFormat = audiExtractor.getTrackFormat(0)

        val vidExtractor = MediaExtractor()
        vidExtractor.setDataSource(vid)
        vidExtractor.selectTrack(0)
        val vidFormat = vidExtractor.getTrackFormat(0)

        val muxer = MediaMuxer(out, OutputFormat.MUXER_OUTPUT_MPEG_4)
        val audiIndex = muxer.addTrack(audiFormat)
        val vidIndex = muxer.addTrack(vidFormat)
        muxer.start()

        val byteSize = 1024 * 1024
        val buffer = ByteBuffer.allocate(byteSize)
        val bufferInfo = MediaCodec.BufferInfo()

        //Mux Vid.
        while (true) {
            val vidByte = vidExtractor.readSampleData(buffer, 0)

            if (vidByte > 0) {
                bufferInfo.presentationTimeUs = vidExtractor.sampleTime
                bufferInfo.flags = vidExtractor.sampleFlags
                bufferInfo.size = vidByte

                muxer.writeSampleData(vidIndex, buffer, bufferInfo)
                vidExtractor.advance()
            } else {
                break
            }
        }

        //Mux audi.
        while (true) {
            val audiByte = audiExtractor.readSampleData(buffer, 0)

            if (audiByte > 0) {
                bufferInfo.presentationTimeUs = audiExtractor.sampleTime
                bufferInfo.flags = audiExtractor.sampleFlags
                bufferInfo.size = audiByte

                muxer.writeSampleData(audiIndex, buffer, bufferInfo)
                audiExtractor.advance()
            } else {
                break
            }
        }

        muxer.stop()
        muxer.release()

        vidExtractor.release()
        audiExtractor.release()
    }

    @androidx.media3.common.util.UnstableApi
    fun loadAvailableElements() {
        //Create gesture detector
        val gestureDetector = elementsMedium.createElementGestureDetector()
        val scaleDetector = elementsMedium.createElementScaleDetector()

        val gson = Gson()


        //Load and build all the available elements.
        for (element in elementsList) {
            try {
                when (element.elementType) {
                    Elements.TEXT_TYPE -> {
                        val hashMapTree = gson.toJsonTree(element.data)
                        val data = gson.fromJson(hashMapTree, TextData::class.java)
                        element.data = data


                        //Create the text element.
                        createTextElement(
                            element,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(element)
                        )

                        //Update all the required attributes.
                        elementsControlsMedium.updateTextInput(element, data.text)
                        elementsControlsMedium.updateTextSize(element, data.size)

                        if (data.font != null) {
                            elementsControlsMedium.updateTextFont(element, data.font!!)
                        }

                        elementsControlsMedium.updateTextColor(
                            element,
                            Color.parseColor(data.color)
                        )
                        elementsControlsMedium.updateTextDepthColor(
                            element,
                            Color.parseColor(data.depthColor)
                        )
                        elementsControlsMedium.updateTextDepthHorizontal(element, data.horzDepth)
                        elementsControlsMedium.updateTextDepthVertical(element, data.vertDepth)
                        elementsControlsMedium.updateTextDepthRadius(element, data.depthRadius)

                        elementsControlsMedium.updateElementZoomX(element, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(element, data.scaleY)
                        elementsControlsMedium.updateElementRotation(element, data.rotation)
                        elementsControlsMedium.updateElementBackgroundPadding(
                            element,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            element,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            element,
                            Color.parseColor(data.backgroundColor)
                        )

                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(element.id).x =
                                element.xPosition
                            binding.creatorCardLayout.findViewById<View>(element.id).y =
                                element.yPosition
                        }

                    }

                    Elements.IMAGE_TYPE -> {
                        val hashMapTree = gson.toJsonTree(element.data)
                        val data = gson.fromJson(hashMapTree, ImageData::class.java)
                        element.data = data


                        //Create the image element.
                        createImageElement(
                            element,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(element)
                        )

                        //Get, load and set the image.
                        if (AppUtils.hasStoragePermission(this)) {
                            val file = File(data.localPath)
                            if (file.exists()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val inputStream = FileInputStream(file.path)
                                    val bit = BitmapFactory.decodeStream(inputStream)

                                    val tempData = TempUiImageData()
                                    tempData.bitmap = bit
                                    tempData.elementId = element.id
                                    tempImageList.add(tempData)

                                    withContext(Dispatchers.Main) {
                                        elementsControlsMedium.updateImageInput(
                                            element,
                                            data.localPath,
                                            bit
                                        )
                                    }
                                }
                            } else {
                                if (user == null) {
                                    return
                                }
                                val path = data.storagePath.ifEmpty {
                                    "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${user!!.uid}/${AppUtils.IMAGE_ELEMENTS_REFERENCE}/${element.id}"
                                }
                                val ref = storageReference.child(path)

                                try {
                                    ref.getStream { state, stream ->
                                        if (state.error != null) {
                                            return@getStream
                                        }
                                        val bitmap = BitmapFactory.decodeStream(stream)

                                        val tempData = TempUiImageData()
                                        tempData.bitmap = bitmap
                                        tempData.elementId = element.id
                                        tempImageList.add(tempData)

                                        elementsControlsMedium.updateImageInput(
                                            element,
                                            data.localPath,
                                            bitmap
                                        )

                                        //Write the image to cache and update the local path.
                                        runBlocking {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                val cachePath =
                                                    elements.writeImageElementToCache(
                                                        element,
                                                        bitmap
                                                    )
                                                data.localPath = cachePath
                                            }
                                        }
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                } catch (e: StorageException) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            AppUtils.buildStoragePermission(this).show()
                        }


                        //Get the right filter with the name and set it.
                        CoroutineScope(Dispatchers.IO).launch {
                            imageFilters.getFilterWithName(
                                data.filterName,
                                object : ImageFilters.ImageFiltersListener {
                                    override fun onFiltersWithImageLoaded(loadedFilters: MutableList<Bitmap>) {

                                    }

                                    override fun onFilterReadyWithName(filter: ImageFilterData) {
                                        elementsControlsMedium.updateImageFilter(element, filter)
                                    }

                                })
                        }

                        elementsControlsMedium.updateImageScaleType(element, data.scaleType!!)


                        elementsControlsMedium.updateElementSizeWidth(element, data.width)
                        elementsControlsMedium.updateElementSizeHeight(element, data.height)
                        elementsControlsMedium.updateElementZoomX(element, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(element, data.scaleY)
                        elementsControlsMedium.updateElementRotation(element, data.rotation)
                        elementsControlsMedium.updateElementCorners(element, data.corners)
                        elementsControlsMedium.updateElementBackgroundPadding(
                            element,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            element,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            element,
                            Color.parseColor(data.backgroundColor)
                        )


                        //Set positions.
                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(element.id).x =
                                element.xPosition
                            binding.creatorCardLayout.findViewById<View>(element.id).y =
                                element.yPosition
                        }

                    }

                    Elements.BLOCK_TYPE -> {
                        val hashMapTree = gson.toJsonTree(element.data)
                        val data = gson.fromJson(hashMapTree, BlockData::class.java)
                        element.data = data


                        //Create the block element.
                        createBlockElement(
                            element,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(element)
                        )

                        elementsControlsMedium.updateBlockColor(
                            element,
                            Color.parseColor(data.color)
                        )

                        elementsControlsMedium.updateElementSizeWidth(element, data.width)
                        elementsControlsMedium.updateElementSizeHeight(element, data.height)
                        elementsControlsMedium.updateElementZoomX(element, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(element, data.scaleY)
                        elementsControlsMedium.updateElementRotation(element, data.rotation)
                        elementsControlsMedium.updateElementCorners(element, data.corners)
                        elementsControlsMedium.updateElementShadowRadius(element, data.shadowRadius)
                        elementsControlsMedium.updateElementStrokeWidth(element, data.strokeWidth)
                        elementsControlsMedium.updateElementStrokeColour(
                            element,
                            Color.parseColor(data.strokeColor)
                        )
                        elementsControlsMedium.updateElementBackgroundPadding(
                            element,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            element,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            element,
                            Color.parseColor(data.backgroundColor)
                        )

                        //Set positions.
                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(element.id).x =
                                element.xPosition
                            binding.creatorCardLayout.findViewById<View>(element.id).y =
                                element.yPosition
                        }


                    }

                    Elements.GRADIENT_TYPE -> {
                        val hashMapTree = gson.toJsonTree(element.data)
                        val data = gson.fromJson(hashMapTree, GradientData::class.java)
                        element.data = data


                        //Create the gradient element.
                        createGradientElement(
                            element,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(element)
                        )

                        elementsControlsMedium.updateGradientColors(element, data.colors!!)
                        elementsControlsMedium.updateGradientAngle(element, data.gradientAngle)


                        elementsControlsMedium.updateElementSizeWidth(element, data.width)
                        elementsControlsMedium.updateElementSizeHeight(element, data.height)
                        elementsControlsMedium.updateElementZoomX(element, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(element, data.scaleY)
                        elementsControlsMedium.updateElementRotation(element, data.rotation)
                        elementsControlsMedium.updateElementCorners(element, data.corners)
                        elementsControlsMedium.updateElementShadowRadius(element, data.shadowRadius)
                        elementsControlsMedium.updateElementStrokeWidth(element, data.strokeWidth)
                        elementsControlsMedium.updateElementStrokeColour(
                            element,
                            Color.parseColor(data.strokeColor)
                        )
                        elementsControlsMedium.updateElementBackgroundPadding(
                            element,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            element,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            element,
                            Color.parseColor(data.backgroundColor)
                        )

                        //Set positions.
                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(element.id).x =
                                element.xPosition
                            binding.creatorCardLayout.findViewById<View>(element.id).y =
                                element.yPosition
                        }
                    }

                    Elements.PATTERN_TYPE -> {
                        val hashMapTree = gson.toJsonTree(element.data)
                        val data = gson.fromJson(hashMapTree, PatternElementData::class.java)
                        element.data = data


                        //Create the pattern element.
                        createPatternElement(
                            element,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(element)
                        )

                        elementsControlsMedium.updatePatternDesign(element, data.pattern!!)

                        elementsControlsMedium.updateElementSizeWidth(element, data.width)
                        elementsControlsMedium.updateElementSizeHeight(element, data.height)
                        elementsControlsMedium.updateElementZoomX(element, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(element, data.scaleY)
                        elementsControlsMedium.updateElementRotation(element, data.rotation)
                        elementsControlsMedium.updateElementCorners(element, data.corners)
                        elementsControlsMedium.updateElementShadowRadius(element, data.shadowRadius)
                        elementsControlsMedium.updateElementStrokeWidth(element, data.strokeWidth)
                        elementsControlsMedium.updateElementStrokeColour(
                            element,
                            Color.parseColor(data.strokeColor)
                        )
                        elementsControlsMedium.updateElementBackgroundPadding(
                            element,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            element,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            element,
                            Color.parseColor(data.backgroundColor)
                        )

                        //Set positions.
                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(element.id).x =
                                element.xPosition
                            binding.creatorCardLayout.findViewById<View>(element.id).y =
                                element.yPosition
                        }
                    }

                    Elements.AUDIO_TYPE -> {
                        val hashMapTree = gson.toJsonTree(element.data)
                        val data = gson.fromJson(hashMapTree, AudioData::class.java)
                        element.data = data


                        //Create the audio element.
                        createAudioElement(
                            element,
                            gestureDetector,
                            scaleDetector,
                            elementsList.indexOf(element)
                        )


                        //Check if the file is available locally or needs to get from storage and then load it.
                        if (AppUtils.hasStoragePermission(this)) {
                            val file = File(data.localPath)
                            if (file.exists()) {
                                elementsControlsMedium.updateAudioInput(element, data.localPath)
                            }
                            else {
                                if (user == null) {
                                    return
                                }
                                //Get the storage file if there's any.
                                val path = data.storagePath.ifEmpty {
                                    "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${user!!.uid}/${AppUtils.AUDIO_ELEMENTS_REFERENCE}/${element.id}"
                                }
                                val ref = storageReference.child(path)

                                //Load the audio file into the cache.
                                val cachePath =
                                    cacheDir.toString() + "/" + "Crafty/" + element.id.toString() + ".mp3"
                                val folder = File("$cacheDir/Crafty/")
                                if (!folder.exists()) {
                                    folder.mkdir()
                                }

                                //Set the local path to the data.
                                data.localPath = cachePath

                                val loadingSb = AppUtils.buildSnackbar(this, "Loading the audio file, please wait...", binding.root)
                                loadingSb.duration = Snackbar.LENGTH_INDEFINITE
                                loadingSb.show()
                                val cacheFile = File(cachePath)
                                ref.getFile(cacheFile).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        elementsControlsMedium.updateAudioInput(element, data.localPath)
                                        AppUtils.buildSnackbar(this, "Successfully loaded the audio file", binding.root).show()
                                    }
                                    else {
                                        AppUtils.buildSnackbar(this, "Unable to load the file, try again later", binding.root).show()

                                    }

                                }
                            }
                        }
                        else {
                            AppUtils.buildStoragePermission(this).show()
                        }



                        elementsControlsMedium.updateElementSizeWidth(element, data.width)
                        elementsControlsMedium.updateElementSizeHeight(element, data.height)
                        elementsControlsMedium.updateElementZoomX(element, data.scaleX)
                        elementsControlsMedium.updateElementZoomY(element, data.scaleY)
                        elementsControlsMedium.updateElementRotation(element, data.rotation)
                        elementsControlsMedium.updateElementCorners(element, data.corners)
                        elementsControlsMedium.updateElementShadowRadius(element, data.shadowRadius)
                        elementsControlsMedium.updateElementStrokeWidth(element, data.strokeWidth)
                        elementsControlsMedium.updateElementStrokeColour(
                            element,
                            Color.parseColor(data.strokeColor)
                        )
                        elementsControlsMedium.updateElementBackgroundPadding(
                            element,
                            data.backgroundPadding
                        )
                        elementsControlsMedium.updateElementBackgroundCorners(
                            element,
                            data.backgroundCorners
                        )
                        elementsControlsMedium.updateElementBackgroundColor(
                            element,
                            Color.parseColor(data.backgroundColor)
                        )

                        //Set positions.
                        binding.creatorCardLayout.doOnLayout {
                            //Set positions.
                            binding.creatorCardLayout.findViewById<View>(element.id).x =
                                element.xPosition
                            binding.creatorCardLayout.findViewById<View>(element.id).y =
                                element.yPosition
                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun backPressDialog() {
        val theme = if (AppUtils.isDarkMode(this)) {
            R.style.DarkCustomDialogTheme
        } else {
            R.style.CustomDialogTheme
        }

        val builder = AlertDialog.Builder(this, theme)
        builder.setNegativeButton(
            "NO",
            DialogInterface.OnClickListener { dialogInterface, i -> })
        builder.setPositiveButton("YES", DialogInterface.OnClickListener { dialogInterface, i ->
            finish()
        })

        if (callType == CALL_TYPE_NEW) {
            builder.setTitle("Confirm exit")
            builder.setMessage("Click yes to confirm")
        }
        if (callType == CALL_TYPE_EDIT) {
            builder.setTitle("Confirm exit")
            builder.setMessage("Click yes to confirm")
        }

        builder.show()
    }

    fun elementNameDialog(elementData: ElementData) {
        val theme = if (AppUtils.isDarkMode(this)) {
            R.style.DarkCustomDialogTheme
        } else {
            R.style.CustomDialogTheme
        }

        //Create edit text for input.
        val editText = EditText(this)
        editText.background = ColorDrawable(Color.TRANSPARENT)
        editText.setText(elementData.elementName)

        if (AppUtils.isDarkMode(this)) {
            editText.setTextColor(colourUtils.darkModeDeepPurple)

        } else {
            editText.setTextColor(colourUtils.lightModeDeepPurple)

        }
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        editText.typeface = ResourcesCompat.getFont(this, R.font.open_sans_semibold)
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.deepPurpleLight))
        editText.hint = "Enter a name here"
        editText.setPadding(AppUtils.pxToDp(this, 30))


        //Build the dialog
        val builder = AlertDialog.Builder(this, theme)
        builder.setTitle("Set a name for your element")
        builder.setView(editText)
        builder.setNegativeButton(
            "CANCEL",
            DialogInterface.OnClickListener { dialogInterface, i -> })
        builder.setPositiveButton("SET", null)
        val dialog = builder.create()

        dialog.setOnShowListener(OnShowListener {
            val button: Button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                //Check if the entered text is empty or not.
                if (TextUtils.isEmpty(editText.text.toString().trim())) {
                    editText.error = "Name should not be empty"
                } else {
                    //Save the name to the data.
                    for (element in elementsList) {
                        if (element.id == elementData.id) {
                            //Set the name.
                            element.elementName = editText.text.toString()
                            //Notify the adapter.
                            adapter.notifyItemChanged(elementsList.indexOf(element))

                            //Show a message informing that the name has been updated.
                            AppUtils.buildSnackbar(
                                this,
                                "Element name updated successfully",
                                binding.root
                            ).show()

                            //Dismiss the dialog.
                            dialog.dismiss()
                            break
                        }
                    }
                }
            }
        })
        dialog.show()
    }


    fun adjustOutliner(drawable: GradientDrawable) {
        val prefs = getSharedPreferences(AppUtils.APP_PREFS, MODE_PRIVATE)
        val colour = prefs.getString(AppUtils.ELEMENT_HIGHLIGHT_COLOUR_KEY, "#311B92")
        val corners = prefs.getInt(AppUtils.ELEMENT_HIGHLIGHT_CORNERS_KEY, 20)

        drawable.setStroke(AppUtils.pxToDp(this, 3), Color.parseColor(colour))
        drawable.cornerRadius = AppUtils.pxToDp(this, corners).toFloat()
    }

    fun addElement(type: String) {

        //Create the gesture detector
        val gestureDetector = elementsMedium.createElementGestureDetector()
        val scaleDetector = elementsMedium.createElementScaleDetector()

        when (type) {
            TEXT_TYPE -> {
                //Create the data.
                val data = TextData()
                data.text = "Text ${Random.nextInt(121)}"
                data.color = "#000000"
                data.size = 21


                //Add the element to elements List.
                val elementData = ElementData()
                elementData.elementName = data.text
                elementData.elementType = type
                elementData.data = data
                elementData.id = Random.nextInt(1000000)
                elementData.currentSelection = textOptionsList[0]
                elementData.elementBy = user!!.displayName!!
                elementData.creatorId = user!!.uid
                elementData.createdOn = Calendar.getInstance().timeInMillis
                elementData.originalCreatorId = user!!.uid


                //Notify the elements bar to add the new element.
                elementsList.add(elementData)
                adapter.notifyItemInserted(elementsList.size - 1)


                //Create and add the textview with position as it's tag
                createTextElement(
                    elementData,
                    gestureDetector,
                    scaleDetector,
                    elementsList.size - 1
                )

            }

            IMAGE_TYPE -> {
                val data = ImageData()
                data.scaleType = scaleTypeList[1]

                //Add the element to elements List.
                val elementData = ElementData()
                elementData.elementName = "Image ${kotlin.random.Random.nextInt(100)}"
                elementData.elementType = type
                elementData.data = data
                elementData.id = Random.nextInt(1000000)
                elementData.currentSelection = imageOptionsList[0]
                elementData.elementBy = user!!.displayName!!
                elementData.creatorId = user!!.uid
                elementData.createdOn = Calendar.getInstance().timeInMillis
                elementData.originalCreatorId = user!!.uid

                //Set the storage path.
                data.storagePath =
                    "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${user!!.uid}/${AppUtils.IMAGE_ELEMENTS_REFERENCE}/${elementData.id}"


                //Notify the elements bar to add the new element.
                elementsList.add(elementData)
                adapter.notifyItemInserted(elementsList.size - 1)

                //Create and add the imageview with position as it's tag.
                createImageElement(
                    elementData,
                    gestureDetector,
                    scaleDetector,
                    elementsList.size - 1
                )
            }

            BLOCK_TYPE -> {
                val data = BlockData()

                //Add the element to elements list.
                val elementData = ElementData()
                elementData.elementName = "Block ${kotlin.random.Random.nextInt(100)}"
                elementData.elementType = type
                elementData.data = data
                elementData.id = Random.nextInt(1000000)
                elementData.currentSelection = blockOptionsList[0]
                elementData.elementBy = user!!.displayName!!
                elementData.creatorId = user!!.uid
                elementData.createdOn = Calendar.getInstance().timeInMillis
                elementData.originalCreatorId = user!!.uid

                //Notify the elements bar to add the new element.
                elementsList.add(elementData)
                adapter.notifyItemInserted(elementsList.size - 1)

                //Create and add the block with position as it's tag.
                createBlockElement(
                    elementData,
                    gestureDetector,
                    scaleDetector,
                    elementsList.size - 1
                )
            }

            GRADIENT_TYPE -> {
                val data = GradientData()
                data.colors = arrayListOf("#311B92", "#9965f4")


                //Add the element to elements list.
                val elementData = ElementData()
                elementData.elementName = "Gradient ${kotlin.random.Random.nextInt(100)}"
                elementData.elementType = type
                elementData.data = data
                elementData.id = Random.nextInt(1000000)
                elementData.currentSelection = gradientOptionsList[0]
                elementData.elementBy = user!!.displayName!!
                elementData.creatorId = user!!.uid
                elementData.createdOn = Calendar.getInstance().timeInMillis
                elementData.originalCreatorId = user!!.uid


                //Notify the elements bar to add the new element.
                elementsList.add(elementData)
                adapter.notifyItemInserted(elementsList.size - 1)

                //Create and add the block with position as it's tag.
                createGradientElement(
                    elementData,
                    gestureDetector,
                    scaleDetector,
                    elementsList.size - 1
                )
            }

            PATTERN_TYPE -> {
                val data = PatternElementData()

                //Add the element to elements list.
                val elementData = ElementData()
                elementData.elementName = "Pattern ${kotlin.random.Random.nextInt(100)}"
                elementData.elementType = type
                elementData.data = data
                elementData.id = Random.nextInt(1000000)
                elementData.currentSelection = patternOptionsList[0]
                elementData.elementBy = user!!.displayName!!
                elementData.creatorId = user!!.uid
                elementData.createdOn = Calendar.getInstance().timeInMillis
                elementData.originalCreatorId = user!!.uid


                //Notify the elements bar to add the new element.
                elementsList.add(elementData)
                adapter.notifyItemInserted(elementsList.size - 1)

                //Create and add the block with position as it's tag.
                createPatternElement(
                    elementData,
                    gestureDetector,
                    scaleDetector,
                    elementsList.size - 1
                )
            }

            AUDIO_TYPE -> {
                val data = AudioData()

                //Add the element to elements list.
                val elementData = ElementData()
                elementData.elementName = "Audio ${kotlin.random.Random.nextInt(100)}"
                elementData.elementType = type
                elementData.data = data
                elementData.id = Random.nextInt(1000000)
                elementData.currentSelection = audioOptionsList[0]
                elementData.elementBy = user!!.displayName!!
                elementData.creatorId = user!!.uid
                elementData.createdOn = Calendar.getInstance().timeInMillis
                elementData.originalCreatorId = user!!.uid

                //Notify the elements bar to add the new element.
                elementsList.add(elementData)
                adapter.notifyItemInserted(elementsList.size - 1)

                //Create and add the block with position as it's tag.
                createAudioElement(
                    elementData,
                    gestureDetector,
                    scaleDetector,
                    elementsList.size - 1
                )
            }
        }


        //Adjust the elements bar.
        adjustElementsBar()

        //Adjust the focused Element
        setFocusedElement(elementsList.size - 1)

        //Handle elements options bar
        handleElementsOptionsBarVisibility()

        //Show interstitial if elements are 5.
        if (elementsList.size >= 5) {
            if (user != null) {
                firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get()
                    .addOnCompleteListener {
                        if (!it.result.exists()) {
                            val adRequest = AdRequest.Builder().build()

                            InterstitialAd.load(
                                this,
                                getString(R.string.max_elements_added_interstitial),
                                adRequest,
                                object : InterstitialAdLoadCallback() {
                                    override fun onAdFailedToLoad(adError: LoadAdError) {
                                    }

                                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                        interstitialAd.show(this@CreatorActivity)
                                    }
                                })

                        }
                    }
            }
        }
    }

    //Elements Creation.
    fun createTextElement(
        elementData: ElementData,
        gestureDetector: GestureDetector,
        scaleDetector: ScaleGestureDetector,
        position: Int
    ) {
        val data = elementData.data as TextData
        val view = CreatorTextBinding.inflate(LayoutInflater.from(this), null, false)

        view.creatorTextElement.text = data.text
        view.creatorTextElement.setTextColor(Color.parseColor(data.color))
        view.creatorTextElement.textSize = AppUtils.pxToDp(this, data.size).toFloat()
        view.creatorTextElement.tag = position
        view.creatorTextElement.id = elementData.id
        view.creatorTextElement.setPadding(AppUtils.pxToDp(this, data.backgroundPadding))

        //Set the background drawable.
        val backgroundDrawable =
            ContextCompat.getDrawable(this, R.drawable.element_bgr_layer_list)!!
                .mutate() as LayerDrawable

        //Set the outliner prefs.
        val outliner =
            ContextCompat.getDrawable(this, R.drawable.element_outline_drawable) as GradientDrawable
        adjustOutliner(outliner)

        val stateDrawable = backgroundDrawable.getDrawable(0) as StateListDrawable


        val prefs = getSharedPreferences(AppUtils.APP_PREFS, MODE_PRIVATE)
        val isHighlight = prefs.getBoolean(AppUtils.ELEMENT_HIGHLIGHT_KEY, true)

        if (isHighlight) {
            stateDrawable.addState(intArrayOf(android.R.attr.state_activated), outliner)
        }




        view.creatorTextElement.background = backgroundDrawable


        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.topToTop = binding.creatorCardLayout.id
        params.bottomToBottom = binding.creatorCardLayout.id
        params.startToStart = binding.creatorCardLayout.id
        params.endToEnd = binding.creatorCardLayout.id


        binding.creatorCardLayout.addView(view.creatorTextElement, params)


        moveElement(view.creatorTextElement, gestureDetector, scaleDetector)
    }

    fun createImageElement(
        elementData: ElementData,
        gestureDetector: GestureDetector,
        scaleDetector: ScaleGestureDetector,
        position: Int
    ) {
        val imageData = elementData.data as ImageData
        val view = CreatorImageBinding.inflate(LayoutInflater.from(this), null, false)
        view.creatorImageElement.id = elementData.id
        view.creatorImageElement.tag = position
        view.creatorImageElement.scaleType = imageData.scaleType!!.scaleType


        //Set the background drawable.
        val backgroundDrawable =
            ContextCompat.getDrawable(this, R.drawable.element_bgr_layer_list)!!
                .mutate() as LayerDrawable

        //Set the outliner prefs.
        val outliner =
            ContextCompat.getDrawable(this, R.drawable.element_outline_drawable) as GradientDrawable
        adjustOutliner(outliner)
        val stateDrawable = backgroundDrawable.getDrawable(0) as StateListDrawable
        val prefs = getSharedPreferences(AppUtils.APP_PREFS, MODE_PRIVATE)
        val isHighlight = prefs.getBoolean(AppUtils.ELEMENT_HIGHLIGHT_KEY, true)

        if (isHighlight) {
            stateDrawable.addState(intArrayOf(android.R.attr.state_activated), outliner)
        }


        view.creatorImageElement.background = backgroundDrawable


        val params = ConstraintLayout.LayoutParams(
            AppUtils.pxToDp(this, imageData.width),
            AppUtils.pxToDp(this, imageData.height)
        )
        params.topToTop = binding.creatorCardLayout.id
        params.bottomToBottom = binding.creatorCardLayout.id
        params.startToStart = binding.creatorCardLayout.id
        params.endToEnd = binding.creatorCardLayout.id


        binding.creatorCardLayout.addView(view.creatorImageElement, params)

        //Add temp image data to list.
        val tempUiImageData = TempUiImageData()
        tempUiImageData.elementId = elementData.id
        tempImageList.add(tempUiImageData)

        moveElement(view.creatorImageElement, gestureDetector, scaleDetector)

    }

    fun createBlockElement(
        elementData: ElementData,
        gestureDetector: GestureDetector,
        scaleDetector: ScaleGestureDetector,
        position: Int
    ) {
        val blockData = elementData.data as BlockData
        val view = CreatorBlockBinding.inflate(LayoutInflater.from(this), null, false)
        view.creatorBlockElement.id = elementData.id
        view.creatorBlockElement.tag = position

        //Set the background drawable.
        val backgroundDrawable =
            ContextCompat.getDrawable(this, R.drawable.element_bgr_layer_list)!!
                .mutate() as LayerDrawable
        val outliner =
            ContextCompat.getDrawable(this, R.drawable.element_outline_drawable) as GradientDrawable
        adjustOutliner(outliner)
        val stateDrawable = backgroundDrawable.getDrawable(0) as StateListDrawable
        val prefs = getSharedPreferences(AppUtils.APP_PREFS, MODE_PRIVATE)
        val isHighlight = prefs.getBoolean(AppUtils.ELEMENT_HIGHLIGHT_KEY, true)

        if (isHighlight) {
            stateDrawable.addState(intArrayOf(android.R.attr.state_activated), outliner)
        }


        view.creatorBlockElement.background = backgroundDrawable

        //Set the main shape drawable
        val mainDrawable =
            ContextCompat.getDrawable(this, R.drawable.block_element_shape)!!.mutate()
        view.creatorBlockElement.setImageDrawable(mainDrawable)


        val params = ConstraintLayout.LayoutParams(
            AppUtils.pxToDp(this, 250),
            AppUtils.pxToDp(this, 250)
        )
        params.topToTop = binding.creatorCardLayout.id
        params.bottomToBottom = binding.creatorCardLayout.id
        params.startToStart = binding.creatorCardLayout.id
        params.endToEnd = binding.creatorCardLayout.id


        binding.creatorCardLayout.addView(view.creatorBlockElement, params)

        moveElement(view.creatorBlockElement, gestureDetector, scaleDetector)
    }

    fun createGradientElement(
        elementData: ElementData,
        gestureDetector: GestureDetector,
        scaleDetector: ScaleGestureDetector,
        position: Int
    ) {
        val gradientData = elementData.data as GradientData

        val view = CreatorGradientBinding.inflate(LayoutInflater.from(this), null, false)
        view.creatorGradientElement.id = elementData.id
        view.creatorGradientElement.tag = position

        //Set the background drawable.
        val backgroundDrawable =
            ContextCompat.getDrawable(this, R.drawable.element_bgr_layer_list)!!
                .mutate() as LayerDrawable
        val outliner =
            ContextCompat.getDrawable(this, R.drawable.element_outline_drawable) as GradientDrawable
        adjustOutliner(outliner)
        val stateDrawable = backgroundDrawable.getDrawable(0) as StateListDrawable
        val prefs = getSharedPreferences(AppUtils.APP_PREFS, MODE_PRIVATE)
        val isHighlight = prefs.getBoolean(AppUtils.ELEMENT_HIGHLIGHT_KEY, true)

        if (isHighlight) {
            stateDrawable.addState(intArrayOf(android.R.attr.state_activated), outliner)
        }

        view.creatorGradientElement.background = backgroundDrawable

        //Set the main shape drawable
        val mainDrawable =
            ContextCompat.getDrawable(this, R.drawable.gradient_element_shape)!!.mutate()
        view.creatorGradientElement.setImageDrawable(mainDrawable)


        val params = ConstraintLayout.LayoutParams(
            AppUtils.pxToDp(this, gradientData.width),
            AppUtils.pxToDp(this, gradientData.height)
        )
        params.topToTop = binding.creatorCardLayout.id
        params.bottomToBottom = binding.creatorCardLayout.id
        params.startToStart = binding.creatorCardLayout.id
        params.endToEnd = binding.creatorCardLayout.id


        binding.creatorCardLayout.addView(view.creatorGradientElement, params)

        moveElement(view.creatorGradientElement, gestureDetector, scaleDetector)
    }

    fun createPatternElement(
        elementData: ElementData,
        gestureDetector: GestureDetector,
        scaleDetector: ScaleGestureDetector,
        position: Int
    ) {
        val patternElementData = elementData.data as PatternElementData

        val view = CreatorPatternBinding.inflate(LayoutInflater.from(this), null, false)
        view.creatorPatternElement.id = elementData.id
        view.creatorPatternElement.tag = position

        //Set the background drawable.
        val backgroundDrawable =
            ContextCompat.getDrawable(this, R.drawable.element_bgr_layer_list)!!
                .mutate() as LayerDrawable
        val outliner =
            ContextCompat.getDrawable(this, R.drawable.element_outline_drawable) as GradientDrawable
        adjustOutliner(outliner)
        val stateDrawable = backgroundDrawable.getDrawable(0) as StateListDrawable
        val prefs = getSharedPreferences(AppUtils.APP_PREFS, MODE_PRIVATE)
        val isHighlight = prefs.getBoolean(AppUtils.ELEMENT_HIGHLIGHT_KEY, true)

        if (isHighlight) {
            stateDrawable.addState(intArrayOf(android.R.attr.state_activated), outliner)
        }


        view.creatorPatternElement.background = backgroundDrawable


        val params = ConstraintLayout.LayoutParams(
            AppUtils.pxToDp(this, patternElementData.width),
            AppUtils.pxToDp(this, patternElementData.height)
        )
        params.topToTop = binding.creatorCardLayout.id
        params.bottomToBottom = binding.creatorCardLayout.id
        params.startToStart = binding.creatorCardLayout.id
        params.endToEnd = binding.creatorCardLayout.id


        binding.creatorCardLayout.addView(view.creatorPatternElement, params)

        moveElement(view.creatorPatternElement, gestureDetector, scaleDetector)
    }

    fun createAudioElement(
        elementData: ElementData,
        gestureDetector: GestureDetector,
        scaleDetector: ScaleGestureDetector,
        position: Int
    ) {
        val audioData = elementData.data as AudioData
        val view = CreatorAudioBinding.inflate(LayoutInflater.from(this), null, false)
        view.creatorAudioElement.id = elementData.id
        view.creatorAudioElement.tag = position

        //Set the background drawable.
        val backgroundDrawable =
            ContextCompat.getDrawable(this, R.drawable.element_bgr_layer_list)!!
                .mutate() as LayerDrawable
        val outliner =
            ContextCompat.getDrawable(this, R.drawable.element_outline_drawable) as GradientDrawable
        adjustOutliner(outliner)
        val stateDrawable = backgroundDrawable.getDrawable(0) as StateListDrawable
        val prefs = getSharedPreferences(AppUtils.APP_PREFS, MODE_PRIVATE)
        val isHighlight = prefs.getBoolean(AppUtils.ELEMENT_HIGHLIGHT_KEY, true)

        if (isHighlight) {
            stateDrawable.addState(intArrayOf(android.R.attr.state_activated), outliner)
        }


        view.creatorAudioElement.background = backgroundDrawable


        val params = ConstraintLayout.LayoutParams(
            AppUtils.pxToDp(this, 250),
            AppUtils.pxToDp(this, 250)
        )
        params.topToTop = binding.creatorCardLayout.id
        params.bottomToBottom = binding.creatorCardLayout.id
        params.startToStart = binding.creatorCardLayout.id
        params.endToEnd = binding.creatorCardLayout.id


        binding.creatorCardLayout.addView(view.creatorAudioElement, params)

        moveElement(view.creatorAudioElement, gestureDetector, scaleDetector)
    }


    fun setFocusedElement(position: Int) {
        if (position == elementsList.size) {
            return
        }

        //If the current position is -1, then the focused element is the main parent card.
        if (position == -1) {
            isMainFocused = true

            //Remove outline indicator showing current selected element.
            if (elementsList.isNotEmpty()) {
                binding.creatorCardLayout.findViewById<View>(elementsList[adapter.currentFocusedElement].id).isActivated =
                    false
            }

            mainOptionsAdapter.currentSelectedOption = CURRENT_SELECTED_MAIN_OPTION

            binding.elementOptionsRecy.adapter = mainOptionsAdapter

            elementsControlsMedium.handleMainOptionSelection(CURRENT_SELECTED_MAIN_OPTION!!)

            handleElementsOptionsBarVisibility()

            return
        }

        isMainFocused = false

        //Update the outline to show on the focused view.
        val prevId = elementsList[adapter.currentFocusedElement].id
        val currentId = elementsList[position].id

        if (elementsList[position].elementType == Elements.MAIN) {
            return
        }


        binding.creatorCardLayout.findViewById<View>(prevId).isActivated = false
        binding.creatorCardLayout.findViewById<View>(currentId).isActivated = true

        binding.creatorCardLayout.bringChildToFront(
            binding.creatorCardLayout.findViewById<View>(
                currentId
            )
        )

        //Update the adapter.
        runBlocking {
            CoroutineScope(Dispatchers.Main).launch {
                val prevPosition = adapter.currentFocusedElement
                adapter.currentFocusedElement = position

                adapter.notifyItemChanged(prevPosition)
                adapter.notifyItemChanged(position)
            }

            //Update the elements bar and options bar.
            val elementData = elementsList[position]
            when (elementData.elementType) {

                Elements.TEXT_TYPE -> {
                    //Adjust the options bar recy and controls with type.
                    CoroutineScope(Dispatchers.Main).launch {
                        textOptionsAdapter.currentSelectedOption = elementData.currentSelection

                        binding.elementOptionsRecy.adapter = textOptionsAdapter

                        elementsControlsMedium.handleTextOptionSelection(elementData.currentSelection!!)
                    }
                }

                Elements.IMAGE_TYPE -> {
                    //Adjust the options bar recy with type.
                    CoroutineScope(Dispatchers.Main).launch {
                        imageOptionsAdapter.currentSelectedOption = elementData.currentSelection

                        binding.elementOptionsRecy.adapter = imageOptionsAdapter
                        elementsControlsMedium.handleImageOptionSelection(elementData.currentSelection!!)
                    }
                }

                Elements.BLOCK_TYPE -> {
                    //Adjust the options bar recy with type.
                    CoroutineScope(Dispatchers.Main).launch {
                        blockOptionsAdapter.currentSelectedOption = elementData.currentSelection


                        binding.elementOptionsRecy.adapter = blockOptionsAdapter

                        elementsControlsMedium.handleBlockOptionSelection(elementData.currentSelection!!)
                    }
                }

                Elements.GRADIENT_TYPE -> {
                    //Adjust the options bar recy and controls with type.
                    CoroutineScope(Dispatchers.Main).launch {
                        gradientOptionsAdapter.currentSelectedOption = elementData.currentSelection

                        binding.elementOptionsRecy.adapter =
                            gradientOptionsAdapter

                        elementsControlsMedium.handleGradientOptionSelection(elementData.currentSelection!!)

                    }
                }

                Elements.PATTERN_TYPE -> {
                    //Adjust the options bar recy and controls with type.
                    CoroutineScope(Dispatchers.Main).launch {
                        patternOptionsAdapter.currentSelectedOption = elementData.currentSelection
                        binding.elementOptionsRecy.adapter =
                            patternOptionsAdapter

                        elementsControlsMedium.handlePatternOptionSelection(elementData.currentSelection!!)
                    }
                }

                Elements.AUDIO_TYPE -> {
                    //Adjust the options bar recy and controls with type.
                    CoroutineScope(Dispatchers.Main).launch {
                        audioOptionsAdapter.currentSelectedOption = elementData.currentSelection
                        binding.elementOptionsRecy.adapter =
                            audioOptionsAdapter

                        elementsControlsMedium.handleAudioOptionSelection(elementData.currentSelection!!)
                    }
                }

                else -> {

                }
            }
        }



        handleElementsOptionsBarVisibility()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun moveElement(view: View, gestureDetector: GestureDetector, sd: ScaleGestureDetector) {
        var movedX = 0f
        var movedY = 0f

        var centerX = 0f
        var centerY = 0f

        var rotateX = 0f
        var rotateY = 0f
        view.setOnTouchListener { v, event ->

            val params = v.layoutParams as ConstraintLayout.LayoutParams

            val set = ConstraintSet()
            set.clone(binding.creatorCardLayout)
            when (event.action and MotionEvent.ACTION_MASK) {

                MotionEvent.ACTION_DOWN -> {
                    movedX = v.x - event.rawX
                    movedY = v.y - event.rawY
                    centerX = v.x + (binding.root as View).x + v.width / 2;

                    centerY = v.y + 10 + v.height / 2;


                    rotateX = event.rawX
                    rotateY = event.rawY

                    //Update the focused element.
                    for (i in 0 until elementsList.size) {
                        if (elementsList[i].id == v.id) {
                            setFocusedElement(i)
                            break
                        }
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    val xValue = event.rawX.toInt() + movedX
                    val yValue = event.rawY.toInt() + movedY


                    //Add half of width and height to x and y values respectively to shift the points to middle of the text
                    val mX = xValue + v.width / 2
                    val mY = yValue + v.height / 2
                    if (mX < binding.creatorCardBoundPlaceHolderTR.x && mX > binding.creatorCardBoundPlaceHolderBL.x &&
                        mY > binding.creatorCardBoundPlaceHolderTR.y && mY < binding.creatorCardBoundPlaceHolderBL.y
                    ) {
                        v.x = xValue
                        v.y = yValue


                        /*if (xValue == mainCardData.width.toFloat()/2 - 100) {
                            binding.creatorCardVertLine.visibility = View.VISIBLE
                            binding.creatorCardHorzLine.visibility = View.VISIBLE
                        }
                        else {
                            binding.creatorCardVertLine.visibility = View.GONE
                            binding.creatorCardHorzLine.visibility = View.GONE
                        }*/

                        elementsList[v.tag as Int].xPosition = xValue
                        elementsList[v.tag as Int].yPosition = yValue
                    }
                }

                MotionEvent.ACTION_UP -> {
                }
            }


            v.invalidate()

            //Notify gesture detector.
            sd.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)

            true

        }

    }

    fun adjustElementsBar() {
        //Adjust the visibility
        if (elementsList.isEmpty()) {
            binding.creatorAddedElementsRecy.visibility = View.GONE
            binding.creatorNoElementsText.visibility = View.VISIBLE
        } else {
            binding.creatorAddedElementsRecy.visibility = View.VISIBLE
            binding.creatorNoElementsText.visibility = View.GONE

        }
    }

    fun handleHeaderSpaceVisibility() {
        if (headerVisible) {
            binding.creatorHeaderSpace.visibility = View.GONE
            headerVisible = false
        } else {
            binding.creatorHeaderSpace.visibility = View.VISIBLE
            headerVisible = true
        }
    }

    fun handleElementsOptionsBarVisibility() {
        if (isMainFocused) {
            binding.elementOptionsRecy.visibility = View.VISIBLE
            binding.elementOptionControlsContainer.visibility = View.VISIBLE
            binding.creatorAddedElementsRecy.visibility = View.VISIBLE

        } else {
            if (elementsList.isEmpty()) {
                binding.elementOptionsRecy.visibility = View.GONE
                binding.elementOptionControlsContainer.visibility = View.GONE
                binding.creatorAddedElementsRecy.visibility = View.GONE


            } else {
                binding.elementOptionsRecy.visibility = View.VISIBLE
                binding.elementOptionControlsContainer.visibility = View.VISIBLE
                binding.creatorAddedElementsRecy.visibility = View.VISIBLE

            }
        }
    }
}