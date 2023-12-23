package com.rb.crafty.textControls

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.provider.FontsContractCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.rb.crafty.databinding.FragmentTextFontControlBinding
import kotlinx.coroutines.*
import com.rb.crafty.R.*
import com.rb.crafty.adapters.FontsAdapter
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.dataObjects.FontData
import com.rb.crafty.dataObjects.TextData
import com.rb.crafty.dataObjects.UIFontData
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.R.color.*
import com.rb.crafty.R.drawable.*

class TextFontControlFragment() : Fragment(), FontsAdapter.FontsAdapterListener {
    var binding: FragmentTextFontControlBinding? = null

    lateinit var listener: AppUtils.ElementOptionsControlsListener

    lateinit var defaultFontsList: MutableList<UIFontData>

    lateinit var addedFontsList: MutableList<UIFontData>

    lateinit var searchFontsList: MutableList<UIFontData>

    lateinit var defaultFontsAdapter: FontsAdapter

    lateinit var addedFontsAdapter: FontsAdapter

    lateinit var searchFontsAdapter: FontsAdapter

    var user: FirebaseUser? = null

    lateinit var storageReference: StorageReference

    lateinit var firestore: FirebaseFirestore

    var isAddedFontsListenerAttached = false

    lateinit var addedFontsListener: EventListener<QuerySnapshot>

    var isCurrentSectionDefault = true


