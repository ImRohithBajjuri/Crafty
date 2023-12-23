package com.rb.crafty.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.provider.FontsContractCompat
import androidx.core.view.doOnLayout
import androidx.core.view.setPadding
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.makeramen.roundedimageview.RoundedImageView
import com.rb.crafty.R
import com.rb.crafty.databinding.GreetrCardThumbnailLayoutBinding
import com.rb.crafty.dataObjects.*
import com.rb.crafty.databinding.CreatorAudioBinding
import com.rb.crafty.databinding.CreatorGradientBinding
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream

class Elements {
    lateinit var context: Context

    lateinit var storageReference: StorageReference
    var user: FirebaseUser? = null
    lateinit var firestore: FirebaseFirestore

    constructor(context: Context) {
        this.context = context
    }

    init {
        storageReference = Firebase.storage.reference
        firestore = Firebase.firestore
        user = Firebase.auth.currentUser
    }

    companion object {
        //Elements.
        val MAIN = "main"
        val TEXT_TYPE = "text"
        val IMAGE_TYPE = "image"
        val BLOCK_TYPE = "block"
        val GRADIENT_TYPE = "gradient"
        val PATTERN_TYPE = "pattern"
        val AUDIO_TYPE = "audio"

        //Element options.
        //Text options.
        val TEXT_COLOR = "textColor"
        val TEXT_SIZE = "textSize"
        val TEXT_ZOOM = "textZoom"
        val TEXT_INPUT = "textInput"
        val TEXT_FONT = "textFont"
        val TEXT_ROTATE = "textRotate"
        val TEXT_DEPTH = "textDepth"
        val TEXT_BACKGROUND = "textBackground"

        //Image options.
        val IMAGE_INPUT = "imageInput"
        val IMAGE_CROP = "imageCrop"
        val IMAGE_COMPRESSION = "imageQuality"
        val IMAGE_FILTERS = "imageFilters"
        val IMAGE_ROTATION = "imageRotation"
        val IMAGE_CORNERS = "imageCorners"
        val IMAGE_ZOOM = "imageZoom"
        val IMAGE_SCALE_TYPE = "imageScaleType"
        val IMAGE_SIZE = "imageSize"
        val IMAGE_BACKGROUND = "imageBackground"

        //Block options.
        val BLOCK_COLOR = "blockColor"
        val BLOCK_SIZE = "blockSize"
        val BLOCK_ROTATION = "blockRotation"
        val BLOCK_CORNERS = "blockCorners"
        val BLOCK_ZOOM = "blockZoom"
        val BLOCK_SHADOW = "blockShadow"
        val BLOCK_STROKE = "blockStroke"
        val BLOCK_BACKGROUND = "blockBackground"

        //Gradient options.
        val GRADIENT_COLORS = "gradientColor"
        val GRADIENT_SIZE = "gradientSize"
        val GRADIENT_ROTATION = "gradientRotation"
        val GRADIENT_CORNERS = "gradientCorners"
        val GRADIENT_ZOOM = "gradientZoom"
        val GRADIENT_SHADOW = "gradientShadow"
        val GRADIENT_STROKE = "gradientStroke"
        val GRADIENT_BACKGROUND = "gradientBackground"

        //Pattern options.
        val PATTERN_DESIGN = "patternDesign"
        val PATTERN_SIZE = "patternSize"
        val PATTERN_ROTATION = "patternRotation"
        val PATTERN_CORNERS = "patternCorners"
        val PATTERN_ZOOM = "patternZoom"
        val PATTERN_SHADOW = "patternShadow"
        val PATTERN_STROKE = "patternStroke"
        val PATTERN_BACKGROUND = "patternBackground"

        //Audio options.
        val AUDIO_INPUT = "audioInput"
        val AUDIO_SIZE = "audioSize"
        val AUDIO_ROTATION = "audioRotation"
        val AUDIO_CORNERS = "audioCorners"
        val AUDIO_ZOOM = "audioZoom"
        val AUDIO_SHADOW = "audioShadow"
        val AUDIO_STROKE = "audioStroke"
        val AUDIO_BACKGROUND = "audioBackground"
    }

    interface ThumbnailLoaderListener {
        fun thumbnailLoaded(view: MaterialCardView)
    }

    interface ElementBuilderListener {
        fun onElementReady(view: View)

        fun onElementFailed()
    }

    interface ImageElementBytesListener {
       fun onBytesReady(bytes: ByteArray)
    }

    fun textOptions(): MutableList<ElementOptionData> {
        val textOptionsList = kotlin.collections.ArrayList<ElementOptionData>()
        textOptionsList.add(
            ElementOptionData(
                "Input",
                TEXT_INPUT,
                TEXT_TYPE)
        )
        textOptionsList.add(
            ElementOptionData(
                "Color",
                TEXT_COLOR,
                TEXT_TYPE)
        )
        textOptionsList.add(
            ElementOptionData(
                "Size",
                TEXT_SIZE,
                TEXT_TYPE)
        )
        textOptionsList.add(
            ElementOptionData(
                "Font",
                TEXT_FONT,
                TEXT_TYPE)
        )
        textOptionsList.add(
            ElementOptionData(
                "Rotation",
                TEXT_ROTATE,
                TEXT_TYPE)
        )
        textOptionsList.add(
            ElementOptionData(
                "Zoom",
                TEXT_ZOOM,
                TEXT_TYPE)
        )
        textOptionsList.add(
            ElementOptionData(
                "Depth",
                TEXT_DEPTH,
                TEXT_TYPE)
        )
        textOptionsList.add(
            ElementOptionData(
                "Background",
                TEXT_BACKGROUND,
                TEXT_TYPE)
        )

        return textOptionsList
    }

    fun imageOptions(): MutableList<ElementOptionData> {
        val imageOptionsList = kotlin.collections.ArrayList<ElementOptionData>()
        imageOptionsList.add(
            ElementOptionData(
                "Input",
                IMAGE_INPUT,
                IMAGE_TYPE)
        )
        imageOptionsList.add(
            ElementOptionData(
                "Crop",
                IMAGE_CROP,
                IMAGE_TYPE)
        )
        imageOptionsList.add(
            ElementOptionData(
                "Quality",
                IMAGE_COMPRESSION,
                IMAGE_TYPE)
        )
        imageOptionsList.add(
            ElementOptionData(
                "Filters",
                IMAGE_FILTERS,
                IMAGE_TYPE)
        )
        imageOptionsList.add(
            ElementOptionData(
                "Size",
                IMAGE_SIZE,
                IMAGE_TYPE)
        )
        imageOptionsList.add(
            ElementOptionData(
                "Zoom",
                IMAGE_ZOOM,
                IMAGE_TYPE)
        )
        imageOptionsList.add(
            ElementOptionData(
                "Scale",
                IMAGE_SCALE_TYPE,
                IMAGE_TYPE)
        )
        imageOptionsList.add(
            ElementOptionData(
                "Rotation",
                IMAGE_ROTATION,
                IMAGE_TYPE)
        )
        imageOptionsList.add(
            ElementOptionData(
                "Corners",
                IMAGE_CORNERS,
                IMAGE_TYPE)
        )
        imageOptionsList.add(
            ElementOptionData(
                "Background",
                IMAGE_BACKGROUND,
                IMAGE_TYPE)
        )

        return imageOptionsList
    }

