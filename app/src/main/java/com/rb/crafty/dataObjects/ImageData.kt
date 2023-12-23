package com.rb.crafty.dataObjects

class ImageData: java.io.Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694978376793973L
    }
    var localPath = ""
    var storagePath = ""
    var width: Int = 200
    var height: Int = 200
    var quality: Int = 100
    var filterName: String = "Original"
    var scaleType: ImageScaleTypeData? = null
    var rotation: Int = 0
    var corners: Int = 20
    var backgroundPadding: Int = 20
    var backgroundColor: String = "#00000000"
    var backgroundCorners: Int = 30
    var scaleX: Float = 1f
    var scaleY: Float = 1f
}