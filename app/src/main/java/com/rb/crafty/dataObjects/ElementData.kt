package com.rb.crafty.dataObjects

class ElementData: java.io.Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694978376793969L
    }

    var elementName: String = ""
    var elementType: String = ""
    var data: Any? = null
    var id: Int = 0
    var currentSelection: ElementOptionData? = null
    var xPosition: Float = 0f
    var yPosition: Float = 0f
    var elementBy: String = ""
    var creatorId: String = ""
    var createdOn: Long = 0
    var originalCreatorId: String = ""

}