    fun blockOptions(): MutableList<ElementOptionData> {
        val blockOptionsList = ArrayList<ElementOptionData>()
        blockOptionsList.add(
            ElementOptionData(
                "Color",
                BLOCK_COLOR,
                BLOCK_TYPE)
        )
        blockOptionsList.add(
            ElementOptionData(
                "Size",
                BLOCK_SIZE,
                BLOCK_TYPE)
        )
        blockOptionsList.add(
            ElementOptionData(
                "Zoom",
                BLOCK_ZOOM,
                BLOCK_TYPE)
        )
        blockOptionsList.add(
            ElementOptionData(
                "Rotation",
                BLOCK_ROTATION,
                BLOCK_TYPE)
        )
        blockOptionsList.add(
            ElementOptionData(
                "Corners",
                BLOCK_CORNERS,
                BLOCK_TYPE)
        )
        blockOptionsList.add(
            ElementOptionData(
                "Stroke",
                BLOCK_STROKE,
                BLOCK_TYPE)
        )
        blockOptionsList.add(
            ElementOptionData(
                "Shadow",
                BLOCK_SHADOW,
                BLOCK_TYPE)
        )
        blockOptionsList.add(
            ElementOptionData(
                "Background",
                BLOCK_BACKGROUND,
                BLOCK_TYPE)
        )

        return blockOptionsList
    }

    fun gradientOptions(): MutableList<ElementOptionData> {
        val gradientOptionsList = ArrayList<ElementOptionData>()
        gradientOptionsList.add(
            ElementOptionData(
                "Colors",
                GRADIENT_COLORS,
                GRADIENT_TYPE)
        )
        gradientOptionsList.add(
            ElementOptionData(
                "Size",
                GRADIENT_SIZE,
                GRADIENT_TYPE)
        )
        gradientOptionsList.add(
            ElementOptionData(
                "Zoom",
                GRADIENT_ZOOM,
                GRADIENT_TYPE)
        )
        gradientOptionsList.add(
            ElementOptionData(
                "Rotation",
                GRADIENT_ROTATION,
                GRADIENT_TYPE)
        )
        gradientOptionsList.add(
            ElementOptionData(
                "Corners",
                GRADIENT_CORNERS,
                GRADIENT_TYPE)
        )
        gradientOptionsList.add(
            ElementOptionData(
                "Shadow",
                GRADIENT_SHADOW,
                GRADIENT_TYPE)
        )
        gradientOptionsList.add(
            ElementOptionData(
                "Stroke",
                GRADIENT_STROKE,
                GRADIENT_TYPE)
        )
        gradientOptionsList.add(
            ElementOptionData(
                "Background",
                GRADIENT_BACKGROUND,
                GRADIENT_TYPE)
        )

        return gradientOptionsList
    }

    fun patternOptions(): MutableList<ElementOptionData> {
        val patternOptionsList = ArrayList<ElementOptionData>()
        patternOptionsList.add(
            ElementOptionData(
                "Patterns",
                PATTERN_DESIGN,
                PATTERN_TYPE)
        )
        patternOptionsList.add(
            ElementOptionData(
                "Size",
                PATTERN_SIZE,
                PATTERN_TYPE)
        )
        patternOptionsList.add(
            ElementOptionData(
                "Zoom",
                PATTERN_ZOOM,
                PATTERN_TYPE)
        )
        patternOptionsList.add(
            ElementOptionData(
                "Rotation",
                PATTERN_ROTATION,
                PATTERN_TYPE)
        )
        patternOptionsList.add(
            ElementOptionData(
                "Corners",
                PATTERN_CORNERS,
                PATTERN_TYPE)
        )
        patternOptionsList.add(
            ElementOptionData(
                "Shadow",
                PATTERN_SHADOW,
                PATTERN_TYPE)
        )
        patternOptionsList.add(
            ElementOptionData(
                "Stroke",
                PATTERN_STROKE,
                PATTERN_TYPE)
        )
        patternOptionsList.add(
            ElementOptionData(
                "Background",
                PATTERN_BACKGROUND,
                PATTERN_TYPE)
        )

        return patternOptionsList
    }

    fun audioOptions(): MutableList<ElementOptionData> {
        val audioOptionsList = ArrayList<ElementOptionData>()
        audioOptionsList.add(
            ElementOptionData(
                "Audio",
                AUDIO_INPUT,
                AUDIO_TYPE)
        )
        audioOptionsList.add(
            ElementOptionData(
                "Size",
                AUDIO_SIZE,
                AUDIO_TYPE)
        )
        audioOptionsList.add(
            ElementOptionData(
                "Zoom",
                AUDIO_ZOOM,
                AUDIO_TYPE)
        )

        audioOptionsList.add(
            ElementOptionData(
                "Rotation",
                AUDIO_ROTATION,
                AUDIO_TYPE)
        )
        audioOptionsList.add(
            ElementOptionData(
                "Corners",
                AUDIO_CORNERS,
                AUDIO_TYPE)
        )
        audioOptionsList.add(
            ElementOptionData(
                "Stroke",
                AUDIO_STROKE,
                AUDIO_TYPE)
        )
        audioOptionsList.add(
            ElementOptionData(
                "Shadow",
                AUDIO_SHADOW,
                AUDIO_TYPE)
        )
        audioOptionsList.add(
            ElementOptionData(
                "Background",
                AUDIO_BACKGROUND,
                AUDIO_TYPE)
        )

        return audioOptionsList
    }


    fun textOptionDrawables(): MutableList<Int> {
        val drawableList = ArrayList<Int>()

        drawableList.add(R.drawable.ic_round_text_format_24)
        drawableList.add(R.drawable.ic_round_color_lens_24)
        drawableList.add(R.drawable.ic_round_format_size_24)
        drawableList.add(R.drawable.ic_round_font_24)
        drawableList.add(R.drawable.ic_round_crop_rotate_24)
        drawableList.add(R.drawable.zoom_in_24dp)
        drawableList.add(R.drawable.ic_round_flip_to_front_24)
        drawableList.add(R.drawable.ic_round_rectangle_24)
        return drawableList
    }

    fun imageOptionDrawables(): MutableList<Int> {
        val drawableList = ArrayList<Int>()

        drawableList.add(R.drawable.ic_round_image_24)
        drawableList.add(R.drawable.ic_round_crop_free_24)
        drawableList.add( R.drawable.compress_white_24dp)
        drawableList.add(R.drawable.filter_24dp)
        drawableList.add(R.drawable.ic_round_photo_size_24)
        drawableList.add(R.drawable.zoom_in_24dp)
        drawableList.add(R.drawable.ic_round_image_aspect_ratio_24)
        drawableList.add(R.drawable.ic_round_rounded_corner_24)
        drawableList.add(R.drawable.ic_round_rectangle_24)
        drawableList.add(R.drawable.ic_round_crop_rotate_24)

        return drawableList
    }

