package com.rb.crafty.imageControls

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.view.ViewCompat
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.dataObjects.TempUiImageData
import com.rb.crafty.databinding.FragmentImageCropControlsBinding


class ImageCropControlsFragment() : Fragment() {
    var binding: FragmentImageCropControlsBinding? = null

    lateinit var listener: AppUtils.ElementOptionsControlsListener

    var currentImage: Bitmap? = null

    constructor(listener: AppUtils.ElementOptionsControlsListener) : this() {
        this.listener = listener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImageCropControlsBinding.inflate(inflater, container, false)

        ViewCompat.setNestedScrollingEnabled(binding!!.imageCropOptionLayout, true)

        binding!!.imageCropOptionComplete.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    if (currentImage != null) {
                        //Update the UI and notify the listener.
                        val croppedImage = binding!!.imageCropOptionView.croppedImage

                        binding!!.imageCropOptionView.setImageBitmap(croppedImage)
                        listener.onImageCropUpdate(croppedImage!!)
                    } else {
                        AppUtils.buildSnackbar(
                            requireContext(),
                            "No image to crop",
                            binding!!.root
                        ).show()
                    }

                }

                override fun onAnimationRepeat(p0: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding!!.imageCropOptionZoomSwitch.setOnCheckedChangeListener { compoundButton, b ->
            binding!!.imageCropOptionView.isAutoZoomEnabled = b
        }

        return binding!!.root
    }

    fun setCurrentImageCrop(tempUiImageData: TempUiImageData) {
        if (binding != null) {
            val isChecked = binding!!.imageCropOptionView.isAutoZoomEnabled
            binding!!.imageCropOptionZoomSwitch.isChecked = isChecked

            currentImage = tempUiImageData.bitmap

            binding!!.imageCropOptionView.setImageBitmap(tempUiImageData.bitmap)
        }
    }

}