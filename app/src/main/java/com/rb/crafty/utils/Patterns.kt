package com.rb.crafty.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setMargins
import com.rb.crafty.R
import com.rb.crafty.dataObjects.PatternData
import com.rb.crafty.databinding.StarsBgrPatternLayoutBinding

@SuppressLint("DiscouragedApi")
class Patterns(var context: Context) {
    //Repeating patterns.
    val STAR_LIGHTS_PATTERN = "Star Lights"
    val MAPLE_LEAVES_PATTERN = "Maple Leaves"
    val RED_HEARTS_PATTERN = "Red Hearts"
    val X_MARKS_PATTERN = "X Marks"
    val TRASH_OVERLOAD_PATTERN = "Trash Overload"
    val TICK_MARKS_PATTERN = "Tick Marks"
    val AT_THE_RATE_PATTERN = "@ Pattern"
    val LIGHTNING_STRIKES_PATTERN = "Lightning Strikes"
    val INFINITY_PATTERN = "Infinity"
    val CLOUDS_PATTERN = "Clouds"

    //Up down patterns.
    val BLUE_RED_PATTERN = "Blue Red"
    val LIME_GREEN_PATTERN = "Lime Green"
    val PURPLE_PINK_PATTERN = "Purple Pink"
    val GREEN_ORANGE_PATTERN = "Green Orange"
    val YELLOW_PURPLE_PATTERN = "Yellow Purple"

    //Shape patterns.

    //Pattern types.
    val PATTERN_TYPE_REPEATING = "patternRepeating"
    val PATTERN_TYPE_UP_DOWN = "patternUpDown"
    val PATTERN_TYPE_SHAPES = "patternShapes"


    //Pattern identifiers.
    val STAR_LIGHTS_ID = "stars_bgr_pattern_layout"
    val MAPPLE_LEAVES_ID = "maple_leaf_pattern"
    val RED_HEARTS_ID = "red_hearts_pattern_layout"
    val X_MARKS_ID = "x_marks_pattern_layout"
    val TRASH_OVERLOAD_ID = "trash_pattern_layout"
    val TICK_MARKS_ID = "tick_mark_pattern"
    val AT_THE_RATE_ID = "at_the_rate_pattern"
    val LIGHTNING_STRIKES_ID = "lightning_pattern"
    val INFINITY_PATTERN_ID = "infinity_pattern"
    val CLOUDS_PATTERN_ID = "clouds_pattern"

    val BLUE_RED_PATTERN_ID = "red_blue_pattern"
    val LIME_GREEN_PATTERN_ID = "lime_green_pattern"
    val PURPLE_PINK_PATTERN_ID = "purple_pink_pattern"
    val GREEN_ORANGE_PATTERN_ID = "green_orange_pattern"
    val YELLOW_PURPLE_PATTERN_ID = "yellow_purple_pattern"



    val DEFAULT_WIDTH = 300
    val DEFAULT_HEIGHT = 500