    fun blockOptionDrawables(): MutableList<Int> {
        val drawableList = ArrayList<Int>()

        drawableList.add(R.drawable.ic_round_color_lens_24)
        drawableList.add(R.drawable.ic_round_block_size_24)
        drawableList.add(R.drawable.zoom_in_24dp)
        drawableList.add(R.drawable.ic_round_crop_rotate_24)
        drawableList.add(R.drawable.ic_round_block_outline_24)
        drawableList.add(R.drawable.ic_round_rounded_corner_24)
        drawableList.add(R.drawable.ic_round_flip_to_front_24)
        drawableList.add(R.drawable.ic_round_rectangle_24)

        return drawableList
    }

    fun gradientOptionDrawables(): MutableList<Int> {
        val drawableList = ArrayList<Int>()
        drawableList.add(R.drawable.ic_round_gradient_24)
        drawableList.add(R.drawable.ic_round_block_size_24)
        drawableList.add(R.drawable.zoom_in_24dp)
        drawableList.add(R.drawable.ic_round_crop_rotate_24)
        drawableList.add(R.drawable.ic_round_rounded_corner_24)
        drawableList.add(R.drawable.ic_round_flip_to_front_24)
        drawableList.add(R.drawable.ic_round_block_outline_24)
        drawableList.add(R.drawable.ic_round_rectangle_24)
        return drawableList
    }

    fun patternOptionDrawable(): MutableList<Int> {
        val drawableList = ArrayList<Int> ()

        drawableList.add(R.drawable.ic_round_design_services_24)
        drawableList.add(R.drawable.ic_round_block_size_24)
        drawableList.add(R.drawable.zoom_in_24dp)
        drawableList.add(R.drawable.ic_round_crop_rotate_24)
        drawableList.add(R.drawable.ic_round_rounded_corner_24)
        drawableList.add(R.drawable.ic_round_flip_to_front_24)
        drawableList.add(R.drawable.ic_round_block_outline_24)
        drawableList.add(R.drawable.ic_round_rectangle_24)

        return drawableList
    }

    fun audioOptionDrawables(): MutableList<Int> {
        val drawableList = ArrayList<Int>()

        drawableList.add(R.drawable.audio_24dp)
        drawableList.add(R.drawable.ic_round_block_size_24)
        drawableList.add(R.drawable.zoom_in_24dp)
        drawableList.add(R.drawable.ic_round_crop_rotate_24)
        drawableList.add(R.drawable.ic_round_block_outline_24)
        drawableList.add(R.drawable.ic_round_rounded_corner_24)
        drawableList.add(R.drawable.ic_round_flip_to_front_24)
        drawableList.add(R.drawable.ic_round_rectangle_24)

        return drawableList
    }


    @SuppressLint("UnsafeOptInUsageError")
    fun makeGreetrCardThumb(
        cardData: CardData,
        listener: ThumbnailLoaderListener
    ) {
        val binding =
            GreetrCardThumbnailLayoutBinding.inflate(LayoutInflater.from(context), null, false)
        val mainCardData = cardData.mainCardData


        //Build the thumbnail.

        //Set the main card attributes and build the elements.
        CoroutineScope(Dispatchers.Main).launch {

            //Build the main card.
            buildMainCard(binding, mainCardData!!)

            //Add all the available elements.
            buildElements(
                binding,
                cardData.elementsList!!,
                cardData
            )

            //send the thumbnail.
            listener.thumbnailLoaded(binding.greetrThumbnailCard)
        }
    }

    suspend fun buildMainCard(
        binding: GreetrCardThumbnailLayoutBinding, mainCardData: MainCardData
    ) {
        //Card width and height.
        binding.greetrThumbnailCard.measure(
            View.MeasureSpec.makeMeasureSpec(
                mainCardData.width, View.MeasureSpec.EXACTLY
            ), View.MeasureSpec.makeMeasureSpec(
                mainCardData.height, View.MeasureSpec.EXACTLY
            )
        )
        binding.greetrThumbnailCard.layout(
            0,
            0,
            binding.greetrThumbnailCard.measuredWidth,
            binding.greetrThumbnailCard.measuredHeight
        )

        //Card zoom.
        binding.greetrThumbnailCard.scaleX = mainCardData.scaleX
        binding.greetrThumbnailCard.scaleY = mainCardData.scaleY

        //Corner Radius.
        binding.greetrThumbnailCard.radius =
            AppUtils.pxToDp(context, mainCardData.cornerRadius).toFloat()
        /* val corners = floatArrayOf(
             mainCardData.cornerRadius.toFloat(),  mainCardData.cornerRadius.toFloat(),   // Top left radius in px
             mainCardData.cornerRadius.toFloat(),  mainCardData.cornerRadius.toFloat(),   // Top right radius in px
             mainCardData.cornerRadius.toFloat(),  mainCardData.cornerRadius.toFloat(),     // Bottom right radius in px
             mainCardData.cornerRadius.toFloat(),  mainCardData.cornerRadius.toFloat()      // Bottom left radius in px
         )
         val path = Path()
         val paint = Paint()
         paint.color = Color.TRANSPARENT
         path.addRoundRect(RectF(mainCardData.width.toFloat(), 0f, 0f, mainCardData.height.toFloat()), corners, Path.Direction.CW)
         canvas.drawPath(path, paint)*/

        //Card stroke.
        binding.greetrThumbnailCard.strokeWidth = AppUtils.pxToDp(context, mainCardData.strokeWidth)
        binding.greetrThumbnailCard.strokeColor = Color.parseColor(mainCardData.strokeColor)

        //Card rotation.
        binding.greetrThumbnailCard.rotation = mainCardData.rotation.toFloat()

        //Card foreground.
        when (mainCardData.foregroundType) {
            AppUtils.MAIN_FOREGROUND_COLOR -> {
                binding.greetrThumbnailCard.setCardBackgroundColor(
                    Color.parseColor(
                        mainCardData.color
                    )
                )


            }
            AppUtils.MAIN_FOREGROUND_GRADIENT -> {
                val intColorArray = intArrayOf(
                    Color.parseColor(mainCardData.gradientColors!![0]),
                    Color.parseColor(mainCardData.gradientColors!![1])
                )

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

                //Set the corners that are same as card corners.
                gradient.cornerRadius = AppUtils.pxToDp(context, mainCardData.cornerRadius).toFloat()

                //Update the UI.
                binding.greetrThumbnailCard.background = gradient
            }
            AppUtils.MAIN_FOREGROUND_PATTERN -> {
                val params = ConstraintLayout.LayoutParams(0, 0)
                params.topToTop = binding.greetrThumbnailCardLayout.id
                params.bottomToBottom = binding.greetrThumbnailCardLayout.id
                params.startToStart = binding.greetrThumbnailCardLayout.id
                params.endToEnd = binding.greetrThumbnailCardLayout.id

                var view: View
                withContext(Dispatchers.IO) {
                    view = Patterns(context).loadPatternWithViewId(
                        mainCardData.patternData!!, mainCardData.width, mainCardData.height
                    )!!
                }


                binding.greetrThumbnailCardLayout.addView(view, params)
            }
        }

    }