    constructor(listener: AppUtils.ElementOptionsControlsListener) : this() {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTextFontControlBinding.inflate(inflater, container, false)


        storageReference = Firebase.storage.reference
        firestore = Firebase.firestore
        user = Firebase.auth.currentUser

        defaultFontsList = ArrayList()
        addedFontsList = ArrayList()
        searchFontsList = ArrayList()


        //Load stuff when fragment is added and is attached to activity.
        if (isAdded && activity != null) {
            defaultFontsAdapter = FontsAdapter(requireActivity(), defaultFontsList, "normal", this)
            val layoutManager = LinearLayoutManager(requireActivity())
            binding!!.textFontControlDefaultRecy.layoutManager = layoutManager
            binding!!.textFontControlDefaultRecy.adapter = defaultFontsAdapter


            addedFontsAdapter = FontsAdapter(requireActivity(), addedFontsList, "normal", this)
            val layoutManager2 = LinearLayoutManager(requireActivity())
            binding!!.textFontControlYoursRecy.layoutManager = layoutManager2
            binding!!.textFontControlYoursRecy.adapter = addedFontsAdapter


            searchFontsAdapter = FontsAdapter(requireActivity(), searchFontsList, "search", this)
            val layoutManager3 = LinearLayoutManager(requireActivity())
            binding!!.textFontControlSearchRecy.layoutManager = layoutManager3
            binding!!.textFontControlSearchRecy.adapter = searchFontsAdapter

            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (requireActivity().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }


            //Add on item touch listener to add swipe functions.
            var itemTouchHelper: ItemTouchHelper? = null
            val itemTouchHelperCallback = object :
                ItemTouchHelper.SimpleCallback(0, (ItemTouchHelper.START or ItemTouchHelper.END)) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                @SuppressLint("MissingPermission")
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    //Give haptic feedback
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                60,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else {
                        vibrator.vibrate(60)
                    }

                    //Remove the font, once the item has been swiped.
                    removeFont(addedFontsList[viewHolder.adapterPosition])
                    /*  //Reset the item touch helper.
                      itemTouchHelper!!.attachToRecyclerView(null)
                      itemTouchHelper!!.attachToRecyclerView(binding!!.textFontControlYoursRecy)*/

                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    val itemView = viewHolder.itemView


                    //Adds stretchability.
                    /*   var editX=dX
                       val editLimitX=itemView.right/4

                       //Add the swipe dx value to the limit value to give stretch starter
                       val stretchedLimitX=editLimitX+dX/4
                       if (editX>stretchedLimitX){
                           //Set the limit with the dx value to give stretchy feeling
                           editX=editLimitX.toFloat()+dX/4
                       }*/


                    showRemoveCanvas(
                        c,
                        itemView.top.toFloat(),
                        itemView.left.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        itemView.height.toFloat(),
                        dX
                    )
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )


                    if (dX == 0f) {
                        super.onChildDraw(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                    }

                }

                override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                    return 0.3f
                }

            }

            itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(binding!!.textFontControlYoursRecy)


            if (user != null) {
                //Get the default fonts.
                firestore.collection(AppUtils.DEFAULT_FONT_ASSETS_COLLECTION).get()
                    .addOnSuccessListener {
                        /* val job = CoroutineScope(Dispatchers.IO).launch {
                             for (doc in it.documents) {
                                 val fontData = doc.toObject<FontData>()
                                 val uiFontData = UIFontData()
                                 uiFontData.fontData = fontData

                                 AppUtils.getTypeFace(requireActivity(), fontData!!, object : FontsContractCompat.FontRequestCallback() {
                                     override fun onTypefaceRetrieved(typeface: Typeface?) {
                                         uiFontData.typeface = typeface
                                         defaultFontsList.add(uiFontData)
                                         defaultFontsAdapter.notifyItemInserted(defaultFontsList.indexOf(uiFontData))


                                         (this@launch as Job).start()
                                         super.onTypefaceRetrieved(typeface)
                                     }

                                     override fun onTypefaceRequestFailed(reason: Int) {
                                         (this@launch as Job).start()
                                         super.onTypefaceRequestFailed(reason)
                                     }
                                 })

                                 cancel()
                             }
                         }*/

                        runBlocking {
                            CoroutineScope(Dispatchers.IO).launch {
                                loadDefaultFonts(0, it.documents)
                            }
                        }

                    }


                //Get added fonts collection and listen to changes.
                addedFontsListener =
                    EventListener<QuerySnapshot> { value, error -> //Update the added fonts list.
                        runBlocking {
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    if (isCurrentSectionDefault) {
                                        binding!!.textFontControlNoFontsTxt.visibility = View.GONE
                                        binding!!.textFontControlNoFontsDes.visibility = View.GONE
                                    } else {
                                        if (value!!.isEmpty) {
                                            binding!!.textFontControlNoFontsTxt.visibility =
                                                View.VISIBLE
                                            binding!!.textFontControlNoFontsDes.visibility =
                                                View.VISIBLE
                                        } else {
                                            binding!!.textFontControlNoFontsTxt.visibility =
                                                View.GONE
                                            binding!!.textFontControlNoFontsDes.visibility =
                                                View.GONE
                                        }
                                    }
                                }
                                updateAddedFontsList(0, value!!.documentChanges)
                            }
                        }
                    }
            }
        }



        binding!!.textFontControlSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterSearchFonts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


        binding!!.textFontControlAddFont.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (user != null) {
                        if (addedFontsList.size < 3) {
                            //Get the searchable fonts.
                            if (isAdded && activity != null) {
                                handleLayoutVisibility(true)

                                if (searchFontsList.isEmpty()) {
                                    firestore.collection(AppUtils.ALL_FONT_ASSETS_COLLECTION).get()
                                        .addOnSuccessListener {

                                            /*  val job = CoroutineScope(Dispatchers.IO).launch {
                                                  for (doc in it.documents) {
                                                      val fontData = doc.toObject<FontData>()
                                                      val uiFontData = UIFontData()
                                                      uiFontData.fontData = fontData

                                                      AppUtils.getTypeFace(requireActivity(), fontData!!, object : FontsContractCompat.FontRequestCallback() {
                                                          override fun onTypefaceRetrieved(typeface: Typeface?) {
                                                              uiFontData.typeface = typeface
                                                              searchFontsList.add(uiFontData)
                                                              searchFontsAdapter.notifyItemInserted(searchFontsList.indexOf(uiFontData))

                                                              (this@launch as Job).start()
                                                              super.onTypefaceRetrieved(typeface)
                                                          }

                                                          override fun onTypefaceRequestFailed(reason: Int) {
                                                              (this@launch as Job).start()
                                                              super.onTypefaceRequestFailed(reason)
                                                          }
                                                      })

                                                      cancel()
                                                  }
                                              }*/

                                            runBlocking {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    loadSearchableFonts(0, it.documents)
                                                }
                                            }

                                        }
                                }

                            }
                        }
                        else {
                            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {it2 ->
                                if (it2.result.exists()) {
                                    //Get the searchable fonts.
                                    if (isAdded && activity != null) {
                                        handleLayoutVisibility(true)

                                        if (searchFontsList.isEmpty()) {
                                            firestore.collection(AppUtils.ALL_FONT_ASSETS_COLLECTION).get()
                                                .addOnSuccessListener {

                                                    /*  val job = CoroutineScope(Dispatchers.IO).launch {
                                                          for (doc in it.documents) {
                                                              val fontData = doc.toObject<FontData>()
                                                              val uiFontData = UIFontData()
                                                              uiFontData.fontData = fontData

                                                              AppUtils.getTypeFace(requireActivity(), fontData!!, object : FontsContractCompat.FontRequestCallback() {
                                                                  override fun onTypefaceRetrieved(typeface: Typeface?) {
                                                                      uiFontData.typeface = typeface
                                                                      searchFontsList.add(uiFontData)
                                                                      searchFontsAdapter.notifyItemInserted(searchFontsList.indexOf(uiFontData))

                                                                      (this@launch as Job).start()
                                                                      super.onTypefaceRetrieved(typeface)
                                                                  }

                                                                  override fun onTypefaceRequestFailed(reason: Int) {
                                                                      (this@launch as Job).start()
                                                                      super.onTypefaceRequestFailed(reason)
                                                                  }
                                                              })

                                                              cancel()
                                                          }
                                                      }*/

                                                    runBlocking {
                                                        CoroutineScope(Dispatchers.IO).launch {
                                                            loadSearchableFonts(0, it.documents)
                                                        }
                                                    }

                                                }
                                        }

                                    }
                                }
                                else {
                                    if (isAdded && activity != null) {
                                        AppUtils.buildSnackbar(requireActivity(), "Only 3 fonts are allowed in free version. Get premium to add more", binding!!.root).show()
                                    }
                                }
                            }
                        }
                    }


                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding!!.defaultSelection.setOnClickListener {
            handleSections(true)
        }

        binding!!.yoursSelection.setOnClickListener {
            handleSections(false)

            //Attach the listener if it isn't attached.
            if (!isAddedFontsListenerAttached) {
                firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                    .collection(user!!.uid).document("doc")
                    .collection(AppUtils.USER_ADDED_FONTS_COLLECTION)
                    .addSnapshotListener(addedFontsListener)
            }

            //Set the isAdded to true.
            isAddedFontsListenerAttached = true
        }

        binding!!.textFontControlSearchClear.setOnClickListener {
            val anim = AnimUtils.pressAnim(null)

            //Empty the search.
            binding!!.textFontControlSearch.setText("")

            it.startAnimation(anim)
        }

        binding!!.textFontControlSearchBack.setOnClickListener {
            val anim = AnimUtils.pressAnim(null)
            it.startAnimation(anim)

            //Change visibility to show all fonts page.
            handleLayoutVisibility(false)
        }



        return binding!!.root
    }

    override fun onFontSelected(fontData: FontData) {
        listener.onTextFontUpdate(fontData)
    }

    override fun onSearchFontAddClicked(fontData: FontData) {
        //Add the font to the user's added fonts list.
        if (user != null && isAdded && activity != null) {

            if (addedFontsList.size < 3) {
                //Add the font to the user's list after checking if it is already added previously.
                firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                    .collection(user!!.uid).document("doc")
                    .collection(AppUtils.USER_ADDED_FONTS_COLLECTION).document(fontData.fontName).get().addOnCompleteListener {
                        if (it.result.exists()) {
                            AppUtils.buildSnackbar(
                                requireActivity(),
                                "${fontData.fontName} has already been added to your list",
                                binding!!.root
                            ).show()
                        }
                        else {
                            firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                                .collection(user!!.uid).document("doc")
                                .collection(AppUtils.USER_ADDED_FONTS_COLLECTION).document(fontData.fontName)
                                .set(fontData).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        AppUtils.buildSnackbar(
                                            requireActivity(),
                                            "${fontData.fontName} added successfully to your list",
                                            binding!!.root
                                        ).show()
                                    }
                                    else {
                                        AppUtils.buildSnackbar(
                                            requireActivity(),
                                            "Unable to add ${fontData.fontName}, try again later...",
                                            binding!!.root
                                        ).show()
                                    }
                                }
                        }
                    }
            }
            else {
                firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {it2 ->
                    if (it2.result.exists()) {
                        //Add the font to the user's list after checking if it is already added previously.
                        firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                            .collection(user!!.uid).document("doc")
                            .collection(AppUtils.USER_ADDED_FONTS_COLLECTION).document(fontData.fontName).get().addOnCompleteListener {
                                if (it.result.exists()) {
                                    AppUtils.buildSnackbar(
                                        requireActivity(),
                                        "${fontData.fontName} has already been added to your list",
                                        binding!!.root
                                    ).show()
                                }
                                else {
                                    firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                                        .collection(user!!.uid).document("doc")
                                        .collection(AppUtils.USER_ADDED_FONTS_COLLECTION).document(fontData.fontName)
                                        .set(fontData).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                AppUtils.buildSnackbar(
                                                    requireActivity(),
                                                    "${fontData.fontName} added successfully to your list",
                                                    binding!!.root
                                                ).show()
                                            }
                                            else {
                                                AppUtils.buildSnackbar(
                                                    requireActivity(),
                                                    "Unable to add ${fontData.fontName}, try again later...",
                                                    binding!!.root
                                                ).show()
                                            }
                                        }
                                }
                            }
                    }
                    else {
                        AppUtils.buildSnackbar(requireActivity(), "Only 3 fonts are allowed in free version. Get premium to add more", binding!!.root).show()
                    }
                }
            }
    }
}

