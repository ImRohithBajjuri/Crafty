package com.rb.crafty

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.core.provider.FontsContractCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.rb.crafty.adapters.FontsAdapter
import com.rb.crafty.dataObjects.FontData
import com.rb.crafty.dataObjects.UIFontData
import com.rb.crafty.databinding.ActivityAddedFontsBinding
import com.rb.crafty.R
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddedFontsActivity : AppCompatActivity(), FontsAdapter.FontsAdapterListener {
    lateinit var binding: ActivityAddedFontsBinding

    var user: FirebaseUser? = null

    lateinit var firestore: FirebaseFirestore

    lateinit var fontsList: MutableList<UIFontData>

    lateinit var adapter: FontsAdapter

    lateinit var colourUtils: ColourUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddedFontsBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        user = Firebase.auth.currentUser
        firestore = Firebase.firestore

        //Initialize ads if required.
        if (user != null) {
            firestore.collection(AppUtils.PREMIUM_USERS_COLLECTION).document(user!!.uid).get().addOnCompleteListener {
                if (!it.result.exists()) {
                    val adRequest = AdRequest.Builder().build()
                    binding.addedFontsBanner.loadAd(adRequest)
                    binding.addedFontsBanner.visibility = View.VISIBLE
                }
                else {
                    binding.addedFontsBanner.visibility = View.GONE
                }
            }
        }
        else {
            binding.addedFontsBanner.visibility = View.VISIBLE
        }

        colourUtils = ColourUtils(this)

        fontsList = ArrayList()
        adapter = FontsAdapter(this, fontsList, "profile", this)
        val layoutManager = LinearLayoutManager(this)
        binding.addedFontsRecy.layoutManager = layoutManager
        binding.addedFontsRecy.adapter = adapter

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
           getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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

                //Remove the font here.
                removeFont(fontsList[viewHolder.adapterPosition])
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
        itemTouchHelper.attachToRecyclerView(binding.addedFontsRecy)

        //Get the user's added fonts list.
        if (user != null) {
            firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                .collection(user!!.uid).document("doc")
                .collection(AppUtils.USER_ADDED_FONTS_COLLECTION).get().addOnSuccessListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        loadAddedFonts(0, it.documents)

                        //Show empty layout if there's no added fonts.
                        if (it.isEmpty) {
                            binding.addedFontsEmptyTxt.visibility = View.VISIBLE
                            binding.addedFontsEmptyImg.visibility = View.VISIBLE
                        }
                    }
                }
        }

        binding.addedFontsBackButton.setOnClickListener {
            finish()
        }

        darkMode(AppUtils.isDarkMode(this))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_puller, R.anim.activity_close)
    }

    override fun onFontSelected(fontData: FontData) {

    }

    override fun onSearchFontAddClicked(fontData: FontData) {

    }


    suspend fun loadAddedFonts(count: Int, documents: MutableList<DocumentSnapshot>) {
        //Only proceed if the fragment is attached.
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
           this,
            data!!,
            object : FontsContractCompat.FontRequestCallback() {
                override fun onTypefaceRetrieved(typeface: Typeface?) {
                    uiFontData.typeface = typeface
                    fontsList.add(uiFontData)
                    adapter.notifyItemInserted(fontsList.indexOf(uiFontData))

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
        paint.color = ContextCompat.getColor(this, R.color.deepPurple)

        c.drawRect(paintRectF, paint)
        val draw = ContextCompat.getDrawable(this, R.drawable.font_delete_100dp)!!
        val icon = Bitmap.createBitmap(
            draw.intrinsicWidth,
            draw.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(icon)
        draw.setBounds(0, 0, canvas.width, canvas.height)
        draw.draw(canvas)


        //Create a difference 0f 30 to draw the rect for the icon. Also add 10 margin.
        val diff = AppUtils.pxToDp(this, 30)
        val margin = AppUtils.pxToDp(this, 10)
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
        //Get the position.
        val position = fontsList.indexOf(uiFontData)

        //Remove the font from firestore.
        if (user == null) {
            return
        }

        firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
            .collection(user!!.uid).document("doc")
            .collection(AppUtils.USER_ADDED_FONTS_COLLECTION)
            .whereEqualTo("id", uiFontData.fontData!!.id).get().addOnSuccessListener { it ->
                for (doc in it.documents) {
                    doc.reference.delete().addOnCompleteListener { it2 ->
                        if (it2.isSuccessful) {

                            //Remove from the UI.
                            val iterator = fontsList.iterator()
                            while (iterator.hasNext()) {
                                val data = iterator.next()
                                if (data.fontData!!.id == uiFontData.fontData!!.id) {
                                    val removePos = fontsList.indexOf(data)
                                    iterator.remove()
                                    adapter.notifyItemRemoved(removePos)

                                    //Show the message.
                                    val snackbar = AppUtils.buildSnackbar(
                                        this,
                                        "Removed font from your list successfully",
                                        binding.root
                                    )
                                    snackbar.setAction("UNDO") {
                                        //Add back the font to the list with it's position.
                                        firestore.collection(AppUtils.USER_ASSETS_COLLECTION)
                                            .document("doc").collection(user!!.uid).document("doc")
                                            .collection(AppUtils.USER_ADDED_FONTS_COLLECTION)
                                            .document(uiFontData.fontData!!.fontName)
                                            .set(uiFontData.fontData!!).addOnCompleteListener {result ->
                                                if (result.isSuccessful) {
                                                    fontsList.add(position, uiFontData)
                                                    adapter.notifyItemInserted(position)
                                                }
                                                else {
                                                    AppUtils.buildSnackbar(
                                                        this,
                                                        "Failed to add the font back",
                                                        binding.root
                                                    ).show()
                                                }

                                            }

                                    }
                                    snackbar.show()
                                    break
                                }
                            }

                        } else {
                            AppUtils.buildSnackbar(
                                this,
                                "Unable to remove this font, try again later...",
                                binding.root
                            ).show()
                        }
                    }
                }
            }
    }

    fun darkMode(isDark: Boolean) {
        if (isDark) {
            binding.addedFontsParent.setBackgroundColor(Color.BLACK)
            binding.addedFontsHeader.setTextColor(colourUtils.darkModeDeepPurple)
            binding.addedFontsBackButton.imageTintList = ColorStateList.valueOf(colourUtils.darkModeDeepPurple)

            binding.addedFontsEmptyTxt.setTextColor(colourUtils.darkModeDeepPurple)

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

            binding.addedFontsParent.setBackgroundColor(colourUtils.lightModeParentPurple)
            binding.addedFontsHeader.setTextColor(colourUtils.lightModeDeepPurple)
            binding.addedFontsBackButton.imageTintList = ColorStateList.valueOf(colourUtils.lightModeDeepPurple)

            binding.addedFontsEmptyTxt.setTextColor(colourUtils.lightModeDeepPurple)

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