package com.rb.crafty.sheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.rb.crafty.R
import com.rb.crafty.dataObjects.AudioData
import com.rb.crafty.dataObjects.CardData
import com.rb.crafty.dataObjects.ImageData
import com.rb.crafty.databinding.FragmentImportCardSheetBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import com.rb.crafty.utils.Elements
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.random.Random

class ImportCardSheet() : BottomSheetDialogFragment() {

    lateinit var binding: FragmentImportCardSheetBinding

    lateinit var cardData: CardData

    lateinit var firestore: FirebaseFirestore

    lateinit var storageReference: StorageReference

    var user: FirebaseUser? = null

    lateinit var colourUtils: ColourUtils

    var alreadyExists = false

    interface ImportSheetListener {
        fun onCardImported()

        fun onCardImportFailed()
    }

    lateinit var listener: ImportSheetListener

    constructor(cardData: CardData, listener: ImportSheetListener) : this() {
        this.cardData = cardData
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
        binding = FragmentImportCardSheetBinding.inflate(inflater, container, false)

        firestore = Firebase.firestore
        storageReference = Firebase.storage.reference

        user = Firebase.auth.currentUser

        colourUtils = ColourUtils(requireActivity())

        val elements = Elements(requireActivity())

        elements.makeGreetrCardThumb(cardData, object : Elements.ThumbnailLoaderListener {
            override fun thumbnailLoaded(view: MaterialCardView) {
                binding.importCardPlaceholder.addView(view)
            }

        })

        //Check if the card is already available in the user's list.
        if (user != null) {
            firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                .collection(user!!.uid).document("doc").collection(AppUtils.CRAFTY_CARDS_COLLECTION).document(cardData.id.toString()).get().addOnCompleteListener {
                    if (it.result.exists()) {
                        binding.cardAlreadyExistsTxt.visibility = View.VISIBLE
                        alreadyExists = true
                    }
                    else {
                        binding.cardAlreadyExistsTxt.visibility = View.GONE
                        alreadyExists = false
                    }
                }
        }

        binding.importCardAddButton.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    addCardToList()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        darkMode(AppUtils.isDarkMode(requireActivity()))

        return binding.root
    }

    fun addCardToList() {
        if (user != null) {
            //Upload the card to user's list.
            val ref =  firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                .collection(user!!.uid).document("doc")
                .collection(AppUtils.CRAFTY_CARDS_COLLECTION)

            //Add the image and audio content to user's image and audio storage if there's any.
            for (elementData in cardData.elementsList!!) {
                //Get the image from creator id.
                if (elementData.elementType == Elements.IMAGE_TYPE) {
                    val gson = Gson()
                    val hashMapTree = gson.toJsonTree(elementData.data)
                    val data = gson.fromJson(hashMapTree, ImageData::class.java)
                    val newId = Random.nextInt(1000000000)

                    //Update creator id and path.
                    val oldId = elementData.creatorId
                    val oldPath = data.storagePath
                    val oldElementId = elementData.id

                    data.storagePath = "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${user!!.uid}/${AppUtils.IMAGE_ELEMENTS_REFERENCE}/${newId}"
                    elementData.creatorId = user!!.uid
                    elementData.id = newId


                    val path = oldPath.ifEmpty {
                        "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${oldId}/${AppUtils.IMAGE_ELEMENTS_REFERENCE}/${oldElementId}"
                    }
                    val storageRef = storageReference.child(path)

                    runBlocking {
                        CoroutineScope(Dispatchers.Default).launch {
                            try {
                                storageRef.getStream { state, stream ->
                                    if (state.error != null) {
                                        return@getStream
                                    }
                                    //Upload this stream to the user's storage.
                                    CoroutineScope(Dispatchers.IO).launch {
                                        storageReference.child(AppUtils.USER_STORAGE_ASSETS_REFERENCE).child(user!!.uid).child(AppUtils.IMAGE_ELEMENTS_REFERENCE).child(elementData.id.toString()).putStream(stream)
                                    }
                                }
                            }
                            catch (e: IOException) {
                                e.printStackTrace()
                            }
                            catch (e: StorageException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                //Get the audio from creator id.
                if (elementData.elementType == Elements.AUDIO_TYPE) {
                    val gson = Gson()
                    val hashMapTree = gson.toJsonTree(elementData.data)
                    val data = gson.fromJson(hashMapTree, AudioData::class.java)
                    val newId = Random.nextInt(1000000000)

                    //Update creator id and path.
                    val oldId = elementData.creatorId
                    val oldPath = data.storagePath
                    val oldElementId = elementData.id

                    data.storagePath = "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${user!!.uid}/${AppUtils.AUDIO_ELEMENTS_REFERENCE}/${newId}"
                    elementData.creatorId = user!!.uid
                    elementData.id = newId


                    val path = oldPath.ifEmpty {
                        "${AppUtils.USER_STORAGE_ASSETS_REFERENCE}/${oldId}/${AppUtils.AUDIO_ELEMENTS_REFERENCE}/${oldElementId}"
                    }
                    val storageRef = storageReference.child(path)

                    runBlocking {
                        CoroutineScope(Dispatchers.Default).launch {
                            try {
                                storageRef.getStream { state, stream ->
                                    if (state.error != null) {
                                        return@getStream
                                    }
                                    //Upload this stream to the user's storage.
                                    CoroutineScope(Dispatchers.IO).launch {
                                        storageReference.child(AppUtils.USER_STORAGE_ASSETS_REFERENCE).child(user!!.uid).child(AppUtils.AUDIO_ELEMENTS_REFERENCE).child(elementData.id.toString()).putStream(stream)
                                    }
                                }
                            }
                            catch (e: IOException) {
                                e.printStackTrace()
                            }
                            catch (e: StorageException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }


            cardData.id = Random.nextInt(1000000000)
            cardData.creatorId = user!!.uid

            ref.document(cardData.id.toString()).set(cardData).addOnCompleteListener {
                if (it.isSuccessful) {
                    listener.onCardImported()
                }
                else {
                    listener.onCardImportFailed()
                }
                dismiss()
            }
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

    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.importSheetHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.cardAlreadyExistsTxt.setTextColor(colourUtils.darkModeDeepPurple)
            binding.importCardAddButton.setCardBackgroundColor(colourUtils.darkModeDeepPurple)
        }
        else {
            binding.importSheetHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.cardAlreadyExistsTxt.setTextColor(colourUtils.lightModeDeepPurple)
            binding.importCardAddButton.setCardBackgroundColor(colourUtils.lightModeDeepPurple)
        }
    }
}