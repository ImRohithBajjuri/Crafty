package com.rb.crafty.dataObjects

import java.io.Serializable

class HelpData() : Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8662694978376793972L
    }
    var text: String = "Help"
    var img: Int = 0

    constructor(text: String, img: Int): this(){
        this.text = text
        this.img = img
    }

}