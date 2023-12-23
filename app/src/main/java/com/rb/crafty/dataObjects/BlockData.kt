package com.rb.crafty.dataObjects

class BlockData: java.io.Serializable {

    companion object{
        @JvmStatic
        val serialVersionUID = 8652694978376793968L
    }

    var color: String = "#d1c4e9"
    var width: Int = 250
    var height: Int = 250
    var rotation: Int = 0
    var corners: Int = 20
    var strokeWidth: Int = 0
    var strokeColor: String = "#311B92"
    var shadowRadius: Int = 0
    var backgroundPadding: Int = 20
    var backgroundColor: String = "#00000000"
    var backgroundCorners: Int = 30
    var scaleX: Float = 1f
    var scaleY: Float = 1f
}