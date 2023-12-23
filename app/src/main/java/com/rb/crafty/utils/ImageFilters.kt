package com.rb.crafty.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.rb.crafty.R
import com.rb.crafty.dataObjects.ImageFilterData
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBulgeDistortionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGammaFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHazeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageKuwaharaFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLightenBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLuminanceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageMonochromeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePixelationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSmoothToonFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSphereRefractionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSwirlFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVibranceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter
import kotlinx.coroutines.*

class ImageFilters(var context: Context) {

    //All the current filters.
    val ORIGINAL = "Original"
    val MONOCHROME = "Monochrome"
    val SEPIA_TONE = "Sepia Tone"
    val SKETCH = "Sketch"
    val CONTRAST = "Contrast"
    val BRIGHTNESS = "Brightness"
    val EXPOSURE = "Exposure"
    val GAMMA = "Gamma"
    val GUASSIAN_BLUR = "Guassian Blur"
    val GRAYSCALE = "Grayscale"
    val HAZE = "Haze"
    val Hue = "Hue"
    val KUWAHARA = "Kuwahara"
    val PIXELATION = "Pixelation"
    val LUMINANCE = "Luminance"
    val SATURATION = "Saturation"
    val SHARPEN = "Sharpen"
    val TOON = "Toon"
    val SMOOTH_TOON = "Smooth Toon"
    val VIBRANCE = "Vibrance"
    val WHITE_BALANCE = "White Balance"
    val VIGNETTE = "Vignette"
    val POSTERIZE = "Posterize"
    val SWIRL_DISTORTION = "Swirl Distortion"
    val BULGE_DISTORTION = "Bulge Distortion"
    val LIGHTEN_BLEND = "Lighten Blend"
    val GLASS_SPHERE_REFRACTION = "Glass Sphere Refraction"

    lateinit var filtersList: MutableList<ImageFilterData>

    lateinit var loadedFilterBitmaps: MutableList<Bitmap>

    interface ImageFiltersListener {
        fun onFiltersWithImageLoaded(loadedFilters: MutableList<Bitmap>)

        fun onFilterReadyWithName(filter: ImageFilterData)
    };

    init {
        filtersList = createFiltersList()

        loadedFilterBitmaps = ArrayList()


    }

    fun createFiltersList(): MutableList<ImageFilterData> {
        val filtersList = ArrayList<ImageFilterData>()

        filtersList.add(ImageFilterData(ORIGINAL, null))
        filtersList.add(ImageFilterData(MONOCHROME, GPUImageMonochromeFilter()))
        filtersList.add(ImageFilterData(SEPIA_TONE, GPUImageSepiaToneFilter()))
        filtersList.add(ImageFilterData(SKETCH, GPUImageSketchFilter()))
        filtersList.add(ImageFilterData(CONTRAST, GPUImageContrastFilter()))
        filtersList.add(ImageFilterData(BRIGHTNESS, GPUImageBrightnessFilter()))
        filtersList.add(ImageFilterData(EXPOSURE, GPUImageExposureFilter()))
        filtersList.add(ImageFilterData(GAMMA, GPUImageGammaFilter()))
        filtersList.add(ImageFilterData(GUASSIAN_BLUR, GPUImageGaussianBlurFilter()))
        filtersList.add(ImageFilterData(GRAYSCALE, GPUImageGrayscaleFilter()))
        filtersList.add(ImageFilterData(HAZE, GPUImageHazeFilter()))
        filtersList.add(ImageFilterData(Hue, GPUImageHueFilter()))
        filtersList.add(ImageFilterData(KUWAHARA, GPUImageKuwaharaFilter()))
        filtersList.add(ImageFilterData(PIXELATION, GPUImagePixelationFilter()))
        filtersList.add(ImageFilterData(LUMINANCE, GPUImageLuminanceFilter()))
        filtersList.add(ImageFilterData(SATURATION, GPUImageSaturationFilter()))
        filtersList.add(ImageFilterData(SHARPEN, GPUImageSharpenFilter()))
        filtersList.add(ImageFilterData(SMOOTH_TOON, GPUImageSmoothToonFilter()))
        filtersList.add(ImageFilterData(TOON, GPUImageToonFilter()))
        filtersList.add(ImageFilterData(VIBRANCE, GPUImageVibranceFilter()))
        filtersList.add(ImageFilterData(WHITE_BALANCE, GPUImageWhiteBalanceFilter()))
        filtersList.add(ImageFilterData(VIGNETTE, GPUImageVignetteFilter()))
        filtersList.add(ImageFilterData(POSTERIZE, GPUImagePosterizeFilter()))
        filtersList.add(ImageFilterData(SWIRL_DISTORTION, GPUImageSwirlFilter()))
        filtersList.add(ImageFilterData(BULGE_DISTORTION, GPUImageBulgeDistortionFilter()))
        filtersList.add(ImageFilterData(LIGHTEN_BLEND, GPUImageLightenBlendFilter()))
        filtersList.add(ImageFilterData(GLASS_SPHERE_REFRACTION, GPUImageSphereRefractionFilter()))
        return filtersList
    }

    suspend fun loadFiltersWithImage(listener: ImageFiltersListener) {
        val loadedFilters = ArrayList<Bitmap>()

        val normalBitmap = BitmapFactory.decodeResource(context.resources,
            R.drawable.filter_placeholder
        )

        val gpuImage = GPUImage(context)
        gpuImage.setImage(normalBitmap)

        for (filter in filtersList) {
            //If the filter is null then it's original.
            if (filter.filter != null) {
                gpuImage.setFilter(filter.filter)
                loadedFilters.add(gpuImage.bitmapWithFilterApplied)
            }
            else {
                loadedFilters.add(normalBitmap)
            }


            if (filtersList.indexOf(filter) == filtersList.size -1) {
                withContext(Dispatchers.Main) {
                    listener.onFiltersWithImageLoaded(loadedFilters)
                }
            }
        }
    }

    suspend fun getFilterWithName(filterName: String, listener: ImageFiltersListener) {
        //Index through the filters list and find the filter that matches the name.
        for (filter in filtersList) {
            if (filter.filterName == filterName) {
                withContext(Dispatchers.Main) {
                    listener.onFilterReadyWithName(filter)
                }
                break
            }
        }
    }

}