fun setCurrentTextFont(elementData: ElementData) {
    val data = elementData.data as TextData

    if (binding != null) {
        //Nothing to set if font data is null.
        if (data.font == null) {
            return
        }

        //Set the current font if its not set already.
        if (defaultFontsAdapter.currentFont == null) {
            defaultFontsAdapter.currentFont = data.font

            for (font in defaultFontsList) {
                if (font.fontData!!.id == defaultFontsAdapter.currentFont!!.id) {
                    val currentPosition = defaultFontsList.indexOf(font)
                    defaultFontsAdapter.notifyItemChanged(currentPosition)
                    break
                }
            }

            return
        }

        var prevPosition = 0
        var currentPosition = 0
        for (font in defaultFontsList) {

            //Get the previous position and set the current font and update the UI.
            if (font.fontData!!.id == defaultFontsAdapter.currentFont!!.id) {
                prevPosition = defaultFontsList.indexOf(font)
                defaultFontsAdapter.currentFont = data.font

                defaultFontsAdapter.notifyItemChanged(prevPosition)
            }

            //Get the current position and set the current font and update the UI.
            if (font.fontData!!.id == data.font!!.id) {
                currentPosition = defaultFontsList.indexOf(font)

                defaultFontsAdapter.notifyItemChanged(currentPosition)

            }
        }

    }
}

