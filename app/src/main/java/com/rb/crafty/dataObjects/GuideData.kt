package com.rb.crafty.dataObjects

import java.io.Serializable

class GuideData(): Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694478376793972L
    }
    var stepText = "Step"
    var stepX = 0f
    var stepY = 0f
    constructor(stepText: String, stepX: Float, stepY: Float): this() {
        this.stepText = stepText
        this.stepX = stepX
        this.stepY = stepY
    }

}