package com.rb.crafty.dataObjects

class TextData: java.io.Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694978376793977L
    }

    var text: String = "Your text"
    var color: String = "#000000"
    var size: Int = 21
    var font: FontData? = null
    var rotation: Int = 0
    var vertDepth: Int = 0
    var horzDepth: Int = 0
    var depthRadius: Int = 0
    var depthColor: String = "#000000"
    var backgroundPadding: Int = 20
    var backgroundColor: String = "#00000000"
    var backgroundCorners: Int = 30
    var scaleX: Float = 1f
    var scaleY: Float = 1f

}