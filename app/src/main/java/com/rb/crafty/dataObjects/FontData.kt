package com.rb.crafty.dataObjects

class FontData: java.io.Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694978376798367L
    }
    var fontName: String = "font name"
    var id: Int = 0
    var style: String = ""
    var source: String = ""
    var fontFamily: String = ""
    var weight: Int = 0
    var italic: Boolean = false
}