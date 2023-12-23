package com.rb.crafty.dataObjects

import java.io.Serializable

class PremiumData: Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 8652668978376793974L
    }
    var premiumUserId: String = ""
    var purchaseToken: String = ""
    var orderId: String = ""
    var premiumPurchaseDate: Long = 0
    var isValid: Boolean = true
}