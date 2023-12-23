package com.rb.crafty.sheets

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.rb.crafty.CardViewer
import com.rb.crafty.R
import com.rb.crafty.dataObjects.CardData
import com.rb.crafty.databinding.CardOptionsSheetBinding
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import com.rb.crafty.utils.Elements
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CardOptionsSheet() : BottomSheetDialogFragment() {

    lateinit var binding: CardOptionsSheetBinding

    lateinit var cardData: CardData

    lateinit var firestore: FirebaseFirestore

    var user: FirebaseUser? = null

    lateinit var storageReference: StorageReference

    var callFrom: String = "HomeUI"

    lateinit var cardView: View

    interface CardOptionsSheetListener {
        fun onRefreshClicked(cardData: CardData)

        fun onFavRemoved(cardData: CardData)

        fun onShowMessage(text: String)

        fun onViewFullClicked()
    }


    lateinit var listener: CardOptionsSheetListener

    lateinit var colourUtils: ColourUtils

    lateinit var lockIcon: Drawable

    companion object {
        val HOMEUI = "HomeUI"
        val HIDDEN_CARDS = "HiddenCards"
        val FAV_CARDS = "FavouriteCards"
        val SOME_IDEAS = "SomeIdeas"
    }

    constructor(
        cardData: CardData,
        cardView: View,
        callFrom: String,
        listener: CardOptionsSheetListener
    ) : this() {
        this.cardData = cardData
        this.cardView = cardView
        this.callFrom = callFrom
        this.listener = listener
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       adjustSheetStyle(AppUtils.isDarkMode(requireActivity()))
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = CardOptionsSheetBinding.inflate(inflater, container, false)

        //Initialize firebase user and firestore.
        firestore = Firebase.firestore
        storageReference = Firebase.storage.reference
        user = Firebase.auth.currentUser

        colourUtils = ColourUtils(requireActivity())

        lockIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.lock_24dp)!!

        handleCallFromVisibility()

        if (user != null) {
            //Adjust the text for hidden cards option.
            firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                .collection(user!!.uid).document("doc")
                .collection(AppUtils.HIDDEN_CARDS_COLLECTION).document(cardData.id.toString())
                .get().addOnCompleteListener {
                    if (it.result.exists()) {
                        binding.hideCardOption.text = "Unhide"
                        binding.hideCardOption.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.visibility_30dp,
                            0,
                            0,
                            0
                        )

                    } else {
                        binding.hideCardOption.text = "Hide"
                        binding.hideCardOption.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.invisisble_30dp,
                            0,
                            0,
                            0
                        )
                    }
                }

            //Check premium.
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {
                if (!it.result.exists()) {

                }
            }
        }

        //Adjust the text for fav cards option.
        if (cardData.favourite) {
            binding.favCardOption.text = "Remove from ${getString(R.string.favourites)}"
            binding.favCardOption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.fav_remove_30dp,
                0,
                0,
                0
            )
        } else {
            binding.favCardOption.text = "Add to ${getString(R.string.favourites)}"
            binding.favCardOption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_favorite_30,
                0,
                0,
                0
            )
        }


        binding.deleteCardOption.setOnClickListener {
            if (user == null) {
                return@setOnClickListener
            }
            firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                .collection(user!!.uid).document("doc").collection(AppUtils.CRAFTY_CARDS_COLLECTION)
                .document(cardData.id.toString()).delete().addOnCompleteListener {
                    if (it.isSuccessful) {
                        dismiss()
                    } else {

                        listener.onShowMessage("Unable to delete your card right now, try again later")
                        dismiss()
                    }
                }

            //Delete any images associated with image elements.
            for (element in cardData.elementsList!!) {
                if (element.elementType == Elements.IMAGE_TYPE) {
                    val path =
                        "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${user!!.uid}/${AppUtils.IMAGE_ELEMENTS_REFERENCE}/${element.id}"

                    //Delete the image.
                    try {
                        storageReference.child(path).delete()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        binding.hideCardOption.setOnClickListener {
            if (user == null) {
                return@setOnClickListener
            }

            val collectionRef =
                firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                    .collection(user!!.uid).document("doc")
                    .collection(AppUtils.CRAFTY_CARDS_COLLECTION)

            val hiddenCollectionRef =
                firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                    .collection(user!!.uid).document("doc")
                    .collection(AppUtils.HIDDEN_CARDS_COLLECTION)

            hiddenCollectionRef.document(cardData.id.toString()).get().addOnCompleteListener {
                if (it.result.exists()) {
                    //Move the card back to user's crafty cards collection.
                    collectionRef.document(cardData.id.toString()).set(cardData)
                        .addOnCompleteListener { it2 ->
                            if (it2.isSuccessful) {
                                //Remove the card from the default collection.
                                hiddenCollectionRef.document(cardData.id.toString()).delete()

                                listener.onShowMessage("Successfully moved the card back to your crafty cards.")
                                dismiss()
                            }
                            else {
                                listener.onShowMessage("Unable to move the card back to your crafty cards. try again later...")
                                dismiss()
                            }
                        }
                } else {
                    //Move the card to user's hidden cards collection.
                    hiddenCollectionRef.document(cardData.id.toString()).set(cardData)
                        .addOnCompleteListener { it2 ->
                            if (it2.isSuccessful) {
                                //Remove the card from the default collection.
                                collectionRef.document(cardData.id.toString()).delete()

                                listener.onShowMessage("Successfully moved to your hidden cards")
                                dismiss()
                            }

                            else {
                                listener.onShowMessage("Unable hide the card. try again later...")
                                dismiss()
                            }
                        }
                }
            }
        }

        binding.downloadCardOption.setOnClickListener {
            if (user == null) {
                return@setOnClickListener
            }

            val name = AppUtils.uniqueContentNameGenerator(cardData.cardName)
            CoroutineScope(Dispatchers.IO).launch {
                AppUtils.saveGreetrCardToDevice(
                    requireActivity(),
                    name,
                    cardData,
                    object : AppUtils.MediaListener {
                        override fun onMediaSaved(savedPath: String) {
                            listener.onShowMessage("Card saved at Downloads/Crafty/Cards")
                            dismiss()
                        }

                        override fun onMediaSaveProgress(progress: Int) {

                        }

                        override fun onMediaSaveFailed(reason: String) {
                            listener.onShowMessage("Failed to save the card, try again later...")
                            dismiss()
                        }

                    })
            }

        }

        binding.shareCardOption.setOnClickListener {
            if (user == null) {
                return@setOnClickListener
            }

            val name = AppUtils.uniqueContentNameGenerator(cardData.cardName)
            CoroutineScope(Dispatchers.IO).launch {
                AppUtils.saveGreetrCardToDevice(
                    requireActivity(),
                    name,
                    cardData,
                    object : AppUtils.MediaListener {
                        override fun onMediaSaved(savedPath: String) {
                            val share = Intent()
                            share.setAction(Intent.ACTION_SEND)
                            share.setType("*/*")
                            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(savedPath))
                            startActivity(Intent.createChooser(share, "Share your card"))
                            dismiss()

                        }

                        override fun onMediaSaveProgress(progress: Int) {

                        }

                        override fun onMediaSaveFailed(reason: String) {

                            listener.onShowMessage("Unable to share the card, try again later...")

                            dismiss()

                        }

                    })
            }
        }

        binding.favCardOption.setOnClickListener {
            if (user != null) {
                if (cardData.favourite) {
                    firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                        .collection(user!!.uid).document("doc")
                        .collection(AppUtils.CRAFTY_CARDS_COLLECTION)
                        .document(cardData.id.toString()).update("favourite", false)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                listener.onFavRemoved(cardData)
                                listener.onShowMessage("Removed from your favourites.")
                            } else {
                                listener.onShowMessage("Problem removing from favourites, please try again later.")
                            }
                            dismiss()
                        }
                } else {
                    firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                        .collection(user!!.uid).document("doc")
                        .collection(AppUtils.CRAFTY_CARDS_COLLECTION)
                        .document(cardData.id.toString()).update("favourite", true)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                listener.onShowMessage("Added to your favourites.")

                            } else {
                                listener.onShowMessage("Problem adding to favourites, please try again later.")
                            }
                            dismiss()
                        }
                }
            }
        }

        binding.exportasImageCardOption.setOnClickListener {
            if (user == null) {
                return@setOnClickListener
            }

            //Get the entire card bitmap.
            val bitmap =
                Bitmap.createBitmap(
                    cardData.mainCardData!!.width,
                    cardData.mainCardData!!.height,
                    Bitmap.Config.ARGB_8888
                )

            val canvas = Canvas(bitmap)

            cardView.draw(canvas)

            val savingSheet = SavingSheet()
            savingSheet.show(childFragmentManager, "UseCaseOne")

            val mediaListener = object : AppUtils.MediaListener {
                override fun onMediaSaved(savedPath: String) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(5000)
                        savingSheet.dismiss()

                        listener.onShowMessage("Card image saved at Crafty/Cards")
                        dismiss()
                    }

                }

                override fun onMediaSaveProgress(progress: Int) {

                }

                override fun onMediaSaveFailed(reason: String) {
                    savingSheet.dismiss()
                    listener.onShowMessage("Unable to save card image, try again later.")

                    dismiss()

                }

            }

            val savingFileName = AppUtils.uniqueContentNameGenerator(cardData.cardName)
            CoroutineScope(Dispatchers.IO).launch {
                AppUtils.saveImageBitmap(
                    requireActivity(),
                    savingFileName,
                    AppUtils.JPG,
                    bitmap,
                    mediaListener
                )
            }
        }

        binding.refreshCardOption.setOnClickListener {
            listener.onRefreshClicked(cardData)
            dismiss()
        }

        binding.addSICardOption.setOnClickListener {
            if (user != null) {
                //Add this card to the user's list.
              val ref =  firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                    .collection(user!!.uid).document("doc")
                    .collection(AppUtils.CRAFTY_CARDS_COLLECTION)
                val id = kotlin.random.Random.nextInt(0, 1000000000)


                ref.document(id.toString()).set(cardData).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //Change the id of the card to doc's id.
                        ref.document(id.toString()).update("id", id)

                        listener.onShowMessage("Added the card successfully to your list")

                        dismiss()
                    }
                    else {
                        listener.onShowMessage("Unable to add the card to your list, try again later...")
                        dismiss()
                    }
                }

                //Load ad accordingly.
                firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {
                    if (!it.result.exists()) {
                        val adRequest = AdRequest.Builder().build()

                        InterstitialAd.load(requireActivity(), getString(R.string.some_ideas_interstitial), adRequest, object : InterstitialAdLoadCallback() {
                            override fun onAdFailedToLoad(adError: LoadAdError) {
                            }

                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                interstitialAd.show(requireActivity())
                            }
                        })

                    }
                }
            }
        }

        binding.cardInfoOption.setOnClickListener {
            val sheet = CardInfoSheet(cardData)
            sheet.show(childFragmentManager, "UseCaseOne")
        }

        binding.viewFullCardOption.setOnClickListener {
            val intent = Intent(context, CardViewer::class.java)
            intent.putExtra("cardData", cardData)

           requireActivity().startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.activity_open, R.anim.activity_pusher)
            listener.onViewFullClicked()
        }

        darkMode(AppUtils.isDarkMode(requireActivity()))
        return binding.root
    }

    fun handleCallFromVisibility() {
        when (callFrom) {
            HIDDEN_CARDS -> {
                binding.refreshCardOption.visibility = View.GONE
                binding.deleteCardOption.visibility = View.GONE
                binding.shareCardOption.visibility = View.GONE
                binding.downloadCardOption.visibility = View.GONE
                binding.exportasImageCardOption.visibility = View.GONE
                binding.favCardOption.visibility = View.GONE
                binding.addSICardOption.visibility = View.GONE
                binding.cardInfoOption.visibility = View.GONE
            }

            SOME_IDEAS -> {
                binding.addSICardOption.visibility = View.VISIBLE
                binding.refreshCardOption.visibility = View.GONE
                binding.deleteCardOption.visibility = View.GONE
                binding.shareCardOption.visibility = View.GONE
                binding.downloadCardOption.visibility = View.GONE
                binding.exportasImageCardOption.visibility = View.GONE
                binding.favCardOption.visibility = View.GONE
                binding.hideCardOption.visibility = View.GONE
                binding.cardInfoOption.visibility = View.GONE
            }

            else -> {
                binding.addSICardOption.visibility = View.GONE
            }
        }
    }

    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.cardOptionsSheetHeader.setTextColor(colourUtils.darkModeDeepPurple)

            binding.favCardOption.setTextColor(colourUtils.darkModeDeepPurple)
            binding.addSICardOption.setTextColor(colourUtils.darkModeDeepPurple)
            binding.deleteCardOption.setTextColor(colourUtils.darkModeDeepPurple)
            binding.downloadCardOption.setTextColor(colourUtils.darkModeDeepPurple)
            binding.refreshCardOption.setTextColor(colourUtils.darkModeDeepPurple)
            binding.shareCardOption.setTextColor(colourUtils.darkModeDeepPurple)
            binding.exportasImageCardOption.setTextColor(colourUtils.darkModeDeepPurple)
            binding.hideCardOption.setTextColor(colourUtils.darkModeDeepPurple)
            binding.cardInfoOption.setTextColor(colourUtils.darkModeDeepPurple)
            binding.viewFullCardOption.setTextColor(colourUtils.darkModeDeepPurple)


            TextViewCompat.setCompoundDrawableTintList(binding.favCardOption, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.addSICardOption, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.deleteCardOption, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.refreshCardOption, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.downloadCardOption, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.exportasImageCardOption, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.hideCardOption, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.shareCardOption, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.cardInfoOption, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.viewFullCardOption, ColorStateList.valueOf(colourUtils.darkModeDeepPurple))

            DrawableCompat.wrap(lockIcon).setTint(colourUtils.darkModeDeepPurple)

        }
        else {
            binding.cardOptionsSheetHeader.setTextColor(colourUtils.lightModeDeepPurple)


            binding.favCardOption.setTextColor(colourUtils.lightModeDeepPurple)
            binding.addSICardOption.setTextColor(colourUtils.lightModeDeepPurple)
            binding.deleteCardOption.setTextColor(colourUtils.lightModeDeepPurple)
            binding.downloadCardOption.setTextColor(colourUtils.lightModeDeepPurple)
            binding.refreshCardOption.setTextColor(colourUtils.lightModeDeepPurple)
            binding.shareCardOption.setTextColor(colourUtils.lightModeDeepPurple)
            binding.exportasImageCardOption.setTextColor(colourUtils.lightModeDeepPurple)
            binding.hideCardOption.setTextColor(colourUtils.lightModeDeepPurple)
            binding.cardInfoOption.setTextColor(colourUtils.lightModeDeepPurple)
            binding.viewFullCardOption.setTextColor(colourUtils.lightModeDeepPurple)


            TextViewCompat.setCompoundDrawableTintList(binding.favCardOption, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.addSICardOption, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.deleteCardOption, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.refreshCardOption, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.downloadCardOption, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.exportasImageCardOption, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.hideCardOption, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.shareCardOption, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.cardInfoOption, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))
            TextViewCompat.setCompoundDrawableTintList(binding.viewFullCardOption, ColorStateList.valueOf(colourUtils.lightModeDeepPurple))

            DrawableCompat.wrap(lockIcon).setTint(colourUtils.lightModeDeepPurple)
        }
    }

    fun adjustSheetStyle(isDark: Boolean){
        if (isDark){
            setStyle(STYLE_NORMAL,R.style.DarkBottomSheetDialogStyle)
        }
        else{
            setStyle(STYLE_NORMAL,R.style.BottomSheetDialogStyle)
        }
    }
}