fun handleLayoutVisibility(isSearch: Boolean) {
    if (isSearch) {
        binding!!.textFontControlAllFontsLayout.visibility = View.GONE
        binding!!.textFontControlAddFontsLayout.visibility = View.VISIBLE
    } else {
        binding!!.textFontControlAllFontsLayout.visibility = View.VISIBLE
        binding!!.textFontControlAddFontsLayout.visibility = View.GONE
    }
}

fun handleSections(isDefault: Boolean) {
    if (isDefault) {
        binding!!.textFontSectionSelector.visibility = View.VISIBLE
        val listener = object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                binding!!.defaultSelection.background = ContextCompat.getDrawable(
                    requireActivity(),
                    drawable.selector_bar
                )
                binding!!.defaultSelection.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireActivity(),
                        color.deepPurple
                    )
                )
                binding!!.defaultSelection.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        color.white
                    )
                )

                binding!!.textFontControlDefaultRecy.visibility = View.VISIBLE
                binding!!.textFontControlYoursRecy.visibility = View.GONE
                binding!!.textFontControlAddFont.visibility = View.GONE
                binding!!.textFontControlNoFontsTxt.visibility = View.GONE
                binding!!.textFontControlNoFontsDes.visibility = View.GONE

                binding!!.textFontSectionSelector.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }

        }

        animateBackgroundSelector(binding!!.defaultSelection.x, listener)

        binding!!.yoursSelection.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                color.deepPurple
            )
        )
        binding!!.yoursSelection.background =
            ColorDrawable(
                ContextCompat.getColor(
                    requireActivity(),
                    android.R.color.transparent
                )
            )
    } else {
        binding!!.textFontSectionSelector.visibility = View.VISIBLE
        val listener = object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                binding!!.yoursSelection.background = ContextCompat.getDrawable(
                    requireActivity(),
                    drawable.selector_bar
                )
                binding!!.yoursSelection.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireActivity(),
                        color.deepPurple
                    )
                )
                binding!!.yoursSelection.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        color.white
                    )
                )

                binding!!.textFontControlDefaultRecy.visibility = View.GONE
                binding!!.textFontControlYoursRecy.visibility = View.VISIBLE
                binding!!.textFontControlAddFont.visibility = View.VISIBLE

                //Adjust the visibillity of no fonts text.
                if (addedFontsList.isEmpty()) {
                    binding!!.textFontControlNoFontsTxt.visibility = View.VISIBLE
                    binding!!.textFontControlNoFontsDes.visibility = View.VISIBLE
                } else {
                    binding!!.textFontControlNoFontsTxt.visibility = View.GONE
                    binding!!.textFontControlNoFontsDes.visibility = View.GONE
                }

                binding!!.textFontSectionSelector.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }

        }

        animateBackgroundSelector(binding!!.yoursSelection.x, listener)

        binding!!.defaultSelection.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                color.deepPurple
            )
        )
        binding!!.defaultSelection.background =
            ColorDrawable(
                ContextCompat.getColor(
                    requireActivity(),
                    android.R.color.transparent
                )
            )
    }

    //Update global variable.
    isCurrentSectionDefault = isDefault
}

