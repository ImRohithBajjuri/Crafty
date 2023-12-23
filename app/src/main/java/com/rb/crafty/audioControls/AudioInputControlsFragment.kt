package com.rb.crafty.audioControls

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioFormat
import android.media.MediaController2
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaMuxer.OutputFormat
import android.media.MediaPlayer
import android.media.MediaSession2
import android.media.session.MediaSession
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.MediaController
import android.widget.MediaController.MediaPlayerControl
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.leanback.widget.SeekBar
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerControlView.ProgressUpdateListener
import com.rb.crafty.R
import com.rb.crafty.dataObjects.AudioData
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.dataObjects.TempUiImageData
import com.rb.crafty.databinding.FragmentAudioInputControlsBinding
import com.rb.crafty.sheets.AudioCreatorSheet
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timerTask


class AudioInputControlsFragment() : Fragment() {


    var binding: FragmentAudioInputControlsBinding? = null

    lateinit var listener: AppUtils.ElementOptionsControlsListener

    lateinit var audioLauncher: ActivityResultLauncher<Intent>

    lateinit var audioFlowerJob: Job

    var controller: PlayerControlView? = null

    constructor(
        listener: AppUtils.ElementOptionsControlsListener,
        audioLauncher: ActivityResultLauncher<Intent>
    ) : this() {
        this.listener = listener
        this.audioLauncher = audioLauncher
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAudioInputControlsBinding.inflate(inflater, container, false)


        binding!!.audioInputControlsAdd.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (AppUtils.hasStoragePermission(requireActivity())) {
                        val intent2 = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                        intent2.addCategory(Intent.CATEGORY_TYPED_OPENABLE)
                        intent2.type = "audio/*"
//                        audioLauncher.launch(intent2)


                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.data = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        audioLauncher.launch(intent)
                    } else {
                        AppUtils.buildStoragePermission(requireActivity()).show()
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding!!.audioInputControlsPlayPause.setOnClickListener {
            val anim = AnimUtils.pressAnim(@UnstableApi object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (controller != null) {
                        if (controller!!.player != null) {
                            if (controller!!.player!!.isPlaying) {
                                controller!!.player!!.pause()
                                controller!!.show()

                                binding!!.audioInputControlsPlayPause.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.play_35dp))
                            }
                            else {
                                controller!!.player!!.play()
                                controller!!.show()

                                binding!!.audioInputControlsPlayPause.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.pause_35dp))

                            }
                        }
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            it.startAnimation(anim)
        }

        binding!!.audioInputControlsSeek.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onProgressChanged(
                seekBar: android.widget.SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (binding != null && controller != null && isAdded && fromUser) {
                    //Update the UI.
                    binding!!.audioInputControlsCurrentSecs.text = setProperSecs(progress.toLong())

                    if (controller!!.player != null) {
                        controller!!.player!!.seekTo(progress.toLong())
                        controller!!.show()
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
            }

        })


        binding!!.audioInputControlsGenerate.setOnClickListener {
            it.startAnimation(AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val audioCreator = AudioCreatorSheet()
                    audioCreator.show(childFragmentManager, "AudioInput")
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            }))
        }


        return binding!!.root
    }

    override fun onDetach() {

        super.onDetach()
    }

    @SuppressLint("UnsafeOptInUsageError")
   private fun adjustAddAudioButton() {
        if (binding != null && isAdded) {
            if (controller!!.player != null) {
                binding!!.audioInputControlsAdd.setCardBackgroundColor(
                    ColorStateList.valueOf(Color.TRANSPARENT)
                )
                binding!!.audioInputControlsAdd.strokeWidth =
                    AppUtils.pxToDp(requireActivity(), 2)
                binding!!.audioInputControlsAddTxt.text = "Change Audio"
                binding!!.audioInputControlsAddTxt.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.deepPurple
                    )
                )
                binding!!.audioInputControlsAddTxt.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_round_change_circle_24,
                    0,
                    0,
                    0
                )
            } else {
                binding!!.audioInputControlsAdd.setCardBackgroundColor(
                    ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.deepPurple))
                )
                binding!!.audioInputControlsAdd.strokeWidth =
                    AppUtils.pxToDp(requireActivity(), 0)
                binding!!.audioInputControlsAddTxt.text = "Add Audio"
                binding!!.audioInputControlsAddTxt.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.white
                    )
                )
                binding!!.audioInputControlsAddTxt.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_round_add_circle_white_24,
                    0,
                    0,
                    0
                )
            }

        }
    }


    @SuppressLint("UnsafeOptInUsageError")
    fun setCurrentAudioInput(elementData: ElementData, controller: PlayerControlView) {
        if (binding != null && isAdded && activity != null && controller.player != null) {
            this.controller = controller

            binding!!.audioInputControlsName.text = elementData.elementName
            binding!!.audioInputControlsSeek.max = controller.player!!.duration.toInt()
            binding!!.audioInputControlsSeek.progress = controller.player!!.currentPosition.toInt()
            binding!!.audioInputControlsCurrentSecs.text = "0:00"
            binding!!.audioInputControlsCard.visibility = View.VISIBLE

            if (controller.player!!.isPlaying) {
                binding!!.audioInputControlsPlayPause.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.pause_35dp))
            }
            else {
                binding!!.audioInputControlsPlayPause.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.play_35dp))
            }


            binding!!.audioInputControlsTotalSecs.text = setProperSecs(controller.player!!.duration)

            binding!!.audioInputControlsCurrentSecs.text = setProperSecs(controller.player!!.currentPosition)

            //Adjust the 'Add card'.
            adjustAddAudioButton()

            controller.setProgressUpdateListener { position, bufferedPosition ->
                if (controller.player != null) {
                    if (controller.player!!.isPlaying) {
                        binding!!.audioInputControlsSeek.progress = position.toInt()

                        binding!!.audioInputControlsCurrentSecs.text = setProperSecs(position)
                        controller.show()
                    }

                }
            }
        }
        else if (controller.player == null) {
            binding!!.audioInputControlsCard.visibility = View.GONE
        }
    }
    @androidx.media3.common.util.UnstableApi
    private fun setProperSecs(currentSecs: Long): String {
        val inSecs = (currentSecs.toInt() / 1000)
        val inMins = (inSecs.toFloat() / 60).toInt()
        val boundSecs = (inSecs % 60)
        var displaySecs = boundSecs.toString()
        if (displaySecs.length == 1) {
            displaySecs = "0${displaySecs}"
        }
        return "${inMins}:${displaySecs}"
    }


    private fun audioTimeFlower(totalMillis: Int): Job {
        val interval = 1000
        val toRepeat = totalMillis / interval
        val job = CoroutineScope(Dispatchers.Main).launch {
            repeat(toRepeat) {
                delay(interval.toLong())
                val currentProgress = binding!!.audioInputControlsSeek.progress
                val nextProgress = currentProgress + 1000
                binding!!.audioInputControlsSeek.progress = nextProgress

                val inSecs = (nextProgress / 1000)
                val inMins = (inSecs.toFloat() / 60).toInt()
                val boundSecs = (inSecs % 60)
                var displaySecs = boundSecs.toString()
                if (displaySecs.length == 1) {
                    displaySecs = "0${displaySecs}"
                }
                binding!!.audioInputControlsCurrentSecs.text = "${inMins}:${displaySecs}"
            }
        }

        val timer = Timer()
        timer.schedule(timerTask {

        },interval.toLong(), totalMillis.toLong())


        return job
    }

}