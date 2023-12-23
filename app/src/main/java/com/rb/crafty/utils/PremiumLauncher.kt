package com.rb.crafty.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.google.common.collect.ImmutableList
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rb.crafty.R
import com.rb.crafty.dataObjects.PremiumData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject


class PremiumLauncher(
    var context: Context,
    var listener: PremiumLauncherListener
) {

    lateinit var firestore: FirebaseFirestore
    var user: FirebaseUser? = null

    lateinit var billingClient: BillingClient

    lateinit var purchaseListener: PurchasesUpdatedListener

    interface PremiumLauncherListener {
        fun alreadyPremium()

        fun onPurchaseSuccess()

        fun onError(reason: String)
    }

    init {
        user = Firebase.auth.currentUser
        firestore = Firebase.firestore

        val purchaseListener = PurchasesUpdatedListener { billingResult, purchases ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    CoroutineScope(Dispatchers.IO).launch {
                        completePurchase(purchase)
                    }
                }
            }
            else {
                listener.onError("Unable to process the purchase, try again later...")
            }
        }

        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(purchaseListener).build()

    }

    fun startConnection() {
        if (user != null) {
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {
                if (it.result.exists()) {
                    listener.alreadyPremium()
                }
                else {
                    billingClient.startConnection(object : BillingClientStateListener {
                        override fun onBillingServiceDisconnected() {
                            listener.onError("Unable to connect to google play, try again later...")
                        }

                        override fun onBillingSetupFinished(p0: BillingResult) {
                            if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                                getCraftyPremiumSKU()
                            }
                        }

                    })
                }
            }
        }
    }

    fun getCraftyPremiumSKU() {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(context.getString(R.string.crafty_premium_id))
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()))
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                productDetailsList ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                launchBillingFlow(productDetailsList[0])
            }
        }

    }

    fun launchBillingFlow(productDetails: ProductDetails) {

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(context as AppCompatActivity, billingFlowParams).responseCode

        if (billingResult != BillingClient.BillingResponseCode.OK) {
            val billingResult2 = billingClient.launchBillingFlow(context as AppCompatActivity, billingFlowParams).responseCode

            if (billingResult2 != BillingClient.BillingResponseCode.OK) {
                val billingResult3 = billingClient.launchBillingFlow(context as AppCompatActivity, billingFlowParams).responseCode

                if (billingResult3 != BillingClient.BillingResponseCode.OK) {

                   listener.onError("Unable to continue with purchasing premium, try again later")
                }

            }
        }
    }

    suspend fun completePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                }
            }
        }

        //Upload premium details.
        val premiumData = PremiumData()
        premiumData.purchaseToken = purchase.purchaseToken
        premiumData.orderId = purchase.orderId!!
        premiumData.premiumUserId = user!!.uid
        premiumData.premiumPurchaseDate = purchase.purchaseTime


        firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).set(premiumData).addOnCompleteListener {
            if (it.isSuccessful) {
               listener.onPurchaseSuccess()
            }
            else {
                firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).set(premiumData).addOnCompleteListener { it2 ->
                  listener.onPurchaseSuccess()
                }
            }
        }
    }

}