fun filterSearchFonts(searchedText: String) {
    //Create a filtered fonts list.
    val filteredList = ArrayList<UIFontData>()

    //Do filtering only if the user has searched for a valid text, otherwise update UI to notify the user.
    if (!TextUtils.isEmpty(searchedText.trim())) {
        for (font in searchFontsList) {
            //Convert to lowercase on both sides and check if starting letters match.
            if (font.fontData!!.fontFamily.lowercase().trim()
                    .startsWith(searchedText.lowercase().trim())
            ) {
                //Add to the list if they match.
                filteredList.add(font)
                searchFontsAdapter.filterFonts(filteredList)
            }
        }
    } else {
        searchFontsAdapter.filterFonts(searchFontsList)
    }
}

fun animateBackgroundSelector(requiredPosX: Float, listener: Animator.AnimatorListener) {
    //Set the proper width so that it will blend well with the background
    binding!!.textFontSectionSelector.width = binding!!.yoursSelection.width

    //Get the current position x
    val currentPosX = binding!!.textFontSectionSelector.x


    //Start the animation
    val anim = ValueAnimator.ofFloat(currentPosX, requiredPosX)
    anim.addUpdateListener {
        val value = it.animatedValue
        binding!!.textFontSectionSelector.x = value as Float

    }
    anim.addListener(listener)
    anim.duration = 400
    anim.interpolator = DecelerateInterpolator()
    anim.start()
}

