package com.rb.crafty.utils


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.database.CursorIndexOutOfBoundsException
import android.graphics.*
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaMuxer.OutputFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import androidx.media3.common.util.MediaFormatUtil
import com.google.android.material.snackbar.Snackbar
import com.rb.crafty.R
import com.rb.crafty.dataObjects.*
import kotlinx.coroutines.*
import org.jcodec.api.SequenceEncoder
import org.jcodec.scale.BitmapUtil
import java.io.File
import java.io.IOException
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*


class AppUtils {
    companion object {
        //Cloud store labels.
        val USER_ASSETS_COLLECTION = "User Assets"
        val CRAFTY_CARDS_COLLECTION = "Crafty Cards"
        val DEFAULT_FONT_ASSETS_COLLECTION = "Default Fonts"
        val ALL_FONT_ASSETS_COLLECTION = "All Fonts"
        val USER_ADDED_FONTS_COLLECTION = "Added Fonts"
        val SOME_IDEAS_COLLECTION = "Some Ideas"
        val HIDDEN_CARDS_COLLECTION = "Hidden Cards"
        val PREMIUM_USERS_COLLECTION = "Premium Users"
        val CRAFTY_ELEMENTS_COLLECTION = "Elements"
        val CANCELLED_PREMIUM_USERS_COLLECTION = "Cancelled Premium Users"

        //Storage labels.
        val USER_STORAGE_ASSETS_REFERENCE = "User Storage Assets"

        //        val CARD_IMAGE_ELEMENTS_REFERENCE = "Crafty Card Image Elements"
        val IMAGE_ELEMENTS_REFERENCE = "Image Element Assets"
        val AUDIO_ELEMENTS_REFERENCE = "Audio Element Assets"


        //File types.
        val JPG = "jpg"
        val PNG = "png"
        val SVG = "svg"

        //Scale types.
        val CENTER_CROP = "Center crop"
        val FIT_CENTER = "Fit center"
        val FIT_XY = "Fit xy"
        val CENTER = "Center"
        val CENTER_INSIDE = "Center Inside"
        val FIT_END = "Fit end"
        val FIT_START = "Fit start"
        val MATRIX = "Matrix"

        //Main foreground types.
        val MAIN_FOREGROUND_COLOR = "color"
        val MAIN_FOREGROUND_GRADIENT = "gradient"
        val MAIN_FOREGROUND_PATTERN = "pattern"


        //Main options.
        val MAIN_SIZE = "mainSize"
        val MAIN_ZOOM = "mainZoom"
        val MAIN_STROKE = "mainStroke"
        val MAIN_CORNERS = "mainCorners"
        val MAIN_ROTATION = "mainRotation"
        val MAIN_FOREGROUND = "mainForeground"

        //App themes.
        val APP_THEME_LIGHT = "light"
        val APP_THEME_DARK = "dark"
        val APP_THEME_SYSTEM = "system"


        //App pref keys.
        val APP_PREFS = "appPrefs"
        val APP_THEME_KEY = "appTheme"
        val ELEMENT_HIGHLIGHT_KEY = "elementHighlight"
        val ELEMENT_HIGHLIGHT_COLOUR_KEY = "elementHighlightColour"
        val ELEMENT_HIGHLIGHT_CORNERS_KEY = "elementHighlightCorners"
        val ELEMENT_MENU_KEY = "elementMenu"


        //Purchase states.
        val PURCHASE_STATE_PURCHASED = 0
        val PURCHASE_STATE_CANCELLED = 1
        val PURCHAE_STATE_PENDING = 2

        //Acknowledgement states.
        val ACKNOWLEDGEMENT_STATE_COMPLETED = 1
        val ACKNOWLEDGEMENT_STATE_NOT_YET = 0


        fun pxToDp(context: Context, toConvertValue: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                toConvertValue.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        fun dpToPx(context: Context, toConvertValue: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                toConvertValue.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        fun pxToSp(context: Context, toConvertValue: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                toConvertValue.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        fun intToHex(color: Int): String {
            return String.format("#%08X", color).uppercase(Locale.getDefault())
        }

        fun scaleTypeItems(): MutableList<ImageScaleTypeData> {
            val dataList = kotlin.collections.ArrayList<ImageScaleTypeData>()
            dataList.add(ImageScaleTypeData(CENTER, ImageView.ScaleType.CENTER))
            dataList.add(ImageScaleTypeData(CENTER_CROP, ImageView.ScaleType.CENTER_CROP))
            dataList.add(ImageScaleTypeData(CENTER_INSIDE, ImageView.ScaleType.CENTER_INSIDE))
            dataList.add(ImageScaleTypeData(FIT_CENTER, ImageView.ScaleType.FIT_CENTER))
            dataList.add(ImageScaleTypeData(FIT_START, ImageView.ScaleType.FIT_START))
            dataList.add(ImageScaleTypeData(FIT_END, ImageView.ScaleType.FIT_END))
            dataList.add(ImageScaleTypeData(FIT_XY, ImageView.ScaleType.FIT_XY))
            dataList.add(ImageScaleTypeData(MATRIX, ImageView.ScaleType.MATRIX))
            return dataList
        }

        fun mainOptions(): MutableList<ElementOptionData> {
            val mainOptionsList = kotlin.collections.ArrayList<ElementOptionData>()
            mainOptionsList.add(
                ElementOptionData(
                    "Size",
                    MAIN_SIZE,
                    Elements.MAIN
                )
            )
            mainOptionsList.add(
                ElementOptionData(
                    "Zoom",
                    MAIN_ZOOM,
                    Elements.MAIN
                )
            )

            mainOptionsList.add(
                ElementOptionData(
                    "Corners",
                    MAIN_CORNERS,
                    Elements.MAIN
                )
            )
            mainOptionsList.add(
                ElementOptionData(
                    "Rotation",
                    MAIN_ROTATION,
                    Elements.MAIN
                )
            )
            mainOptionsList.add(
                ElementOptionData(
                    "Stroke",
                    MAIN_STROKE,
                    Elements.MAIN
                )
            )
            mainOptionsList.add(
                ElementOptionData(
                    "Foreground",
                    MAIN_FOREGROUND,
                    Elements.MAIN
                )
            )

            return mainOptionsList
        }

        fun mainOptionDrawables(): MutableList<Int> {
            val drawableList = ArrayList<Int>()

            drawableList.add(R.drawable.ic_round_block_size_24)
            drawableList.add(R.drawable.zoom_in_24dp)
            drawableList.add(R.drawable.ic_round_rounded_corner_24)
            drawableList.add(R.drawable.ic_round_crop_rotate_24)
            drawableList.add(R.drawable.ic_round_block_outline_24)
            drawableList.add(R.drawable.ic_round_rectangle_24)

            return drawableList
        }


        suspend fun saveImageBitmap(
            context: Context,
            imageName: String,
            fileType: String,
            bitmap: Bitmap,
            mediaListener: MediaListener
        ) {
            //Create the content values for saving image;
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.$fileType")
            contentValues.put(MediaStore.Images.Media.TITLE, imageName)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/$fileType")
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES.toString() + "/Crafty"
                )
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)
            } else {
                val file = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_PICTURES + "/Crafty"
                )
                if (!file.exists()) {
                    file.mkdirs()
                }
                contentValues.put(
                    MediaStore.Images.Media.DATA,
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_PICTURES + "/Crafty/" + imageName + ".$fileType"
                )
            }
            val externalUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            //Insert the content values using the content resolver and get the saved uri;
            val contentResolver = context.contentResolver
            var savedUri: Uri? = contentResolver.insert(externalUri, contentValues)

            //Save the image data to the saved uri;
            var outputStream: OutputStream? = null

            //Check if saved uri is valid and then proceed to save the data;
            try {
                if (savedUri == null) {
                    throw IOException("no image")
                } else {
                    outputStream = contentResolver.openOutputStream(savedUri)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
                }
            } catch (e: IOException) {
                //Remove the saved uri in case if saving data to it has failed;
                if (savedUri != null) {
                    contentResolver.delete(savedUri, null, null)
                    savedUri = null
                }
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    mediaListener.onMediaSaveFailed("Can't save the image, try again later.")
                }
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush()
                        outputStream.close()
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

        suspend fun saveGreetrCardToDevice(
            context: Context,
            cardName: String,
            cardData: CardData,
            mediaListener: MediaListener
        ) {
            //Create the content values for saving the card.
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "$cardName.cty")
            contentValues.put(MediaStore.Images.Media.TITLE, cardName)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "application/cty")
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS.toString() + "/Crafty/Cards"
                )
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)
            } else {
                val file = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_DOWNLOADS + "/Crafty/Cards"
                )
                if (!file.exists()) {
                    file.mkdirs()
                }
                contentValues.put(
                    MediaStore.Images.Media.DATA,
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_DOWNLOADS + "/Crafty/Cards/" + cardName + ".cty"
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
                    throw IOException("Can't save the card.")
                } else {
                    withContext(Dispatchers.IO) {
                        outputStream =
                            ObjectOutputStream(contentResolver.openOutputStream(savedUri!!))
                        outputStream!!.writeObject(cardData)
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
                    mediaListener.onMediaSaveFailed("Can't save the card, try again later.")
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

        fun getMIMEType(url: String?): String? {
            var mType: String? = null
            val mExtension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (mExtension != null) {
                mType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExtension)
            }
            return mType
        }

        suspend fun saveCardAsMp4(
            context: Context,
            fileName: String,
            bitmap: Bitmap,
            audioPath: String,
            mediaListener: MediaListener
        ) {
            var saveProgress = 0
            //Save video directly to user's folder if there's no audio or mux if there's any.
            if (File(audioPath).exists()) {
                val tempVideoCachePath =
                    context.cacheDir.toString() + "/" + "Crafty/" + fileName + ".mp4"
                val cacheFolder = File("${context.cacheDir}/Crafty/")
                if (!cacheFolder.exists()) {
                    cacheFolder.mkdir()

                    saveProgress += 10
                    mediaListener.onMediaSaveProgress(saveProgress)
                }

                val tempVideoCache = File(tempVideoCachePath)
                if (!tempVideoCache.exists()) {
                    withContext(Dispatchers.IO) {
                        try {
                            tempVideoCache.createNewFile()
                            saveProgress += 10
                            mediaListener.onMediaSaveProgress(saveProgress)
                        } catch (e: IOException) {
                            mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                            Log.e("mp4Error", "error", e)
                            e.printStackTrace()
                            return@withContext
                        }
                    }
                } else {
                    saveProgress += 10
                    mediaListener.onMediaSaveProgress(saveProgress)
                }

                val sequenceEncoder = SequenceEncoder.createSequenceEncoder(tempVideoCache, 1)
                try {
                    sequenceEncoder.encodeNativeFrame(BitmapUtil.fromBitmap(bitmap))
                } catch (e: Exception) {
                    mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                    Log.e("mp4Error", "error", e)
                    e.printStackTrace()
                    return
                }
                sequenceEncoder.finish()
                saveProgress += 30
                mediaListener.onMediaSaveProgress(saveProgress)

                //Start muxing.
                muxAudioAndVideo(
                    tempVideoCachePath,
                    audioPath,
                    fileName,
                    saveProgress,
                    mediaListener
                )
            } else {
                val videoPath = Environment.getExternalStorageDirectory()
                    .toString() + "/" + Environment.DIRECTORY_MOVIES + "/Crafty/" + fileName + ".mp4"

                val videoFolder =
                    File("${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_MOVIES}/Crafty/")
                if (!videoFolder.exists()) {
                    videoFolder.mkdir()
                }

                val videoFile = File(videoPath)
                if (!videoFile.exists()) {
                    withContext(Dispatchers.IO) {
                        try {
                            videoFile.createNewFile()
                        } catch (e: IOException) {
                            mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                            e.printStackTrace()
                            return@withContext
                        }
                    }
                }

                saveProgress += 50
                mediaListener.onMediaSaveProgress(saveProgress)
                val sequenceEncoder = SequenceEncoder.createSequenceEncoder(videoFile, 1)

                for (secs in 0..5) {
                    try {
                        sequenceEncoder.encodeNativeFrame(BitmapUtil.fromBitmap(bitmap))


                        if (secs == 5) {
                            sequenceEncoder.finish()
                            mediaListener.onMediaSaved(videoPath)
                        } else {
                            saveProgress += 10
                            mediaListener.onMediaSaveProgress(saveProgress)
                        }
                    } catch (e: Exception) {
                        mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                        e.printStackTrace()
                        break
                    }
                }
            }
        }

        @SuppressLint("WrongConstant")
        private suspend fun muxAudioAndVideo(
            videoFile: String,
            audioFile: String,
            fileName: String,
            saveProgress: Int,
            mediaListener: MediaListener
        ) {
            var progress = saveProgress
            val videoExtractor = MediaExtractor()
            try {
                videoExtractor.setDataSource(videoFile)
            } catch (e: IOException) {
                mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                e.printStackTrace()
                return
            }
            videoExtractor.selectTrack(0)
            val videoFormat = videoExtractor.getTrackFormat(0)
            progress += 5
            mediaListener.onMediaSaveProgress(progress)

            val audioExtractor = MediaExtractor()
            try {
                audioExtractor.setDataSource(audioFile)
            } catch (e: IOException) {
                mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                e.printStackTrace()
                return
            }
            audioExtractor.selectTrack(0)
            val audioFormat = audioExtractor.getTrackFormat(0)
            progress += 5
            mediaListener.onMediaSaveProgress(progress)

            //Set the output file.
            val outputPath = Environment.getExternalStorageDirectory()
                .toString() + "/" + Environment.DIRECTORY_MOVIES + "/Crafty/" + fileName + ".mp4"

            //Setup the muxer.
            val muxer = MediaMuxer(outputPath, OutputFormat.MUXER_OUTPUT_MPEG_4)
            var videoIndex = 0
            var audioIndex = 0
            try {
                videoIndex = muxer.addTrack(videoFormat)
                audioIndex = muxer.addTrack(audioFormat)
            } catch (e: Exception) {
                mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                e.printStackTrace()


                return
            }

            muxer.start()
            progress += 10
            mediaListener.onMediaSaveProgress(progress)

            //Create byte size and buffer info.
            val byteSize = 1024 * 1024
            val buffer = ByteBuffer.allocate(byteSize)
            val bufferInfo = MediaCodec.BufferInfo()

            //Mux Video.
            while (true) {
                val vidByte = videoExtractor.readSampleData(buffer, 0)

                if (vidByte > 0) {
                    bufferInfo.presentationTimeUs = videoExtractor.sampleTime
                    bufferInfo.flags = videoExtractor.sampleFlags
                    bufferInfo.size = vidByte

                    try {
                        muxer.writeSampleData(videoIndex, buffer, bufferInfo)
                    } catch (e: IllegalArgumentException) {
                        mediaListener.onMediaSaveFailed("Unable to export the Video, try again later...")

                        muxer.stop()
                        muxer.release()

                        videoExtractor.release()
                        audioExtractor.release()

                        e.printStackTrace()
                        break
                    } catch (e: IllegalStateException) {
                        mediaListener.onMediaSaveFailed("Unable to export the Video, try again later...")

                        muxer.stop()
                        muxer.release()

                        videoExtractor.release()
                        audioExtractor.release()

                        e.printStackTrace()
                        break
                    }

                    videoExtractor.advance()
                } else {
                    progress += 15
                    mediaListener.onMediaSaveProgress(progress)
                    break
                }
            }

            //Mux audio.
            while (true) {
                val audioByte = audioExtractor.readSampleData(buffer, 0)

                if (audioByte > 0) {
                    bufferInfo.presentationTimeUs = audioExtractor.sampleTime
                    bufferInfo.flags = audioExtractor.sampleFlags
                    bufferInfo.size = audioByte

                    try {
                        muxer.writeSampleData(audioIndex, buffer, bufferInfo)
                    } catch (e: IllegalArgumentException) {
                        mediaListener.onMediaSaveFailed("Unable to export the Video, try again later...")

                        muxer.stop()
                        muxer.release()

                        videoExtractor.release()
                        audioExtractor.release()


                        e.printStackTrace()
                        break
                    } catch (e: IllegalStateException) {
                        mediaListener.onMediaSaveFailed("Unable to export the Video, try again later...")

                        muxer.stop()
                        muxer.release()

                        videoExtractor.release()
                        audioExtractor.release()

                        e.printStackTrace()

                        break
                    }
                    audioExtractor.advance()

                } else {
                    progress += 15
                    mediaListener.onMediaSaveProgress(progress)
                    break
                }
            }

            try {
                muxer.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            muxer.release()

            videoExtractor.release()
            audioExtractor.release()

            mediaListener.onMediaSaved(outputPath)
        }

       suspend fun convertAudioToM4A(inFile: String, outFileM4a: String) {

            val extractor = MediaExtractor()
            extractor.setDataSource(inFile)

            // Find Audio Track
            for (i in 0 until extractor.trackCount) {
                val inputFormat = extractor.getTrackFormat(i)
                val mime = inputFormat.getString(MediaFormat.KEY_MIME);

                if (mime!!.startsWith("audio/")) {

                    extractor.selectTrack(i)

                    val decoder =
                        MediaCodec.createDecoderByType(inputFormat.getString(MediaFormat.KEY_MIME)!!)
                    decoder.configure(inputFormat, null, null, 0)
                    decoder.start()

                    // Prepare output format for aac/m4a
                    val outputFormat = MediaFormat()
                    outputFormat.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm")
                    outputFormat.setInteger(
                        MediaFormat.KEY_AAC_PROFILE,
                        MediaCodecInfo.CodecProfileLevel.AACObjectLC
                    )
                    outputFormat.setInteger(
                        MediaFormat.KEY_SAMPLE_RATE,
                        inputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                    )
                    outputFormat.setInteger(
                        MediaFormat.KEY_BIT_RATE,
                        inputFormat.getInteger(MediaFormat.KEY_BIT_RATE)
                    )
                    outputFormat.setInteger(
                        MediaFormat.KEY_CHANNEL_COUNT,
                        inputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                    )
                    outputFormat.setInteger(
                        MediaFormat.KEY_MAX_INPUT_SIZE,
                        1048576
                    ) // Needs to be large enough to avoid BufferOverflowException

                    // Init encoder
                    val encoder =
                        MediaCodec.createEncoderByType(outputFormat.getString(MediaFormat.KEY_MIME)!!)
                    encoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                    encoder.start()

                    // Init muxer
                    val muxer = MediaMuxer(outFileM4a, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

                    var allInputExtracted = false
                    var allInputDecoded = false
                    var allOutputEncoded = false

                    val timeoutUs = 10000L
                    val bufferInfo = MediaCodec.BufferInfo()
                    var trackIndex = -1

                    while (!allOutputEncoded) {

                        // Feed input to decoder
                        if (!allInputExtracted) {
                            val inBufferId = decoder.dequeueInputBuffer(timeoutUs)
                            if (inBufferId >= 0) {
                                val buffer = decoder.getInputBuffer(inBufferId)
                                val sampleSize = extractor.readSampleData(buffer!!, 0)

                                if (sampleSize >= 0) {
                                    decoder.queueInputBuffer(
                                        inBufferId, 0, sampleSize,
                                        extractor.sampleTime, extractor.sampleFlags
                                    )

                                    extractor.advance()
                                } else {
                                    decoder.queueInputBuffer(
                                        inBufferId, 0, 0,
                                        0, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                    )
                                    allInputExtracted = true
                                }
                            }
                        }

                        var encoderOutputAvailable = true
                        var decoderOutputAvailable = !allInputDecoded

                        while (encoderOutputAvailable || decoderOutputAvailable) {

                            // Drain Encoder & mux first
                            val outBufferId = encoder!!.dequeueOutputBuffer(bufferInfo, timeoutUs)
                            if (outBufferId >= 0) {

                                val encodedBuffer = encoder!!.getOutputBuffer(outBufferId)

                                muxer!!.writeSampleData(trackIndex, encodedBuffer!!, bufferInfo)

                                encoder!!.releaseOutputBuffer(outBufferId, false)

                                // Are we finished here?
                                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                    allOutputEncoded = true
                                    break
                                }
                            } else if (outBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                                encoderOutputAvailable = false
                            } else if (outBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                                trackIndex = muxer!!.addTrack(encoder!!.outputFormat)
                                muxer!!.start()
                            }

                            if (outBufferId != MediaCodec.INFO_TRY_AGAIN_LATER)
                                continue

                            // Get output from decoder and feed it to encoder
                            if (!allInputDecoded) {
                                val outBufferId = decoder.dequeueOutputBuffer(bufferInfo, timeoutUs)
                                if (outBufferId >= 0) {
                                    val outBuffer = decoder.getOutputBuffer(outBufferId)

                                    // If needed, process decoded data here
                                    // ...

                                    // We drained the encoder, so there should be input buffer
                                    // available. If this is not the case, we get a NullPointerException
                                    // when touching inBuffer
                                    val inBufferId = encoder.dequeueInputBuffer(timeoutUs)
                                    val inBuffer = encoder.getInputBuffer(inBufferId)

                                    // Copy buffers - decoder output goes to encoder input
                                    inBuffer!!.put(outBuffer)

                                    // Feed encoder
                                    encoder.queueInputBuffer(
                                        inBufferId,
                                        bufferInfo.offset,
                                        bufferInfo.size,
                                        bufferInfo.presentationTimeUs,
                                        bufferInfo.flags
                                    )

                                    decoder.releaseOutputBuffer(outBufferId, false)

                                    // Did we get all output from decoder?
                                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                                        allInputDecoded = true

                                } else if (outBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                                    decoderOutputAvailable = false
                                }
                            }
                        }
                    }

                    // Cleanup
                    extractor.release()

                    decoder.stop()
                    decoder.release()

                    encoder.stop()
                    encoder.release()

                    muxer.stop()
                    muxer.release()

                    return
                }
            }
        }

        fun uniqueContentNameGenerator(name: String): String {
            val timeStamp: String =
                SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.getDefault()).format(
                    Date()
                )
            return name + "_" + timeStamp
        }

        fun getContentFileName(context: Context, uri: Uri): String {
            var name = "content file name"
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                if (cursor != null) {
                    val index = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    name = cursor.getString(index)
                    cursor.close()
                }
            } catch (e: CursorIndexOutOfBoundsException) {
                e.printStackTrace()
            }
            return name
        }

        fun buildSnackbar(context: Context, text: String, view: View): Snackbar {
            val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
            snackbar.view.background =
                ContextCompat.getDrawable(context, R.drawable.snackbar_background)
            snackbar.setTextColor(ContextCompat.getColor(context, R.color.white))
            snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.white))
            snackbar.view.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.deepPurple))
            return snackbar
        }

        fun getNormalTime(millis: Long): String {
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = millis

            val currentTime = java.util.Calendar.getInstance()

            val amPm = getAmPm(calendar)

            return if (calendar.get(java.util.Calendar.DAY_OF_MONTH) == currentTime.get(java.util.Calendar.DAY_OF_MONTH) &&
                calendar.get(java.util.Calendar.MONTH) == currentTime.get(java.util.Calendar.MONTH) &&
                calendar.get(java.util.Calendar.YEAR) == currentTime.get(java.util.Calendar.YEAR)
            ) {
                ("${calendar.get(java.util.Calendar.HOUR)}:${calendar.get(java.util.Calendar.MINUTE)} $amPm")
            } else {
                "${calendar.get(java.util.Calendar.DAY_OF_MONTH)}/${calendar.get(java.util.Calendar.MONTH)}/${
                    calendar.get(
                        java.util.Calendar.YEAR
                    )
                }"
            }
        }

        fun getAmPm(calendar: java.util.Calendar): String {
            return if (calendar.get(java.util.Calendar.AM_PM) == Calendar.AM) {
                "am"
            } else {
                "pm"
            }
        }

        fun contentExists(context: Context, uri: Uri?): Boolean {
            return context.contentResolver.getType(uri!!) != null
        }

        fun buildStoragePermission(context: Context): AlertDialog.Builder {
            val theme = if (isDarkMode(context)) {
                R.style.DarkCustomDialogTheme
            } else {
                R.style.CustomDialogTheme
            }
            val builder = AlertDialog.Builder(context, theme)
            builder.setTitle("Permission required!")
            builder.setMessage("Storage permission is required for loading image elements")
            builder.setPositiveButton(
                "GIVE",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(
                            context as AppCompatActivity, arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES
                            ), 67
                        )
                    } else {
                        ActivityCompat.requestPermissions(
                            context as AppCompatActivity, arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ), 67
                        )
                    }


                })
            return builder
        }

        fun hasStoragePermission(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED)
            } else {
                (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED)
            }
        }

        fun booleanToInt(value: Boolean): Int {
            return if (value) {
                1
            } else {
                0
            }
        }

        fun getTypeFace(
            context: Context,
            fontData: FontData,
            callBack: FontsContractCompat.FontRequestCallback
        ) {
            val queryFontName = "name=${fontData.fontFamily}&weight=${fontData.weight}&italic=${
                booleanToInt(fontData.italic)
            }"
            CoroutineScope(Dispatchers.Main).launch {
                val request = FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    queryFontName,
                    R.array.com_google_android_gms_fonts_certs
                )
                FontsContractCompat.requestFont(context, request, callBack, Handler())
            }

        }

        fun isDarkMode(context: Context): Boolean {
            val prefs = context.getSharedPreferences("appPrefs", MODE_PRIVATE)
            val appTheme = prefs.getString("appTheme", "dark")
            var isDark = false
            when (appTheme) {
                APP_THEME_LIGHT -> isDark = false

                APP_THEME_DARK -> isDark = true

                APP_THEME_SYSTEM -> {
                    when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES -> isDark = true

                        Configuration.UI_MODE_NIGHT_NO -> isDark = false
                    }
                }

                else -> isDark = false
            }

            return isDark
        }

        fun createCardHelpList(): MutableList<HelpData> {
            val helpList = ArrayList<HelpData>()
            helpList.add(
                HelpData(
                    "In the main page, click on the 'Create a new card' to open the creator page.",
                    R.drawable.help_list_create_step_one
                )
            )
            helpList.add(
                HelpData(
                    "You can create various type of cards with multiple elements in the creator page.",
                    R.drawable.help_list_create_step_two
                )
            )
            helpList.add(
                HelpData(
                    "Here you can see a beautiful card with gradient background hosting a text element with a slick font. Customize like this with even more elements in your way!",
                    R.drawable.help_list_create_step_three
                )
            )
            helpList.add(
                HelpData(
                    "After you customize your card with elements, you can save the card by opening the options menu in the top right corner and clicking the save option..",
                    R.drawable.help_list_create_step_four
                )
            )
            helpList.add(
                HelpData(
                    "The saved card will be automatically added to your list. You can close the creator page and your newly created awesome card will be visible in the main page.",
                    R.drawable.help_list_create_step_five
                )
            )
            return helpList
        }

        fun createCardExportAsImageHelpList(): MutableList<HelpData> {
            val helpList = ArrayList<HelpData>()
            helpList.add(
                HelpData(
                    "In the main page, click on the card you want to export as image. This will open the creator page.",
                    R.drawable.help_list_create_step_five
                )
            )
            helpList.add(
                HelpData(
                    "Now, once you have your card available, it is ready to get exported.",
                    R.drawable.help_list_create_step_three
                )
            )
            helpList.add(
                HelpData(
                    "At the top right corner, click on the options menu and then click on the 'export as ' option",
                    R.drawable.help_list_create_step_four
                )
            )
            helpList.add(
                HelpData(
                    "Once you click on the option, a sub menu will appear hosting options to export in different ways.",
                    R.drawable.help_list_export_as_image_step_four
                )
            )
            helpList.add(
                HelpData(
                    "Click on any option to export the card in the given format. This will export the card to 'Pictures/Crafty/' folder in your device.",
                    R.drawable.help_list_export_as_image_step_five
                )
            )
            helpList.add(
                HelpData(
                    "You can access this exported card using a file manager or any other file viewing service.",
                    R.drawable.help_list_export_as_image_step_six
                )
            )
            return helpList
        }

        fun createCardExportHelpList(): MutableList<HelpData> {
            val helpList = ArrayList<HelpData>()
            helpList.add(
                HelpData(
                    "In the main page, long press on the card you want to export. This will open the options sheet",
                    R.drawable.help_list_create_step_five
                )
            )
            helpList.add(
                HelpData(
                    "In the options sheet, you will find various options related to the card. Click on the 'Download this card'.",
                    R.drawable.help_list_export_as_file_step_two
                )
            )
            helpList.add(
                HelpData(
                    "The card will be exported to Download/Crafty/Cards.",
                    R.drawable.help_list_export_as_file_step_three
                )
            )
            helpList.add(
                HelpData(
                    "You can access the exported card using a file manager or any other file viewing service.",
                    R.drawable.help_list_export_as_file_step_four
                )
            )
            return helpList
        }

        fun createCardFavingHelpList(context: Context): MutableList<HelpData> {
            val helpList = ArrayList<HelpData>()
            helpList.add(
                HelpData(
                    "In the main page, long press on the card you want to favourite. This will open the options sheet",
                    R.drawable.help_list_create_step_five
                )
            )
            helpList.add(
                HelpData(
                    "In the options sheet, you will find various options related to the card. Click on the 'Add to '${
                        context.getString(
                            R.string.favourites
                        )
                    }'.",
                    R.drawable.help_list_export_as_file_step_two
                )
            )
            helpList.add(
                HelpData(
                    "The card will be added to your ${context.getString(R.string.favourites)}.",
                    R.drawable.help_list_fav_card_step_three
                )
            )
            helpList.add(
                HelpData(
                    "Now click on the profile icon to open your profile page.",
                    R.drawable.help_list_fav_card_step_four
                )
            )
            helpList.add(
                HelpData(
                    "Click on the '${context.getString(R.string.favourites)} cards' button to open the ${
                        context.getString(
                            R.string.favourites
                        )
                    } page.",
                    R.drawable.help_list_fav_card_step_five
                )
            )

            helpList.add(
                HelpData(
                    "Here you can access your ${context.getString(R.string.favourites)} cards.",
                    R.drawable.help_list_fav_card_step_six
                )
            )
            return helpList
        }

        fun createCardHidingHelpList(): MutableList<HelpData> {
            val helpList = ArrayList<HelpData>()
            helpList.add(
                HelpData(
                    "In the main page, long press on the card you want to hide. This will open the options sheet.",
                    R.drawable.help_list_create_step_five
                )
            )
            helpList.add(
                HelpData(
                    "In the options sheet, you will find various options related to the card. Click on the 'Hide'.",
                    R.drawable.help_list_export_as_file_step_two
                )
            )
            helpList.add(
                HelpData(
                    "The card will get hidden and will not be visible in the main page.",
                    0
                )
            )
            helpList.add(
                HelpData(
                    "Now click on the profile icon to open your profile page.",
                    R.drawable.help_list_fav_card_step_four
                )
            )
            helpList.add(
                HelpData(
                    "Click on the 'Hidden cards' button to open the hidden cards page. You will need to authenticate with your device credentials to proceed.",
                    R.drawable.help_list_fav_card_step_five
                )
            )

            helpList.add(
                HelpData(
                    "Once you authenticate successfully you will be able to view your hidden cards.",
                    R.drawable.help_list_hide_card_step_six
                )
            )
            return helpList
        }
    }

    interface ElementOptionsControlsListener {
        //Focus.
        fun controlsInFocus()
        fun controlsNotInFocus()

        //Main Updates.
        fun onMainWidthUpdate(newWidth: Int)
        fun onMainHeightUpdate(newHeight: Int)
        fun onMainZoomXUpdate(zoomX: Float)
        fun onMainZoomYUpdate(zoomY: Float)
        fun onMainStrokeWidthUpdate(newWidth: Int)
        fun onMainStrokeColorUpdate(newColor: Int)
        fun onMainCornersUpdate(newCorners: Int)
        fun onMainRotateUpdate(newValue: Int)
        fun onMainColorUpdate(newColor: Int)
        fun onMainGradientColorsUpdate(newColors: MutableList<String>)
        fun onMainGradientAngleUpdate(newAngle: Int)
        fun onMainForegroundTypeUpdate(newType: String)


        //Text updates.
        fun onTextUpdate(newText: String)
        fun onTextColorUpdate(newColor: Int)
        fun onTextSizeUpdate(newSize: Int)
        fun onTextFontUpdate(newFont: FontData)
        fun onTextDepthRadiusUpdate(newRadius: Int)
        fun onTextDepthVertUpdate(newValue: Int)
        fun onTextDepthHorzUpdate(newValue: Int)
        fun onTextDepthColorUpdate(newColor: Int)


        //Image updates.
        fun onImageUpdate(newUri: String)
        fun onImageCropUpdate(croppedImage: Bitmap)
        fun onImageCompressUpdate(newValue: Int)
        fun onImageFilterUpdate(newFilter: ImageFilterData)
        fun onImageScaleTypeUpdate(newScaleType: ImageScaleTypeData)


        //Block updates.
        fun onBlockColorUpdate(newColor: Int)


        //Gradient updates.
        fun onGradientColorsUpdate(newColors: MutableList<String>)
        fun onGradientAngleUpdate(newAngle: Int)
        fun onGradientShapeUpdate(newShape: String)


        //Pattern Updates.
        fun onPatternDesignUpdate(newPattern: PatternData, patternsFor: String)
        fun onPatternShapeUpdate(newShape: String)


        //Audio updates.
        fun onAudioInputUpdate(newUri: Uri)


        //Elements common updates.
        fun onElementRotationUpdate(newRotation: Int)
        fun onElementWidthUpdate(newWidth: Int)
        fun onElementHeightUpdate(newHeight: Int)
        fun onElementZoomXUpdate(zoomX: Float)
        fun onElementZoomYUpdate(zoomY: Float)
        fun onElementCornerUpdate(newCorners: Int)
        fun onElementShadowRadiusUpdate(newRadius: Int)
        fun onElementStrokeWidthUpdate(newWidth: Int)
        fun onElementStrokeColorUpdate(newColor: Int)
        fun onElementBackgroundPaddingUpdate(newValue: Int)
        fun onElementBackgroundCornersUpdate(newCorners: Int)
        fun onElementBackgroundColorUpdate(newColor: Int)
    }

    interface MediaListener {
        //Image saving callbacks.
        fun onMediaSaved(savedPath: String)
        fun onMediaSaveProgress(progress: Int)
        fun onMediaSaveFailed(reason: String)
    }

}