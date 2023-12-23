package com.rb.crafty.dataObjects

import com.rb.crafty.utils.AppUtils

class MainCardData: java.io.Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694978376793975L
    }

    var width: Int = 0
    var height: Int = 0
    var cornerRadius: Int = 20
    var color: String = "#FFFFFF"
    var strokeWidth: Int = 0
    var strokeColor: String = "#FFFFFF"
    var maxHeight: Int = 5000
    var maxWidth: Int = 5000
    var rotation: Int = 0
    var foregroundType: String = AppUtils.MAIN_FOREGROUND_COLOR
    var gradientColors: MutableList<String>? = arrayListOf("#000000", "#FFFFFF")
    var gradientAngle: Int = 0
    var patternData: PatternData? = null
    var xPosition: Float = 0f
    var yPosition: Float = 0f
    var scaleX: Float = 1f
    var scaleY: Float = 1f

}