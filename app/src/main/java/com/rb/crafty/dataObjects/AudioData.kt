package com.rb.crafty.dataObjects

import java.io.Serializable

class AudioData: Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694278376793968L
    }

    var localPath: String = ""
    var storagePath: String = ""
    var width: Int = 200
    var height: Int = 200
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