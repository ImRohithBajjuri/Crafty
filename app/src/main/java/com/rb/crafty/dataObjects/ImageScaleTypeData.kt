package com.rb.crafty.dataObjects

import android.widget.ImageView.ScaleType

class ImageScaleTypeData: java.io.Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694978376793974L
    }

    var typeName: String = "Type name"
    var scaleType: ScaleType? = null

    constructor(typeName: String, scaleType: ScaleType?) {
        this.typeName = typeName
        this.scaleType = scaleType
    }
}