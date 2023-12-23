package com.rb.crafty

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.rb.crafty.adapters.CardsUIAdapter
import com.rb.crafty.dataObjects.CardData
import com.rb.crafty.databinding.ActivityFavouriteCardsBinding
import com.rb.crafty.sheets.CardOptionsSheet
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils

class FavouriteCards : AppCompatActivity() {
    lateinit var binding: ActivityFavouriteCardsBinding

    var user: FirebaseUser? = null

    lateinit var firestore: FirebaseFirestore

    lateinit var favList: MutableList<CardData>

    lateinit var adapter: CardsUIAdapter

    lateinit var colourUtils: ColourUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteCardsBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        user = Firebase.auth.currentUser
        firestore = Firebase.firestore

        //Initialize ads if required.
        if (user != null) {
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {
                if (!it.result.exists()) {
                    val adRequest = AdRequest.Builder().build()
                    binding.favBanner.loadAd(adRequest)
                    binding.favBanner.visibility = View.VISIBLE
                }
                else {
                    binding.favBanner.visibility = View.GONE
                }
            }
        }
        else {
            binding.favBanner.visibility = View.VISIBLE
        }

        colourUtils = ColourUtils(this)

        //Initialize favourite list and adapter.
        favList = ArrayList()
        adapter = CardsUIAdapter(this, favList, CardOptionsSheet.FAV_CARDS,  object : CardOptionsSheet.CardOptionsSheetListener {
            override fun onRefreshClicked(cardData: CardData) {
                adapter.notifyItemChanged(favList.indexOf(cardData))

                AppUtils.buildSnackbar(this@FavouriteCards, "Card Refreshed", binding.root).show()
            }

            override fun onFavRemoved(cardData: CardData) {
                val iterator = favList.iterator()
                while (iterator.hasNext()) {
                    val data = iterator.next()
                    if (data.id == cardData.id) {
                        val position = favList.indexOf(data)
                        iterator.remove()
                        adapter.notifyItemRemoved(position)

                        //Show empty layout if there's no favourite cards.
                        if (favList.isEmpty()) {
                            binding.favEmptyImg.visibility = View.VISIBLE
                            binding.favEmptyTxt.visibility = View.VISIBLE
                        }
                        break
                    }
                }
            }

            override fun onShowMessage(text: String) {
                AppUtils.buildSnackbar(this@FavouriteCards, text, binding.root).show()
            }

            override fun onViewFullClicked() {

            }
        })
        val layoutManager = LinearLayoutManager(this)
        binding.favRecy.layoutManager = layoutManager
        binding.favRecy.adapter = adapter

        //Get the favourite cards only if the user is already logged in.
        if (user != null) {
            firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                .collection(user!!.uid).document("doc").collection(AppUtils.CRAFTY_CARDS_COLLECTION)
                .whereEqualTo("favourite", true).get().addOnSuccessListener {
                    for (doc in it.documents) {
                        val data = doc.toObject<CardData>()!!
                        favList.add(data)
                        adapter.notifyItemInserted(favList.indexOf(data))
                    }

                    //Show empty layout if there's no favourite cards.
                    if (it.isEmpty) {
                        binding.favEmptyImg.visibility = View.VISIBLE
                        binding.favEmptyTxt.visibility = View.VISIBLE
                    }
                }
        }

        binding.favBackButton.setOnClickListener {
            finish()
        }

        darkMode(AppUtils.isDarkMode(this))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_puller, R.anim.activity_close)
    }

    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.favParent.setBackgroundColor(Color.BLACK)
            binding.favHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.favBackButton.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)
            binding.favEmptyTxt.setTextColor(colourUtils.darkModeDeepPurple)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
            else {
                var flags: Int =
                    window.decorView.getSystemUiVisibility()

                flags =
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

                window.decorView.setSystemUiVisibility(flags)

            }
            window.statusBarColor = Color.BLACK

        }
        else {
            binding.favParent.setBackgroundColor(colourUtils.lightModeParentPurple)
            binding.favHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.favBackButton.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)
            binding.favEmptyTxt.setTextColor(colourUtils.lightModeDeepPurple)



            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                window.insetsController!!.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
            else {
                var flags: Int = binding.root.getSystemUiVisibility()
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.setSystemUiVisibility(flags)
            }

            window.statusBarColor = colourUtils.lightModeParentPurple

        }
    }
}