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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.rb.crafty.adapters.CardsUIAdapter
import com.rb.crafty.dataObjects.CardData
import com.rb.crafty.databinding.ActivityHiddenCardsBinding
import com.rb.crafty.sheets.CardOptionsSheet
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils

class HiddenCards : AppCompatActivity() {
    lateinit var binding: ActivityHiddenCardsBinding
    lateinit var firestore: FirebaseFirestore
    var user: FirebaseUser? = null

    lateinit var hiddenList: MutableList<CardData>
    lateinit var adapter: CardsUIAdapter

    lateinit var colourUtils: ColourUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHiddenCardsBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        firestore = Firebase.firestore
        user = Firebase.auth.currentUser

        colourUtils = ColourUtils(this)

        hiddenList = ArrayList()
        val layoutManager = LinearLayoutManager(this)
        binding.hidRecy.layoutManager = layoutManager

        val adapter = CardsUIAdapter(this, hiddenList, CardOptionsSheet.HIDDEN_CARDS, object : CardOptionsSheet.CardOptionsSheetListener {
            override fun onRefreshClicked(cardData: CardData) {
                adapter.notifyItemChanged(hiddenList.indexOf(cardData))

                AppUtils.buildSnackbar(this@HiddenCards, "Card Refreshed", binding.root).show()
            }

            override fun onFavRemoved(cardData: CardData) {

            }

            override fun onShowMessage(text: String) {
                AppUtils.buildSnackbar(this@HiddenCards, text, binding.root).show()
            }

            override fun onViewFullClicked() {

            }
        })
        binding.hidRecy.adapter = adapter

        if (user != null) {
            firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                .collection(user!!.uid).document("doc").collection(AppUtils.HIDDEN_CARDS_COLLECTION).addSnapshotListener { value, error ->
                    for (change in value!!.documentChanges) {
                        if (change.type == DocumentChange.Type.ADDED) {
                            val data = change.document.toObject<CardData>()
                            hiddenList.add(data)
                            adapter.notifyItemInserted(hiddenList.indexOf(data))
                        }

                        if (change.type == DocumentChange.Type.REMOVED) {
                            val data = change.document.toObject<CardData>()
                            val iterator = hiddenList.iterator()
                            while (iterator.hasNext()) {
                                val cardData = iterator.next()
                                if (cardData.id == data.id) {
                                    val position = hiddenList.indexOf(cardData)
                                    iterator.remove()
                                    adapter.notifyItemRemoved(position)
                                    break
                                }
                            }
                        }
                    }

                    //Show empty layout if there's no hidden cards.
                    if (value.documents.isEmpty()) {
                        binding.hidEmptyImg.visibility = View.VISIBLE
                        binding.hidEmptyTxt.visibility = View.VISIBLE
                    }
                }
        }


        binding.hidBackButton.setOnClickListener {
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
            binding.hidParent.setBackgroundColor(Color.BLACK)
            binding.hidHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.hidBackButton.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)

            binding.hidEmptyTxt.setTextColor(colourUtils.darkModeDeepPurple)

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
            binding.hidParent.setBackgroundColor(colourUtils.lightModeParentPurple)
            binding.hidHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.hidBackButton.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)

            binding.hidEmptyTxt.setTextColor(colourUtils.lightModeDeepPurple)


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