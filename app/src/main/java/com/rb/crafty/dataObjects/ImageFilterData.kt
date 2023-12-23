package com.rb.crafty.dataObjects

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

class ImageFilterData() {
    var filterName: String ="name"
    var filter: GPUImageFilter? = null

    constructor(filterName: String, filter: GPUImageFilter?): this() {
        this.filterName = filterName
        this.filter = filter
    }
}