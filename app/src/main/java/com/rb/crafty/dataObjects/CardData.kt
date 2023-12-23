package com.rb.crafty.dataObjects

class CardData: java.io.Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652694978376793972L
    }

    var by: String = ""
    var to: String = ""
    var cardName: String = "Card name"
    var createdOn: Long = 0
    var updatedOn: Long = 0
    var id: Int = 0
    var creatorId: String = ""
    var mainCardData: MainCardData? = null
    var elementsList: MutableList<ElementData>? = null
    var favourite: Boolean = false
    var originalCreatorId: String = ""

}