fun showRemoveCanvas(
    c: Canvas,
    top: Float,
    left: Float,
    right: Float,
    bottom: Float,
    height: Float,
    dX: Float
) {
    val width = height / 3

    val paint = Paint()
    //Draw the rect for paint based on the swipe direction.
    val paintRectF: RectF = if (dX > 0) {
        RectF(left + dX, top, left, bottom)
    } else {
        RectF(right + dX, top, right, bottom)

    }
    paint.color = ContextCompat.getColor(requireActivity(), deepPurple)

    c.drawRect(paintRectF, paint)
    val draw = ContextCompat.getDrawable(requireActivity(), font_delete_100dp)!!
    val icon = Bitmap.createBitmap(
        draw.intrinsicWidth,
        draw.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(icon)
    draw.setBounds(0, 0, canvas.width, canvas.height)
    draw.draw(canvas)


    //Create a difference 0f 30 to draw the rect for the icon. Also add 10 margin.
    val diff = AppUtils.pxToDp(requireActivity(), 30)
    val margin = AppUtils.pxToDp(requireActivity(), 10)
    val matrix = Matrix()
    val iconRectF = RectF(0f, 0f, icon.width.toFloat(), icon.height.toFloat())
    val dstIconRectF: RectF
    //Modify according to the swipe direction.
    if (dX > margin + diff) {
        dstIconRectF = RectF(left + margin, top, left + margin + diff, bottom)
        matrix.setRectToRect(iconRectF, dstIconRectF, Matrix.ScaleToFit.CENTER)
        c.drawBitmap(icon, matrix, paint)
    } else if (dX < -(margin + diff)) {
        dstIconRectF = RectF(right - margin - diff, top, right - margin, bottom)
        matrix.setRectToRect(iconRectF, dstIconRectF, Matrix.ScaleToFit.CENTER)
        c.drawBitmap(icon, matrix, paint)
    }

}

fun removeFont(uiFontData: UIFontData) {
    //Remove the data from the user's collection.
    if (user != null) {
        //Remember the position.
        val position = addedFontsList.indexOf(uiFontData)
        firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
            .collection(user!!.uid).document("doc")
            .collection(AppUtils.USER_ADDED_FONTS_COLLECTION)
            .whereEqualTo("id", uiFontData.fontData!!.id).get().addOnSuccessListener { it2 ->
                for (doc in it2.documents) {
                    doc.reference.delete().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val snackbar = AppUtils.buildSnackbar(
                                requireActivity(),
                                "Removed font from your list successfully",
                                binding!!.root
                            )
                            snackbar.setAction("UNDO") {
                                //Add back the font to the list with it's position.
                                firestore.collection(AppUtils.USER_ASSETS_COLLECTION)
                                    .document("doc").collection(user!!.uid).document("doc")
                                    .collection(AppUtils.USER_ADDED_FONTS_COLLECTION)
                                    .document(uiFontData.fontData!!.fontName)
                                    .set(uiFontData.fontData!!)
                                /*  addedFontsList.add(position, uiFontData)
                                  addedFontsAdapter.notifyItemInserted(position)*/
                            }
                            snackbar.show()
                        } else {
                            AppUtils.buildSnackbar(
                                requireActivity(),
                                "Unable to remove this font, try again later...",
                                binding!!.root
                            ).show()
                        }
                    }
                }
            }
    }

}

suspend fun loadDefaultFonts(count: Int, documents: MutableList<DocumentSnapshot>) {
    //Only proceed if the fragment is attached.
    if (isAdded && activity != null) {
        //Return once the fonts have loaded.
        if (count == documents.size) {
            return
        }

        //Load data and the typeface.
        val data = documents[count].toObject<FontData>()
        val uiFontData = UIFontData()
        uiFontData.fontData = data

        //Get the typeface and add the data to the list.
        AppUtils.getTypeFace(
            requireActivity(),
            data!!,
            object : FontsContractCompat.FontRequestCallback() {
                override fun onTypefaceRetrieved(typeface: Typeface?) {
                    uiFontData.typeface = typeface
                    defaultFontsList.add(uiFontData)
                    defaultFontsAdapter.notifyItemInserted(defaultFontsList.indexOf(uiFontData))

                    //Move the loop forward by calling the method again.
                    CoroutineScope(Dispatchers.IO).launch {
                        loadDefaultFonts(count + 1, documents)
                    }
                    super.onTypefaceRetrieved(typeface)
                }

                override fun onTypefaceRequestFailed(reason: Int) {
                    //Move the loop forward by calling the method again even if font fetching fails.
                    CoroutineScope(Dispatchers.IO).launch {
                        loadDefaultFonts(count + 1, documents)
                    }
                    super.onTypefaceRequestFailed(reason)
                }
            })
    }

}

