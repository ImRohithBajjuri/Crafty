package com.rb.crafty.imageControls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.dataObjects.ImageData
import com.rb.crafty.adapters.ImageScaleItemAdapter
import com.rb.crafty.dataObjects.ImageScaleTypeData
import com.rb.crafty.databinding.FragmentImageScaleTypeControlsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ImageScaleTypeControlsFragment() : Fragment(),
    ImageScaleItemAdapter.ImageScaleAdapterListener {
    var binding: FragmentImageScaleTypeControlsBinding? = null

    lateinit var listener: AppUtils.ElementOptionsControlsListener

    lateinit var scaleTypeList: MutableList<ImageScaleTypeData>

    lateinit var scaleTypeAdapter: ImageScaleItemAdapter

    lateinit var currentScaleType: ImageScaleTypeData


    constructor(listener: AppUtils.ElementOptionsControlsListener, scaleTypeList: MutableList<ImageScaleTypeData>) : this() {
        this.listener = listener
        this.scaleTypeList = scaleTypeList
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImageScaleTypeControlsBinding.inflate(inflater, container, false)

        //Load and set the image scale types.
        CoroutineScope(Dispatchers.IO).launch {

            scaleTypeAdapter = ImageScaleItemAdapter(
                requireActivity(),
                scaleTypeList,
                this@ImageScaleTypeControlsFragment
            )
            val layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

            withContext(Dispatchers.Main) {
                binding!!.imageScaleTypeRecy.layoutManager = layoutManager

                binding!!.imageScaleTypeRecy.adapter = scaleTypeAdapter
            }
        }


        return binding!!.root
    }

    override fun onScaleSelected(scaleTypeData: ImageScaleTypeData) {
        listener.onImageScaleTypeUpdate(scaleTypeData)
    }

    fun setCurrentScaleType(imageData: ImageData) {
        if (binding != null) {
            val prevPosition = scaleTypeList.indexOf(scaleTypeAdapter.currentScaleType)
            scaleTypeAdapter.currentScaleType = imageData.scaleType
            val currentPosition = scaleTypeList.indexOf(scaleTypeAdapter.currentScaleType)

            scaleTypeAdapter.notifyItemChanged(prevPosition)
            scaleTypeAdapter.notifyItemChanged(currentPosition)
        }
    }
}