    fun starsBackgroundPattern(width: Int, height: Int): Bitmap {
        val binding =
            StarsBgrPatternLayoutBinding.inflate(LayoutInflater.from(context), null, false)
        binding.patternParent.measure(
            View.MeasureSpec.makeMeasureSpec(
                width.toInt(),
                View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                height.toInt(),
                View.MeasureSpec.EXACTLY
            )
        )
        binding.patternParent.layout(
            0,
            0,
            binding.patternParent.measuredWidth,
            binding.patternParent.measuredHeight
        )
        val bitmap = Bitmap.createBitmap(
            binding.patternParent.width,
            binding.patternParent.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        binding.patternParent.draw(canvas)
        return bitmap
    }

    suspend fun loadPatternWithId(patternData: PatternData, width: Int, height: Int): Bitmap {
        try {
            val id = context.resources.getIdentifier(patternData.patternLayout, "layout", context.packageName)
            val view = LayoutInflater.from(context).inflate(id, null, false)
            val parent = view.findViewById<ConstraintLayout>(R.id.patternParent)

            //Modify the pattern child width and height based on provided width and height.
            var loopCount = 0

            if (patternData.patternType == PATTERN_TYPE_REPEATING) {
                when {
                    (width in (0..300) || height in (0..500)) -> {
                        var modifiedWidth = AppUtils.pxToDp(context, 20)
                        var modifiedHeight = AppUtils.pxToDp(context, 20)

                        if (patternData.patternName == MAPLE_LEAVES_PATTERN) {
                            modifiedWidth = AppUtils.pxToDp(context, 30)
                            modifiedHeight = AppUtils.pxToDp(context, 30)
                        }

                        for (i in 0 until parent.childCount) {
                            parent.getChildAt(i).layoutParams.width = modifiedWidth.toInt()
                            parent.getChildAt(i).layoutParams.height = modifiedHeight.toInt()
                            loopCount++
                        }
                    }

                    (width in (301..500) || height in (501..800)) -> {
                        var modifiedWidth = AppUtils.pxToDp(context, 30)
                        var modifiedHeight = AppUtils.pxToDp(context, 30)

                        if (patternData.patternName == MAPLE_LEAVES_PATTERN) {
                            modifiedWidth = AppUtils.pxToDp(context, 50)
                            modifiedHeight = AppUtils.pxToDp(context, 50)
                        }

                        for (i in 0 until parent.childCount) {
                            parent.getChildAt(i).layoutParams.width = modifiedWidth.toInt()
                            parent.getChildAt(i).layoutParams.height = modifiedHeight.toInt()
                            loopCount++
                        }
                    }

                    (width > 500 || height > 800) -> {
                        var modifiedWidth = AppUtils.pxToDp(context, 50)
                        var modifiedHeight = AppUtils.pxToDp(context, 50)

                        if (patternData.patternName == MAPLE_LEAVES_PATTERN) {
                            modifiedWidth = AppUtils.pxToDp(context, 80)
                            modifiedHeight = AppUtils.pxToDp(context, 80)
                        }

                        for (i in 0 until parent.childCount) {
                            parent.getChildAt(i).layoutParams.width = modifiedWidth.toInt()
                            parent.getChildAt(i).layoutParams.height = modifiedHeight.toInt()
                            loopCount++
                        }
                    }
                }
            }


            view.measure(
                View.MeasureSpec.makeMeasureSpec(
                    AppUtils.pxToDp(context, width),
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(
                    AppUtils.pxToDp(context, height),
                    View.MeasureSpec.EXACTLY
                )
            )
            view.layout(
                0,
                0,
                view.measuredWidth,
                view.measuredHeight
            )
            val bitmap = Bitmap.createBitmap(
                view.width,
                view.height,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)

            view.draw(canvas)
            return bitmap
        }
        catch (e: Exception) {
            e.printStackTrace()
            return Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)
        }
    }

    suspend fun loadPatternWithViewId(patternData: PatternData, width: Int, height: Int): View? {
        try {
            val id = context.resources.getIdentifier(patternData.patternLayout, "layout", context.packageName)
            val view = LayoutInflater.from(context).inflate(id, null, false)
            val parent = view.findViewById<ConstraintLayout>(R.id.patternParent)

            //Modify the pattern children width and height based on provided width and height.
            var loopCount = 0

            if (patternData.patternType == PATTERN_TYPE_REPEATING) {
                when {
                    (width in (0..300) || height in (0..500)) -> {
                        var modifiedWidth = AppUtils.pxToDp(context, 20)
                        var modifiedHeight = AppUtils.pxToDp(context, 20)

                        if (patternData.patternName == MAPLE_LEAVES_PATTERN) {
                            modifiedWidth = AppUtils.pxToDp(context, 30)
                            modifiedHeight = AppUtils.pxToDp(context, 30)
                        }

                        for (i in 0 until parent.childCount) {
                            parent.getChildAt(i).layoutParams.width = modifiedWidth.toInt()
                            parent.getChildAt(i).layoutParams.height = modifiedHeight.toInt()
                            loopCount++
                        }
                    }

                    (width in (301..800) || height in (501..1000)) -> {
                        var modifiedWidth = AppUtils.pxToDp(context, 30)
                        var modifiedHeight = AppUtils.pxToDp(context, 30)

                        if (patternData.patternName == MAPLE_LEAVES_PATTERN) {
                            modifiedWidth = AppUtils.pxToDp(context, 50)
                            modifiedHeight = AppUtils.pxToDp(context, 50)
                        }

                        for (i in 0 until parent.childCount) {
                            parent.getChildAt(i).layoutParams.width = modifiedWidth.toInt()
                            parent.getChildAt(i).layoutParams.height = modifiedHeight.toInt()
                            loopCount++
                        }
                    }

                    (width > 800 || height > 1000) -> {
                        var modifiedWidth = AppUtils.pxToDp(context, 50)
                        var modifiedHeight = AppUtils.pxToDp(context, 50)

                        if (patternData.patternName == MAPLE_LEAVES_PATTERN) {
                            modifiedWidth = AppUtils.pxToDp(context, 80)
                            modifiedHeight = AppUtils.pxToDp(context, 80)
                        }

                        for (i in 0 until parent.childCount) {
                            parent.getChildAt(i).layoutParams.width = modifiedWidth.toInt()
                            parent.getChildAt(i).layoutParams.height = modifiedHeight.toInt()
                            loopCount++
                        }
                    }
                }
            }


            view.measure(
                View.MeasureSpec.makeMeasureSpec(
                    AppUtils.pxToDp(context, width),
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(
                    AppUtils.pxToDp(context, height),
                    View.MeasureSpec.EXACTLY
                )
            )
            view.layout(
                0,
                0,
                view.measuredWidth,
                view.measuredHeight
            )



            return view
        }
        catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun patternsList(): MutableList<PatternData> {
        val patternsList = ArrayList<PatternData>()
        //Repeating patterns.
        patternsList.add(
            PatternData(
                STAR_LIGHTS_PATTERN,
                PATTERN_TYPE_REPEATING,
                STAR_LIGHTS_ID
            )
        )
        patternsList.add(
            PatternData(
                MAPLE_LEAVES_PATTERN,
                PATTERN_TYPE_REPEATING,
                MAPPLE_LEAVES_ID
            )
        )
        patternsList.add(
            PatternData(
                RED_HEARTS_PATTERN,
                PATTERN_TYPE_REPEATING,
                RED_HEARTS_ID
            )
        )
        patternsList.add(
            PatternData(
                X_MARKS_PATTERN,
                PATTERN_TYPE_REPEATING,
                X_MARKS_ID
            )
        )
        patternsList.add(
            PatternData(
                TRASH_OVERLOAD_PATTERN,
                PATTERN_TYPE_REPEATING,
                TRASH_OVERLOAD_ID
            )
        )
        patternsList.add(
            PatternData(
                TICK_MARKS_PATTERN,
                PATTERN_TYPE_REPEATING,
                TICK_MARKS_ID
            )
        )
        patternsList.add(
            PatternData(
                AT_THE_RATE_PATTERN,
                PATTERN_TYPE_REPEATING,
                AT_THE_RATE_ID
            )
        )
        patternsList.add(
            PatternData(
                LIGHTNING_STRIKES_PATTERN,
                PATTERN_TYPE_REPEATING,
                LIGHTNING_STRIKES_ID
            )
        )
        patternsList.add(
            PatternData(
                INFINITY_PATTERN,
                PATTERN_TYPE_REPEATING,
                INFINITY_PATTERN_ID
            )
        )
        patternsList.add(
            PatternData(
                CLOUDS_PATTERN,
                PATTERN_TYPE_REPEATING,
                CLOUDS_PATTERN_ID
            )
        )


        //Up down patterns.
        patternsList.add(
            PatternData(
                BLUE_RED_PATTERN,
                PATTERN_TYPE_UP_DOWN,
                BLUE_RED_PATTERN_ID
            )
        )
        patternsList.add(
            PatternData(
                LIME_GREEN_PATTERN,
                PATTERN_TYPE_UP_DOWN,
                LIME_GREEN_PATTERN_ID
            )
        )
        patternsList.add(
            PatternData(
                PURPLE_PINK_PATTERN,
                PATTERN_TYPE_UP_DOWN,
                PURPLE_PINK_PATTERN_ID
            )
        )
        patternsList.add(
            PatternData(
                GREEN_ORANGE_PATTERN,
                PATTERN_TYPE_UP_DOWN,
                GREEN_ORANGE_PATTERN_ID
            )
        )
        patternsList.add(
            PatternData(
                YELLOW_PURPLE_PATTERN,
                PATTERN_TYPE_UP_DOWN,
                YELLOW_PURPLE_PATTERN_ID
            )
        )


        return patternsList
    }

}