suspend fun loadAddedFonts(count: Int, documents: MutableList<DocumentSnapshot>) {
    //Only proceed if the fragment is attached.
    if (isAdded && activity != null) {
        //Return once the fonts have loaded.
        if (count == documents.size) {
            return
        }

        //Load data and the typeface.
        val data = documents[count].toObject<FontData>()
        val uiFontData = UIFontData()
        uiFontData.fontData = data

        //Get the typeface and add the data to the list.
        AppUtils.getTypeFace(
            requireActivity(),
            data!!,
            object : FontsContractCompat.FontRequestCallback() {
                override fun onTypefaceRetrieved(typeface: Typeface?) {
                    uiFontData.typeface = typeface
                    addedFontsList.add(uiFontData)
                    addedFontsAdapter.notifyItemInserted(addedFontsList.indexOf(uiFontData))

                    //Move the loop forward by calling the method again.
                    CoroutineScope(Dispatchers.IO).launch {
                        loadAddedFonts(count + 1, documents)
                    }

                    super.onTypefaceRetrieved(typeface)
                }

                override fun onTypefaceRequestFailed(reason: Int) {
                    //Move the loop forward by calling the method again even if font fetching fails.
                    CoroutineScope(Dispatchers.IO).launch {
                        loadAddedFonts(count + 1, documents)
                    }
                    super.onTypefaceRequestFailed(reason)
                }
            })
    }
}

suspend fun loadSearchableFonts(count: Int, documents: MutableList<DocumentSnapshot>) {
    //Only proceed if the fragment is attached.
    if (isAdded && activity != null) {
        //Return once the fonts have loaded.
        if (count == documents.size) {
            return
        }

        //Load data and the typeface.
        val data = documents[count].toObject<FontData>()
        val uiFontData = UIFontData()
        uiFontData.fontData = data

        //Get the typeface and add the data to the list.
        AppUtils.getTypeFace(
            requireActivity(),
            data!!,
            object : FontsContractCompat.FontRequestCallback() {
                override fun onTypefaceRetrieved(typeface: Typeface?) {
                    uiFontData.typeface = typeface
                    searchFontsList.add(uiFontData)
                    searchFontsAdapter.notifyItemInserted(searchFontsList.indexOf(uiFontData))

                    //Move the loop forward by calling the method again.
                    CoroutineScope(Dispatchers.IO).launch {
                        loadSearchableFonts(count + 1, documents)
                    }

                    super.onTypefaceRetrieved(typeface)
                }

                override fun onTypefaceRequestFailed(reason: Int) {
                    //Move the loop forward by calling the method again even if font fetching fails.
                    CoroutineScope(Dispatchers.IO).launch {
                        loadSearchableFonts(count + 1, documents)
                    }
                    super.onTypefaceRequestFailed(reason)
                }
            })
    }
}

