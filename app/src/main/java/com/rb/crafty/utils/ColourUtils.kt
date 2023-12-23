package com.rb.crafty.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.rb.crafty.R

class ColourUtils(context: Context) {
    val darkModeDeepPurple = ContextCompat.getColor(context, R.color.deepPurple3)
    val lightModeDeepPurple = ContextCompat.getColor(context, R.color.deepPurple)

    val darkModeCardsColour = ContextCompat.getColor(context, R.color.darkGrey3)

    val lightModeParentPurple =  ContextCompat.getColor(context, R.color.deepPurpleLight3)
    init {

    }
}