   private suspend @androidx.media3.common.util.UnstableApi fun buildElements(
       binding: GreetrCardThumbnailLayoutBinding,
       elementsList: MutableList<ElementData>,
       cardData: CardData
    ) {
        val gson = Gson()
        for (element in elementsList) {
            when (element.elementType) {
                TEXT_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson<TextData>(hashMapTree, TextData::class.java)
                    val textView = TextView(context)
                    textView.id = element.id


                    //Text input.
                    textView.text = data.text

                    //Text size.
                    textView.textSize = AppUtils.pxToSp(context, data.size).toFloat()

                    //Text font.
                    if (data.font != null) {
                        AppUtils.getTypeFace(
                            context,
                            data.font!!,
                            object : FontsContractCompat.FontRequestCallback() {
                                override fun onTypefaceRetrieved(typeface: Typeface?) {
                                    textView.typeface = typeface
                                    super.onTypefaceRetrieved(typeface)
                                }

                                override fun onTypefaceRequestFailed(reason: Int) {
                                    super.onTypefaceRequestFailed(reason)
                                }
                            })
                    }

                    //Text color,
                    textView.setTextColor(Color.parseColor(data.color))

                    //Text zoom.
                    textView.scaleX = data.scaleX
                    textView.scaleY = data.scaleY

                    //Text Rotation.
                    textView.rotation = data.rotation.toFloat()

                    //Text depth.
                    textView.setShadowLayer(
                        data.depthRadius.toFloat(),
                        data.horzDepth.toFloat(),
                        data.vertDepth.toFloat(),
                        Color.parseColor(data.depthColor)
                    )

                    //Text background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))
                    textView.background = bgr

                    textView.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))

                    //Text position.
                    binding.greetrThumbnailCard.doOnLayout {
                        textView.x = element.xPosition
                        textView.y = element.yPosition
                    }



                    //Add the view.
                    val params = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.topToTop = binding.greetrThumbnailCardLayout.id
                    params.bottomToBottom = binding.greetrThumbnailCardLayout.id
                    params.startToStart = binding.greetrThumbnailCardLayout.id
                    params.endToEnd = binding.greetrThumbnailCardLayout.id
                    binding.greetrThumbnailCardLayout.addView(textView)

                }

                IMAGE_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson(hashMapTree, ImageData::class.java)
                    var bitmap: Bitmap? = null

                    val imageView = RoundedImageView(context)
                    imageView.id = element.id