suspend fun updateAddedFontsList(count: Int, changes: MutableList<DocumentChange>) {

    //Only proceed if the fragment is attached.
    if (isAdded && activity != null) {
        //End the looping once count reaches the changes list size.
        if (count == changes.size) {
            return
        }

        /*      //Check the type of change and update the list.
              if (changes[count].type == DocumentChange.Type.ADDED) {
                  //Load the font with it's typeface and add it to the list.
                  val data = changes[count].document.toObject<FontData>()
                  val uiFontData = UIFontData()
                  uiFontData.fontData = data

                  //Get the typeface and add the data to the list.
                  AppUtils.getTypeFace(
                      requireActivity(),
                      data,
                      object : FontsContractCompat.FontRequestCallback() {
                          override fun onTypefaceRetrieved(typeface: Typeface?) {
                              uiFontData.typeface = typeface
                              addedFontsList.add(uiFontData)
                              addedFontsAdapter.notifyItemInserted(addedFontsList.indexOf(uiFontData))

                              //Move the loop forward by calling the method again.
                              CoroutineScope(Dispatchers.IO).launch {
                                  updateAddedFontsList(count + 1, changes)
                              }
                              super.onTypefaceRetrieved(typeface)
                          }

                          override fun onTypefaceRequestFailed(reason: Int) {
                              //Move the loop forward by calling the method again even if font fetching fails.
                              CoroutineScope(Dispatchers.IO).launch {
                                  updateAddedFontsList(count + 1, changes)
                              }

                              super.onTypefaceRequestFailed(reason)
                          }
                      })
              }
               if (changes[count].type == DocumentChange.Type.REMOVED) {
                  //Remove the font from this list.

                  val fontData = changes[count].document.toObject<FontData>()

                  //Get the iterator.
                  val iterator = addedFontsList.iterator()
                  while (iterator.hasNext()) {
                      val data = iterator.next()
                      if (data.fontData!!.id == fontData.id) {
                          val position = addedFontsList.indexOf(data)

                          iterator.remove()

                          withContext(Dispatchers.Main) {
                              addedFontsAdapter.notifyItemRemoved(position)
                          }
                      }
                  }
                  //Move the loop forward by calling the method again.
                  CoroutineScope(Dispatchers.IO).launch {
                      updateAddedFontsList(count + 1, changes)
                  }
              }
  */
        when (changes[count].type) {
            DocumentChange.Type.ADDED -> {
                //Load the font with it's typeface and add it to the list.
                val data = changes[count].document.toObject<FontData>()
                val uiFontData = UIFontData()
                uiFontData.fontData = data

                //Get the typeface and add the data to the list.
                AppUtils.getTypeFace(
                    requireActivity(),
                    data,
                    object : FontsContractCompat.FontRequestCallback() {
                        override fun onTypefaceRetrieved(typeface: Typeface?) {
                            uiFontData.typeface = typeface
                            addedFontsList.add(uiFontData)
                            addedFontsAdapter.notifyItemInserted(
                                addedFontsList.indexOf(
                                    uiFontData
                                )
                            )

                            //Move the loop forward by calling the method again.
                            CoroutineScope(Dispatchers.IO).launch {
                                updateAddedFontsList(count + 1, changes)
                            }
                            super.onTypefaceRetrieved(typeface)
                        }

                        override fun onTypefaceRequestFailed(reason: Int) {
                            //Move the loop forward by calling the method again even if font fetching fails.
                            CoroutineScope(Dispatchers.IO).launch {
                                updateAddedFontsList(count + 1, changes)
                            }

                            super.onTypefaceRequestFailed(reason)
                        }
                    })
            }
            DocumentChange.Type.REMOVED -> {
                //Remove the font from this list.

                val fontData = changes[count].document.toObject<FontData>()

                //Get the iterator.
                val iterator = addedFontsList.iterator()
                while (iterator.hasNext()) {
                    val data = iterator.next()
                    if (data.fontData!!.id == fontData.id) {
                        val position = addedFontsList.indexOf(data)

                        iterator.remove()

                        withContext(Dispatchers.Main) {
                            addedFontsAdapter.notifyItemRemoved(position)
                        }

                        //Move the loop forward by calling the method again.
                        CoroutineScope(Dispatchers.IO).launch {
                            updateAddedFontsList(count + 1, changes)
                        }
                        break
                    }
                }

            }
            DocumentChange.Type.MODIFIED -> {
                //Move the loop forward by calling the method again.
                CoroutineScope(Dispatchers.IO).launch {
                    updateAddedFontsList(count + 1, changes)
                }
            }
        }

    }
}
}