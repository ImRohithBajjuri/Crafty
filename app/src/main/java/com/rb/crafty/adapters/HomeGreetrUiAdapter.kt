package com.rb.crafty.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rb.crafty.CardViewer
import com.rb.crafty.CreatorActivity
import com.rb.crafty.sheets.CardOptionsSheet
import com.rb.crafty.R
import com.rb.crafty.dataObjects.CardData
import com.rb.crafty.databinding.GreetrHomeUiCardBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import com.rb.crafty.utils.Elements

class HomeGreetrUiAdapter() : RecyclerView.Adapter<HomeGreetrUiAdapter.ViewHolder>() {

    lateinit var dataList: MutableList<CardData>
    lateinit var context: Context

    lateinit var firestore: FirebaseFirestore
    var user: FirebaseUser? = null

    interface HomeUiAdapterListener {
        fun onFavChanged(isFavourite: Boolean, cardId: Int)

        fun onDownloadClicked(cardData: CardData)

        fun onShareClicked(cardData: CardData)

        fun onShowMessage(text: String)
    }


    lateinit var listener: HomeUiAdapterListener
    lateinit var sheetListener: CardOptionsSheet.CardOptionsSheetListener

    lateinit var colourUtils: ColourUtils

    constructor(
        dataList: MutableList<CardData>,
        context: Context,
        listener: HomeUiAdapterListener,
        sheetListener: CardOptionsSheet.CardOptionsSheetListener
    ) : this() {
        this.dataList = dataList
        this.context = context
        this.listener = listener
        this.sheetListener = sheetListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        colourUtils = ColourUtils(context)

        val view = GreetrHomeUiCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.cardBy.text = dataList[position].by
        holder.binding.cardDate.text = AppUtils.getNormalTime(dataList[position].createdOn)
        holder.binding.cardName.text = dataList[position].cardName

        holder.elements.makeGreetrCardThumb(dataList[position], object :
            Elements.ThumbnailLoaderListener {
            override fun thumbnailLoaded(view: MaterialCardView) {
                holder.binding.homeUICardContainer.removeAllViews()
                holder.binding.homeUICardContainer.addView(view)
            }
        })

        holder.binding.starButton.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_round_favorite_border_30
            )
        )
        if (dataList[position].favourite) {
            holder.binding.starButton.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_round_favorite_30
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(itemView: GreetrHomeUiCardBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
        lateinit var elements: Elements

        init {
            user = Firebase.auth.currentUser
            firestore = Firebase.firestore
            elements = Elements(context)

//            darkMode(AppUtils.isDarkMode(context))

            binding.starButton.setOnClickListener {
                val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {

                        dataList[adapterPosition].favourite = !dataList[adapterPosition].favourite

                        notifyItemChanged(adapterPosition)

                        //Notify the listener.
                        listener.onFavChanged(
                            dataList[adapterPosition].favourite,
                            dataList[adapterPosition].id
                        )
                    }


                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                })
                it.startAnimation(anim)
            }

            binding.saveButton.setOnClickListener {
                val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        if (user != null) {
                            listener.onDownloadClicked(dataList[adapterPosition])
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
                it.startAnimation(anim)
            }

            binding.viewButton.setOnClickListener {
                val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        val intent = Intent(context, CardViewer::class.java)
                        intent.putExtra("cardData", dataList[adapterPosition])
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as AppCompatActivity,
                            binding.homeUICardContainer,
                            "cardViewer"
                        )
                        (context as AppCompatActivity).startActivity(intent, options.toBundle())
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
                it.startAnimation(anim)
            }

            binding.root.setOnClickListener {
                val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        val intent = Intent(context, CreatorActivity::class.java)
                        intent.putExtra("callType", "edit")
                        intent.putExtra("cardData", dataList[adapterPosition])
                        context.startActivity(intent)
                        (context as AppCompatActivity).overridePendingTransition(
                            R.anim.activity_open,
                            R.anim.activity_pusher
                        )
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
                binding.root.startAnimation(anim)
            }

            binding.root.setOnLongClickListener {
                var optionsSheet: CardOptionsSheet? = null

                optionsSheet = CardOptionsSheet(
                    dataList[adapterPosition],
                    binding.homeUICardContainer,
                    CardOptionsSheet.HOMEUI,
                    sheetListener
                )
                optionsSheet.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "useCaseOne"
                )
                true
            }

            binding.homeUICardOptions.setOnClickListener {
                var optionsSheet: CardOptionsSheet? = null

                optionsSheet = CardOptionsSheet(
                    dataList[adapterPosition],
                    binding.homeUICardContainer,
                    CardOptionsSheet.HOMEUI,
                    sheetListener
                )
                optionsSheet!!.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "useCaseOne"
                )
            }

            binding.homeUICardInfoLay.setOnClickListener {
                if (binding.cardBy.visibility == View.VISIBLE) {
                    binding.cardBy.visibility = View.GONE
                    binding.homeUICardOptions.visibility = View.GONE
                }
                else {
                    binding.cardBy.visibility = View.VISIBLE
                    binding.homeUICardOptions.visibility = View.VISIBLE
                }
            }
        }

        fun darkMode(isDark: Boolean) {
            if (isDark) {
                binding.greetrHomeUiCard.setCardBackgroundColor(colourUtils.darkModeCardsColour)
                binding.cardName.setTextColor(Color.WHITE)
                binding.cardDate.setTextColor(Color.WHITE)
                binding.cardBy.setTextColor(Color.WHITE)

            } else {
                binding.greetrHomeUiCard.setCardBackgroundColor(Color.WHITE)
                binding.cardName.setTextColor(Color.BLACK)
                binding.cardDate.setTextColor(Color.BLACK)
                binding.cardBy.setTextColor(Color.BLACK)
            }
        }
    }
}