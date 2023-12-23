package com.rb.crafty.imageControls

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.rb.crafty.adapters.ImageFiltersAdapter
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.dataObjects.ImageData
import com.rb.crafty.dataObjects.ImageFilterData
import com.rb.crafty.databinding.FragmentImageFiltersControlBinding
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ImageFilters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class ImageFiltersControlFragment() : Fragment() {

    var binding: FragmentImageFiltersControlBinding? = null

    lateinit var adapter: ImageFiltersAdapter
    lateinit var filtersList: MutableList<ImageFilterData>

    lateinit var listener: AppUtils.ElementOptionsControlsListener
    lateinit var imageFilters: ImageFilters


    companion object {
        var loadedBitmapFilters: MutableList<Bitmap>? = null
    }


    constructor(listener: AppUtils.ElementOptionsControlsListener, imageFilters: ImageFilters) : this() {
        this.listener = listener
        this.imageFilters = imageFilters
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImageFiltersControlBinding.inflate(inflater, container, false)

        filtersList = ArrayList()
        filtersList.addAll(imageFilters.filtersList)

        binding!!.imageFiltersControlsLoadingBar.visibility = View.VISIBLE
        adapter = ImageFiltersAdapter(requireActivity(), filtersList, imageFilters.loadedFilterBitmaps, listener)

        //Get the loaded filters with image placeholder and set it to the adapter.
        if (loadedBitmapFilters == null) {
            runBlocking {
                CoroutineScope(Dispatchers.Default).launch {
                    imageFilters.loadFiltersWithImage(object : ImageFilters.ImageFiltersListener {
                        override fun onFiltersWithImageLoaded(loadedFilters: MutableList<Bitmap>) {
                            if (activity != null && isAdded) {
                                loadedBitmapFilters = loadedFilters
                                adapter = ImageFiltersAdapter(requireActivity(), filtersList, loadedFilters, listener)
                                val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
                                binding!!.imageFiltersControlRecy.layoutManager = layoutManager
                                binding!!.imageFiltersControlRecy.adapter = adapter
                                binding!!.imageFiltersControlsLoadingBar.visibility = View.GONE
                            }
                        }

                        override fun onFilterReadyWithName(filter: ImageFilterData) {

                        }

                    })
                }
            }
        }
        else {
            adapter = ImageFiltersAdapter(requireActivity(), filtersList,
                loadedBitmapFilters!!, listener)
            val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding!!.imageFiltersControlRecy.layoutManager = layoutManager
            binding!!.imageFiltersControlRecy.adapter = adapter
            binding!!.imageFiltersControlsLoadingBar.visibility = View.GONE
        }



        return binding!!.root
    }

    fun setCurrentFilter(elementData: ElementData) {
        val filterData = elementData.data as ImageData

        if (binding != null) {
            if (adapter.currentSelectedFilter != null) {
                for (filter in filtersList) {
                    if (filter.filterName == filterData.filterName) {
                        //Get the previous selected item.
                        val prevItem = adapter.currentSelectedFilter

                        //Set the current selected item.
                        adapter.currentSelectedFilter = filter

                        //Update the adapter to notify changes at both positions.
                        adapter.notifyItemChanged(filtersList.indexOf(prevItem))
                        adapter.notifyItemChanged(filtersList.indexOf(adapter.currentSelectedFilter))

                        //End the loop.
                        break
                    }
                }
            }
        }
    }

}