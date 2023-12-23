package com.rb.crafty.dataObjects

import android.graphics.Bitmap

class PatternData(): java.io.Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694978376793976L
    }

    var patternLayout: String = ""
    var patternName: String = ""
    var patternType: String = ""

    constructor(patternName: String, patternType: String, patternLayout: String): this() {
        this.patternName = patternName
        this.patternType = patternType
        this.patternLayout = patternLayout
    }
}