                    //Image input and compression.
                    if (AppUtils.hasStoragePermission(context)) {
                        //Check if the cache file exists.
                        val file = File(data.localPath)
                        if (file.exists()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Glide.with(context).asBitmap().load(file).apply(RequestOptions().encodeFormat(Bitmap.CompressFormat.PNG).encodeQuality(data.quality)).into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        bitmap = resource
                                        imageView.setImageBitmap(bitmap)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }

                                })
                            }
                        } else {
                            if (user == null) {
                                return
                            }
                            val path = data.storagePath.ifEmpty {
                                "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${element.creatorId}/${AppUtils.IMAGE_ELEMENTS_REFERENCE}/${element.id}"
                            }

                            val ref = storageReference.child(path)


                            ref.getStream { state, stream ->
                                Glide.with(context).asBitmap().load(stream).apply(RequestOptions().encodeFormat(Bitmap.CompressFormat.JPEG).encodeQuality(data.quality)).into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        bitmap = resource
                                        imageView.setImageBitmap(bitmap)

                                        //Write the image to cache and update the local path.
                                        runBlocking {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                val cachePath = writeImageElementToCache(element,
                                                    bitmap!!
                                                )
                                                data.localPath = cachePath
                                            }
                                        }
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }

                                })
                            }
                        }
                    } else {
                        AppUtils.buildStoragePermission(context).show()
                    }


                    //Image rotation.
                    imageView.rotation = data.rotation.toFloat()

                    //Image filter.
                    CoroutineScope(Dispatchers.IO).launch {
                        ImageFilters(context).getFilterWithName(data.filterName, object :
                            ImageFilters.ImageFiltersListener {
                            override fun onFiltersWithImageLoaded(loadedFilters: MutableList<Bitmap>) {
                            }

                            override fun onFilterReadyWithName(filter: ImageFilterData) {
                                if (filter.filter != null) {
                                    if (bitmap != null) {
                                        CoroutineScope(Dispatchers.Default).launch {
                                            val gpuImage = GPUImage(context)
                                            gpuImage.setImage(bitmap)
                                            gpuImage.setFilter(filter.filter)
                                            val filterBit = gpuImage.bitmapWithFilterApplied
                                            withContext(Dispatchers.Main) {
                                                imageView.setImageBitmap(filterBit)
                                            }
                                        }
                                    }
                                }
                            }

                        })
                    }

                    //Image corners.
                    imageView.cornerRadius = AppUtils.pxToDp(context, data.corners).toFloat()

                    //Image zoom.
                    imageView.scaleX = data.scaleX
                    imageView.scaleY = data.scaleY

                    //Image scale type.
                    imageView.scaleType = data.scaleType!!.scaleType

                    //Image width and height.
                    val params = ConstraintLayout.LayoutParams(
                        AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height)
                    )
                    params.topToTop = binding.greetrThumbnailCardLayout.id
                    params.bottomToBottom = binding.greetrThumbnailCardLayout.id
                    params.startToStart = binding.greetrThumbnailCardLayout.id
                    params.endToEnd = binding.greetrThumbnailCardLayout.id
                    imageView.layoutParams = params

                    //Image background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))

                    imageView.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))
                    imageView.background = bgr

                    //Image position.
                    imageView.x = element.xPosition
                    imageView.y = element.yPosition

                    binding.greetrThumbnailCardLayout.addView(imageView)
                }

                BLOCK_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson<BlockData>(hashMapTree, BlockData::class.java)

                    val imageView = ImageView(context)
                    imageView.id = element.id

                    val drawable =
                        ContextCompat.getDrawable(context, R.drawable.block_element_shape)!!
                            .mutate()
                    imageView.setImageDrawable(drawable)


                    //Block Color.
                    (DrawableCompat.wrap(drawable) as GradientDrawable).setColor(
                        Color.parseColor(
                            data.color
                        )
                    )

                    //Block width and height.
                    val params = ConstraintLayout.LayoutParams(
                        AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height)
                    )
                    params.topToTop = binding.greetrThumbnailCardLayout.id
                    params.bottomToBottom = binding.greetrThumbnailCardLayout.id
                    params.startToStart = binding.greetrThumbnailCardLayout.id
                    params.endToEnd = binding.greetrThumbnailCardLayout.id
                    imageView.layoutParams = params

                    //Block zoom.
                    imageView.scaleX = data.scaleX
                    imageView.scaleY = data.scaleY

                    //Block rotation,
                    imageView.rotation = data.rotation.toFloat()

                    //Block stroke.
                    (DrawableCompat.wrap(drawable) as GradientDrawable).setStroke(
                        AppUtils.pxToDp(
                            context, data.strokeWidth
                        ), Color.parseColor(data.strokeColor)
                    )

                    //Block corners.
                    (DrawableCompat.wrap(drawable) as GradientDrawable).cornerRadius =
                        AppUtils.pxToDp(context, data.corners).toFloat()

                    //Block shadow.
                    imageView.elevation = AppUtils.pxToDp(context, data.shadowRadius).toFloat()

                    //BLock background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))

                    imageView.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))

                    imageView.background = bgr


                    //Block position.
                    imageView.x = element.xPosition
                    imageView.y = element.yPosition

                    binding.greetrThumbnailCardLayout.addView(imageView)
                }

                GRADIENT_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson<GradientData>(hashMapTree, GradientData::class.java)

                    val imageView = ImageView(context)
                    imageView.id = element.id

                    val drawable =
                        ContextCompat.getDrawable(context, R.drawable.gradient_element_shape)!!
                            .mutate()
                    imageView.setImageDrawable(drawable)

                    //Gradient colors.
                    val intColorArray = intArrayOf(
                        Color.parseColor(data.colors!![0]), Color.parseColor(data.colors!![1])
                    )
                    (DrawableCompat.wrap(drawable) as GradientDrawable).colors = intColorArray

                    //Gradient angle.
                    when (data.gradientAngle) {
                        0 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.LEFT_RIGHT
                        }
                        45 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.TL_BR
                        }
                        90 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.TOP_BOTTOM
                        }
                        135 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.TR_BL
                        }
                        180 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.RIGHT_LEFT
                        }
                        225 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.BR_TL
                        }
                        270 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.BOTTOM_TOP
                        }
                        315 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.BL_TR
                        }
                    }

                    //Gradient width and height.
                    val params = ConstraintLayout.LayoutParams(
                        AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height)
                    )
                    params.topToTop = binding.greetrThumbnailCardLayout.id
                    params.bottomToBottom = binding.greetrThumbnailCardLayout.id
                    params.startToStart = binding.greetrThumbnailCardLayout.id
                    params.endToEnd = binding.greetrThumbnailCardLayout.id
                    imageView.layoutParams = params


                    //Gradient zoom.
                    imageView.scaleX = data.scaleX
                    imageView.scaleY = data.scaleY

                    //Gradient corners.
                    (DrawableCompat.wrap(drawable) as GradientDrawable).cornerRadius =
                        AppUtils.pxToDp(
                            context, data.corners
                        ).toFloat()


                    //Gradient shadow.
                    imageView.elevation = AppUtils.pxToDp(context, data.shadowRadius).toFloat()


                    //Gradient rotation.
                    imageView.rotation = data.rotation.toFloat()


                    //Gradient stroke.
                    (DrawableCompat.wrap(drawable) as GradientDrawable).setStroke(
                        AppUtils.pxToDp(
                            context, data.strokeWidth
                        ), Color.parseColor(data.strokeColor)
                    )

                    //Gradient background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))

                    imageView.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))

                    imageView.background = bgr



                    //Gradient position.
                    imageView.x = element.xPosition
                    imageView.y = element.yPosition

                    binding.greetrThumbnailCardLayout.addView(imageView)
                }

                PATTERN_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson(hashMapTree, PatternElementData::class.java)

                    val patternElement = FrameLayout(context)
                    patternElement.id = element.id

                    val patternCard = MaterialCardView(context)

                    patternElement.addView(patternCard)


                    //Pattern design.
                    withContext(Dispatchers.IO) {
                        val view = Patterns(context).loadPatternWithViewId(
                            data.pattern!!, data.width, data.height
                        )
                        withContext(Dispatchers.Main) {
                            patternCard.addView(view)
                        }
                    }

                    //Pattern width and height.
                    val params = ConstraintLayout.LayoutParams(
                        AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height)
                    )
                    params.topToTop = binding.greetrThumbnailCardLayout.id
                    params.bottomToBottom = binding.greetrThumbnailCardLayout.id
                    params.startToStart = binding.greetrThumbnailCardLayout.id
                    params.endToEnd = binding.greetrThumbnailCardLayout.id
                    patternElement.layoutParams = params

                    //Pattern zoom.
                    patternElement.scaleX = data.scaleX
                    patternElement.scaleY = data.scaleY

                    //Pattern rotation.
                    patternElement.rotation = data.rotation.toFloat()

                    //Pattern corners.
                    patternCard.radius = AppUtils.pxToDp(context, data.corners).toFloat()

                    //Pattern stroke.
                    patternCard.strokeWidth = AppUtils.pxToDp(context, data.strokeWidth)
                    patternCard.strokeColor = Color.parseColor(data.strokeColor)

                    //Pattern shadow.
                    patternElement.elevation = AppUtils.pxToDp(context, data.shadowRadius).toFloat()

                    //Pattern background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))

                    patternElement.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))

                    patternElement.background = bgr

                    //Pattern position.
                    patternElement.x = element.xPosition
                    patternElement.y = element.yPosition

                    binding.greetrThumbnailCardLayout.addView(patternElement)
                }

                AUDIO_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson<AudioData>(hashMapTree, AudioData ::class.java)

                    val audioElementBinding = CreatorAudioBinding.inflate(LayoutInflater.from(context))
                    val audioElement = audioElementBinding.creatorAudioElement
                    audioElement.id = element.id



                    //Audio input.
                    //Check if the file is available locally or needs to get from storage and then load it.
                    if (AppUtils.hasStoragePermission(context)) {
                        val file = File(data.localPath)
                        if (file.exists()) {
                            val player = ExoPlayer.Builder(context).build()


                            val item = MediaItem.Builder()
                            item.setUri(Uri.parse(data.localPath))
                            item.setMimeType(MimeTypes.AUDIO_MP4)
                            val menuItem = item.build()

                            val source =
                                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                                    .createMediaSource(menuItem)

                            player.addMediaItem(menuItem)
                            player.prepare()

                            audioElementBinding.creatorAudioElementPlayer.player = player
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
                                context.cacheDir.toString() + "/" + "Crafty/" + element.id.toString() + ".mp3"
                            val folder = File("$context.cacheDir/Crafty/")
                            if (!folder.exists()) {
                                folder.mkdir()
                            }

                            //Set the local path to the data.
                            data.localPath = cachePath

                            val cacheFile = File(cachePath)
                            ref.getFile(cacheFile).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val player = ExoPlayer.Builder(context).build()


                                    val item = MediaItem.Builder()
                                    item.setUri(Uri.parse(data.localPath))
                                    item.setMimeType(MimeTypes.AUDIO_MP4)
                                    val menuItem = item.build()

                                    val source =
                                        ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                                            .createMediaSource(menuItem)

                                    player.addMediaItem(menuItem)
                                    player.prepare()

                                    audioElementBinding.creatorAudioElementPlayer.player = player
                                }
                            }
                        }
                    }
                    else {
                        AppUtils.buildStoragePermission(context).show()
                    }


                    //Audio width and height.
                    val params = FrameLayout.LayoutParams(
                        AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height)
                    )

                    audioElement.layoutParams = params

                    //Audio zoom.
                    audioElement.scaleX = data.scaleX
                    audioElement.scaleY = data.scaleY

                    //Audio rotation,
                    audioElement.rotation = data.rotation.toFloat()

                    //Audio stroke.
                    audioElementBinding.creatorAudioElementCard.strokeWidth =
                        AppUtils.pxToDp(
                            context, data.strokeWidth
                        )

                    //Audio stroke color.
                    audioElementBinding.creatorAudioElementCard.strokeColor = Color.parseColor(data.strokeColor)

                    //Audio corners.
                    audioElementBinding.creatorAudioElementCard.radius =
                        AppUtils.pxToDp(context, data.corners).toFloat()

                    //Audio shadow.
                    audioElement.elevation = AppUtils.pxToDp(context, data.shadowRadius).toFloat()

                    //Audio background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))

                    audioElement.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))

                    audioElement.background = bgr


                    //Audio position.
                    audioElement.x = element.xPosition
                    audioElement.y = element.yPosition

                    binding.greetrThumbnailCardLayout.addView(audioElement)
                }
            }
        }
    }

    suspend @androidx.media3.common.util.UnstableApi fun buildElement(element: ElementData,  listener: ElementBuilderListener) {
        val gson = Gson()
        try {
            when (element.elementType) {
                TEXT_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson<TextData>(hashMapTree, TextData::class.java)
                    val textView = TextView(context)
                    textView.id = element.id


                    //Text input.
                    textView.text = data.text

                    //Text size.
                    textView.textSize = AppUtils.pxToSp(context, data.size).toFloat()

                    //Text zoom.
                    textView.scaleX = data.scaleX
                    textView.scaleY = data.scaleY

                    //Text font.
                    if (data.font != null) {
                        AppUtils.getTypeFace(
                            context,
                            data.font!!,
                            object : FontsContractCompat.FontRequestCallback() {
                                override fun onTypefaceRetrieved(typeface: Typeface?) {
                                    textView.typeface = typeface
                                    super.onTypefaceRetrieved(typeface)
                                }

                                override fun onTypefaceRequestFailed(reason: Int) {
                                    super.onTypefaceRequestFailed(reason)
                                }
                            })
                    }

                    //Text color,
                    textView.setTextColor(Color.parseColor(data.color))

                    //Text Rotation.
                    textView.rotation = data.rotation.toFloat()

                    //Text depth.
                    textView.setShadowLayer(
                        data.depthRadius.toFloat(),
                        data.horzDepth.toFloat(),
                        data.vertDepth.toFloat(),
                        Color.parseColor(data.depthColor)
                    )

                    //Text background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))
                    textView.background = bgr

                    textView.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))

                    withContext(Dispatchers.Main) {
                        listener.onElementReady(textView)
                    }
                }

                IMAGE_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson(hashMapTree, ImageData::class.java)
                    var bitmap: Bitmap? = null

                    val imageView = RoundedImageView(context)
                    imageView.id = element.id

                    withContext(Dispatchers.Main) {
                        val widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                        val heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

                        imageView.measure(widthSpec, heightSpec)
                        imageView.layout(0, 0, imageView.measuredWidth, imageView.measuredHeight)

                        val params = ViewGroup.LayoutParams(AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height))
                        imageView.layoutParams = params

                    }

                    //Image input and compression.
                    if (AppUtils.hasStoragePermission(context)) {
                        //Check if the cache file exists.
                        val file = File(data.localPath)
                        if (file.exists()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Glide.with(context).asBitmap().load(file).apply(RequestOptions().encodeFormat(Bitmap.CompressFormat.PNG).encodeQuality(data.quality)).into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        bitmap = resource
                                        imageView.setImageBitmap(bitmap)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }

                                })
                            }
                        } else {
                            if (user == null) {
                                return
                            }

                            val ref = storageReference.child(data.storagePath)

                            try {
                                ref.getStream { state, stream ->
                                    if (state.error != null) {
                                        return@getStream
                                    }
                                    Glide.with(context).asBitmap().load(stream).apply(RequestOptions().encodeFormat(Bitmap.CompressFormat.JPEG).encodeQuality(data.quality)).into(object : CustomTarget<Bitmap>() {
                                        override fun onResourceReady(
                                            resource: Bitmap,
                                            transition: Transition<in Bitmap>?
                                        ) {
                                            bitmap = resource
                                            imageView.setImageBitmap(bitmap)

                                            //Write the image to cache and update the local path.
                                            runBlocking {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val cachePath = writeImageElementToCache(element,
                                                        bitmap!!
                                                    )
                                                    data.localPath = cachePath
                                                }
                                            }
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) {
                                        }

                                    })

                                }
                            }
                            catch (e: IOException) {
                                e.printStackTrace()
                            }
                            catch (e: StorageException) {
                                e.printStackTrace()
                            }

                        }
                    } else {
                        AppUtils.buildStoragePermission(context).show()
                    }


                    //Image zoom.
                    imageView.scaleX = data.scaleX
                    imageView.scaleY = data.scaleY

                    //Image rotation.
                    imageView.rotation = data.rotation.toFloat()

                    //Image filter.
                    CoroutineScope(Dispatchers.IO).launch {
                        ImageFilters(context).getFilterWithName(data.filterName, object :
                            ImageFilters.ImageFiltersListener {
                            override fun onFiltersWithImageLoaded(loadedFilters: MutableList<Bitmap>) {
                            }

                            override fun onFilterReadyWithName(filter: ImageFilterData) {
                                if (filter.filter != null) {
                                    if (bitmap != null) {
                                        CoroutineScope(Dispatchers.Default).launch {
                                            val gpuImage = GPUImage(context)
                                            gpuImage.setImage(bitmap)
                                            gpuImage.setFilter(filter.filter)
                                            val filterBit = gpuImage.bitmapWithFilterApplied
                                            withContext(Dispatchers.Main) {
                                                imageView.setImageBitmap(filterBit)
                                            }
                                        }
                                    }
                                }
                            }

                        })
                    }

                    //Image corners.
                    imageView.cornerRadius = AppUtils.pxToDp(context, data.corners).toFloat()

                    //Image scale type.
                    imageView.scaleType = data.scaleType!!.scaleType


                    //Image background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))

                    imageView.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))
                    imageView.background = bgr

                    withContext(Dispatchers.Main) {
                        listener.onElementReady(imageView)
                    }
                }

                BLOCK_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson<BlockData>(hashMapTree, BlockData::class.java)

                    val imageView = ImageView(context)
                    imageView.id = element.id

                    withContext(Dispatchers.Main) {
                        val widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                        val heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

                        imageView.measure(widthSpec, heightSpec)
                        imageView.layout(0, 0, imageView.measuredWidth, imageView.measuredHeight)

                        val params = ViewGroup.LayoutParams(AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height))
                        imageView.layoutParams = params

                    }

                    val drawable =
                        ContextCompat.getDrawable(context, R.drawable.block_element_shape)!!
                            .mutate()
                    imageView.setImageDrawable(drawable)


                    //Block Color.
                    (DrawableCompat.wrap(drawable) as GradientDrawable).setColor(
                        Color.parseColor(
                            data.color
                        )
                    )

                    //Block zoom.
                    imageView.scaleX = data.scaleX
                    imageView.scaleY = data.scaleY

                    //Block rotation,
                    imageView.rotation = data.rotation.toFloat()

                    //Block stroke.
                    (DrawableCompat.wrap(drawable) as GradientDrawable).setStroke(
                        AppUtils.pxToDp(
                            context, data.strokeWidth
                        ), Color.parseColor(data.strokeColor)
                    )

                    //Block corners.
                    (DrawableCompat.wrap(drawable) as GradientDrawable).cornerRadius =
                        AppUtils.pxToDp(context, data.corners).toFloat()

                    //Block shadow.
                    imageView.elevation = AppUtils.pxToDp(context, data.shadowRadius).toFloat()

                    //BLock background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))

                    imageView.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))

                    imageView.background = bgr

                    withContext(Dispatchers.Main) {
                        listener.onElementReady(imageView)
                    }
                }

                GRADIENT_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson<GradientData>(hashMapTree, GradientData::class.java)



                    val binding = CreatorGradientBinding.inflate(LayoutInflater.from(context))
                    val imageView = binding.creatorGradientElement
                    imageView.id = element.id

                    withContext(Dispatchers.Main) {
                        val widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                        val heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

                        imageView.measure(widthSpec, heightSpec)
                        imageView.layout(0, 0, imageView.measuredWidth, imageView.measuredHeight)

                        val params = ViewGroup.LayoutParams(AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height))
                        imageView.layoutParams = params

                    }


                    val drawable =
                        ContextCompat.getDrawable(context, R.drawable.gradient_element_shape)!!
                            .mutate()
                    imageView.setImageDrawable(drawable)

                    //Gradient colors.
                    val intColorArray = intArrayOf(
                        Color.parseColor(data.colors!![0]), Color.parseColor(data.colors!![1])
                    )
                    (DrawableCompat.wrap(drawable) as GradientDrawable).colors = intColorArray

                    //Gradient angle.
                    when (data.gradientAngle) {
                        0 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.LEFT_RIGHT
                        }
                        45 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.TL_BR
                        }
                        90 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.TOP_BOTTOM
                        }
                        135 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.TR_BL
                        }
                        180 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.RIGHT_LEFT
                        }
                        225 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.BR_TL
                        }
                        270 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.BOTTOM_TOP
                        }
                        315 -> {
                            (DrawableCompat.wrap(drawable) as GradientDrawable).orientation =
                                GradientDrawable.Orientation.BL_TR
                        }
                    }

                    //Gradient zoom.
                    imageView.scaleX = data.scaleX
                    imageView.scaleY = data.scaleY

                    //Gradient corners.
                    (DrawableCompat.wrap(drawable) as GradientDrawable).cornerRadius =
                        AppUtils.pxToDp(
                            context, data.corners
                        ).toFloat()


                    //Gradient shadow.
                    imageView.elevation = AppUtils.pxToDp(context, data.shadowRadius).toFloat()


                    //Gradient rotation.
                    imageView.rotation = data.rotation.toFloat()


                    //Gradient stroke.
                    (DrawableCompat.wrap(drawable) as GradientDrawable).setStroke(
                        AppUtils.pxToDp(
                            context, data.strokeWidth
                        ), Color.parseColor(data.strokeColor)
                    )

                    //Gradient background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))

                    imageView.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))

                    imageView.background = bgr


                    withContext(Dispatchers.Main) {
                        val widthSpec = MeasureSpec.makeMeasureSpec(AppUtils.pxToDp(context, data.width), MeasureSpec.EXACTLY)
                        val heightSpec = MeasureSpec.makeMeasureSpec(AppUtils.pxToDp(context, data.height), MeasureSpec.EXACTLY)

                        imageView.measure(widthSpec, heightSpec)
                        imageView.layout(0, 0, imageView.measuredWidth, imageView.measuredHeight)

                        val params = ViewGroup.LayoutParams(AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height))
                        imageView.layoutParams = params

                        listener.onElementReady(imageView)
                    }
                }

                PATTERN_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson(hashMapTree, PatternElementData::class.java)

                    val patternElement = FrameLayout(context)
                    patternElement.id = element.id

                    val patternCard = MaterialCardView(context)

                    patternElement.addView(patternCard)

                    withContext(Dispatchers.Main) {
                        val widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                        val heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

                        patternElement.measure(widthSpec, heightSpec)
                        patternElement.layout(0, 0, patternElement.measuredWidth, patternElement.measuredHeight)

                        val params = ViewGroup.LayoutParams(AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height))
                        patternElement.layoutParams = params

                    }

                    //Pattern design.
                    withContext(Dispatchers.IO) {
                        val view = Patterns(context).loadPatternWithViewId(
                            data.pattern!!, data.width, data.height
                        )
                        withContext(Dispatchers.Main) {
                            patternCard.addView(view)
                        }
                    }

                    //Pattern zoom.
                    patternElement.scaleX = data.scaleX
                    patternElement.scaleY = data.scaleY

                    //Patter rotation.
                    patternElement.rotation = data.rotation.toFloat()

                    //Pattern corners.
                    patternCard.radius = AppUtils.pxToDp(context, data.corners).toFloat()

                    //Pattern stroke.
                    patternCard.strokeWidth = AppUtils.pxToDp(context, data.strokeWidth)
                    patternCard.strokeColor = Color.parseColor(data.strokeColor)

                    //Pattern shadow.
                    patternElement.elevation = AppUtils.pxToDp(context, data.shadowRadius).toFloat()

                    //Pattern background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))

                    patternElement.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))

                    patternElement.background = bgr

                    withContext(Dispatchers.Main) {
                        listener.onElementReady(patternElement)
                    }

                }

                AUDIO_TYPE -> {
                    val hashMapTree = gson.toJsonTree(element.data)
                    val data = gson.fromJson<AudioData>(hashMapTree, AudioData ::class.java)

                    val audioElementBinding = CreatorAudioBinding.inflate(LayoutInflater.from(context))
                    val audioElement = audioElementBinding.creatorAudioElement
                    audioElement.id = element.id



                    //Audio input.
                    //Check if the file is available locally or needs to get from storage and then load it.
                    if (AppUtils.hasStoragePermission(context)) {
                        val file = File(data.localPath)
                        if (file.exists()) {
                            val player = ExoPlayer.Builder(context).build()


                            val item = MediaItem.Builder()
                            item.setUri(Uri.parse(data.localPath))
                            item.setMimeType(MimeTypes.AUDIO_MP4)
                            val menuItem = item.build()

                            val source =
                                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                                    .createMediaSource(menuItem)

                            player.addMediaItem(menuItem)
                            player.prepare()

                            audioElementBinding.creatorAudioElementPlayer.player = player
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
                                context.cacheDir.toString() + "/" + "Crafty/" + element.id.toString() + ".mp3"
                            val folder = File("$context.cacheDir/Crafty/")
                            if (!folder.exists()) {
                                folder.mkdir()
                            }

                            //Set the local path to the data.
                            data.localPath = cachePath

                            val cacheFile = File(cachePath)
                            ref.getFile(cacheFile).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val player = ExoPlayer.Builder(context).build()


                                    val item = MediaItem.Builder()
                                    item.setUri(Uri.parse(data.localPath))
                                    item.setMimeType(MimeTypes.AUDIO_MP4)
                                    val menuItem = item.build()

                                    val source =
                                        ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                                            .createMediaSource(menuItem)

                                    player.addMediaItem(menuItem)
                                    player.prepare()

                                    audioElementBinding.creatorAudioElementPlayer.player = player
                                }
                            }
                        }
                    }
                    else {
                        AppUtils.buildStoragePermission(context).show()
                    }


                    //Audio width and height.
                    val params = FrameLayout.LayoutParams(
                        AppUtils.pxToDp(context, data.width), AppUtils.pxToDp(context, data.height)
                    )

                    audioElement.layoutParams = params

                    //Audio zoom.
                    audioElement.scaleX = data.scaleX
                    audioElement.scaleY = data.scaleY

                    //Audio rotation,
                    audioElement.rotation = data.rotation.toFloat()

                    //Audio stroke.
                    audioElementBinding.creatorAudioElementCard.strokeWidth =
                        AppUtils.pxToDp(
                            context, data.strokeWidth
                        )

                    //Audio stroke color.
                    audioElementBinding.creatorAudioElementCard.strokeColor = Color.parseColor(data.strokeColor)

                    //Audio corners.
                    audioElementBinding.creatorAudioElementCard.radius =
                        AppUtils.pxToDp(context, data.corners).toFloat()

                    //Audio shadow.
                    audioElement.elevation = AppUtils.pxToDp(context, data.shadowRadius).toFloat()

                    //Audio background.
                    val bgr = ContextCompat.getDrawable(context, R.drawable.element_bgr)!!
                        .mutate() as GradientDrawable
                    bgr.cornerRadius = AppUtils.pxToDp(context, data.backgroundCorners).toFloat()
                    bgr.setColor(Color.parseColor(data.backgroundColor))

                    audioElement.setPadding(AppUtils.pxToDp(context, data.backgroundPadding))

                    audioElement.background = bgr


                    withContext(Dispatchers.Main) {
                        listener.onElementReady(audioElement)
                    }
                }

                else -> {
                    withContext(Dispatchers.Main) {
                        listener.onElementFailed()
                    }                }
            }
        }
        catch (e: Exception) {
            withContext(Dispatchers.Main) {
                listener.onElementFailed()
            }
        }
    }

    fun recalculateElementPositions(
        originalX: Int,
        originalY: Int,
        originalWidth: Int,
        originalHeight: Int,
        currentWidth: Int,
        currentHeight: Int
    ): Array<Int> {
        val dw = originalWidth - currentWidth
        val dh = originalHeight - currentHeight

        var posArray = arrayOf(originalX, originalY)
        if (currentWidth < originalWidth) {
            posArray[0] = originalX + dw
        } else {
            posArray[0] = originalX - dw
        }

        if (currentHeight < originalHeight) {
            posArray[1] = originalY + dh
        } else {
            posArray[1] = originalY - dh
        }

        return posArray
    }


   suspend fun saveElementToDevice(elementData: ElementData, mediaListener: AppUtils.MediaListener) {
        //Create a unique name.
        val fileName = if (TextUtils.isEmpty(elementData.elementName.trim())) {
            AppUtils.uniqueContentNameGenerator(elementData.elementType)

        }
        else {
            AppUtils.uniqueContentNameGenerator(elementData.elementName)
        }
        //Create the content values for saving the card.
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.ced")
        contentValues.put(MediaStore.Images.Media.TITLE, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "application/ced")
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS.toString() + "/Crafty/Elements"
            )
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)
        } else {
            val file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/" + Environment.DIRECTORY_DOWNLOADS + "/Crafty/Elements"
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            contentValues.put(
                MediaStore.Images.Media.DATA,
                Environment.getExternalStorageDirectory()
                    .toString() + "/" + Environment.DIRECTORY_DOWNLOADS + "/Crafty/Elements/" + fileName + ".ced"
            )
        }
        val externalUri: Uri = MediaStore.Files.getContentUri("external")

        //Insert the content values using the content resolver and get the saved uri;
        val contentResolver = context.contentResolver
        var savedUri: Uri? = contentResolver.insert(externalUri, contentValues)

        //Save the card data to the saved uri;
        var outputStream: ObjectOutputStream? = null

        //Check if saved uri is valid and then proceed to save the data;
        try {
            if (savedUri == null) {
                throw IOException("Can't save the element.")
            } else {
                withContext(Dispatchers.IO) {
                    outputStream =
                        ObjectOutputStream(contentResolver.openOutputStream(savedUri!!))
                    outputStream!!.writeObject(elementData)
                }
            }
        } catch (e: IOException) {
            //Remove the saved uri in case if saving data to it has failed;
            if (savedUri != null) {
                contentResolver.delete(savedUri, null, null)
                savedUri = null
            }
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                mediaListener.onMediaSaveFailed("Can't save the element, try again later.")
            }
        } finally {
            if (outputStream != null) {
                try {
                    withContext(Dispatchers.IO) {
                        outputStream!!.flush()
                        outputStream!!.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            //After saving data change IS_PENDING to 0 so it is available to read for other mediums;
            if (savedUri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(savedUri, contentValues, null, null)
                }
            }

            //Notify the listener.
            withContext(Dispatchers.Main) {
                mediaListener.onMediaSaved(savedUri.toString())
            }
        }
    }

    fun buildColorPickerDialog(listener: ColorPickerDialogListener): ColorPickerDialog {
        val builder = ColorPickerDialog.newBuilder()
        builder.setAllowPresets(false)
        builder.setAllowCustom(true)
        builder.setShowAlphaSlider(true)

        val dialog = builder.create()

        dialog.setColorPickerDialogListener(listener)
        return dialog
    }

   suspend fun writeImageElementToCache(elementData: ElementData, bitmap: Bitmap): String {
        //Write the bitmap to cache.
        val cachePath =
            context.cacheDir.toString() + "/" + "Crafty/" + elementData.id.toString() + ".png"
        val folder = File("${context.cacheDir}/Crafty/")
        if (!folder.exists()) {
            folder.mkdir()
        }

        val file = File(cachePath)
        var fos: FileOutputStream? = null
        withContext(Dispatchers.IO) {
            try {
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos!!)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fos!!.flush()
                fos!!.close()
            }
        }

       return cachePath
    }
}