package com.rb.crafty.imageControls

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.R
import com.rb.crafty.dataObjects.TempUiImageData
import com.rb.crafty.databinding.FragmentImageInputControlsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImageInputControlsFragment() : Fragment() {
    var binding: FragmentImageInputControlsBinding? = null

    lateinit var listener: AppUtils.ElementOptionsControlsListener

    lateinit var imageLauncher: ActivityResultLauncher<Intent>

    constructor(listener: AppUtils.ElementOptionsControlsListener, imageLauncher: ActivityResultLauncher<Intent>) : this() {
        this.listener = listener
        this.imageLauncher = imageLauncher
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImageInputControlsBinding.inflate(inflater, container, false)

        binding!!.imageInputOptionAdd.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (AppUtils.hasStoragePermission(requireActivity())) {
                        val intent2 = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                        intent2.addCategory(Intent.CATEGORY_TYPED_OPENABLE)
                        intent2.type = "image/*"
//                        imageLauncher.launch(intent2)


                        val intent = Intent(Intent.ACTION_PICK)
                        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        imageLauncher.launch(intent)
                    } else {
                        AppUtils.buildStoragePermission(requireActivity()).show()
                    }

                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }


        return binding!!.root
    }

    fun adjustAddImageButton(tempUiImageData: TempUiImageData, context: Context) {
        if (binding != null) {
            if (tempUiImageData.bitmap != null) {
                binding!!.imageInputOptionAdd.setCardBackgroundColor(
                    ColorStateList.valueOf(Color.TRANSPARENT)
                )
                binding!!.imageInputOptionAdd.strokeWidth =
                    AppUtils.pxToDp(context, 2)
                binding!!.imageInputOptionAddTxt.text = "Change image"
                binding!!.imageInputOptionAddTxt.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.deepPurple
                    )
                )
                binding!!.imageInputOptionAddTxt.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_round_change_circle_24,
                    0,
                    0,
                    0
                )
            } else {
                binding!!.imageInputOptionAdd.setCardBackgroundColor(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.deepPurple))
                )
                binding!!.imageInputOptionAdd.strokeWidth =
                    AppUtils.pxToDp(context, 0)
                binding!!.imageInputOptionAddTxt.text = "Add Image"
                binding!!.imageInputOptionAddTxt.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
                binding!!.imageInputOptionAddTxt.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_round_add_circle_white_24,
                    0,
                    0,
                    0
                )
            }

        }
    }


    fun setCurrentImageInput(tempUiImageData: TempUiImageData, context: Context) {
        if (binding != null) {
            //Set the image if there is any.
            if (tempUiImageData.bitmap != null) {
                binding!!.imageInputOptionImage.setImageBitmap(tempUiImageData.bitmap)
            }

            //Adjust the image add button.
            adjustAddImageButton(tempUiImageData, context)
        }
    }
}