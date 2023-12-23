package com.rb.crafty.dataObjects

class ElementOptionData(): java.io.Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694978376793970L
    }

    var elementType: String = ""
    var optionType: String = ""
    var name: String = ""

    constructor(name: String, optionType: String, elementType: String): this() {
        this.optionType = optionType
        this.elementType = elementType
        this.name = name
    }
}