package com.rb.crafty.mainControls

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.rb.crafty.R
import com.rb.crafty.adapters.PatternsAdapter
import com.rb.crafty.dataObjects.MainCardData
import com.rb.crafty.dataObjects.PatternData
import com.rb.crafty.databinding.FragmentMainForegroundControlsBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.Elements
import com.rb.crafty.utils.Patterns


class MainForegroundControlsFragment() : Fragment() {

    var binding: FragmentMainForegroundControlsBinding? = null

    lateinit var listener: AppUtils.ElementOptionsControlsListener

    lateinit var colors: MutableList<String>
    var selectedColor: Int = 0
    var currentAngle: Int = 0
    lateinit var gradient: GradientDrawable

    lateinit var patternsList: MutableList<PatternData>
    lateinit var patternsAdapter: PatternsAdapter
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
        binding = FragmentMainForegroundControlsBinding.inflate(inflater, container, false)


        colors = arrayListOf("#000000", "#ffffff")
        gradient = GradientDrawable()
        gradient.cornerRadius = AppUtils.pxToDp(requireActivity(), 20).toFloat()



        patternsList = Patterns(requireActivity()).patternsList()
        patternsAdapter = PatternsAdapter(requireActivity(), patternsList, null, "main", listener)
        val layoutManager = GridLayoutManager(requireActivity(), 2)
        binding!!.mainPatternsRecy.layoutManager = layoutManager
        binding!!.mainPatternsRecy.adapter = patternsAdapter
        ViewCompat.setNestedScrollingEnabled(binding!!.mainPatternsRecy, false)





        //Gradient.
        binding!!.mainGradOneHex.setOnClickListener {
            //Set current color.
            selectedColor = 0

            binding!!.mainGradOneHex.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.selected_grad_hex_bgr
            )
            binding!!.mainGradTwoHex.background = ColorDrawable(Color.TRANSPARENT)

            //Set the color card color.
            binding!!.mainGradColorCard.setCardBackgroundColor(Color.parseColor(colors[0]))

        }

        binding!!.mainGradTwoHex.setOnClickListener {
            //Set current color.
            selectedColor = 1

            binding!!.mainGradTwoHex.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.selected_grad_hex_bgr
            )
            binding!!.mainGradOneHex.background = ColorDrawable(Color.TRANSPARENT)

            //Set the color card color.
            binding!!.mainGradColorCard.setCardBackgroundColor(Color.parseColor(colors[1]))
        }

        binding!!.mainGradColorCard.setOnClickListener {
            val listener = object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, it: Int) {
                    //Update the grad color and other UI.
                    if (selectedColor == 0) {
                        val colorString = AppUtils.intToHex(it)

                        binding!!.mainGradOneHexTxt.text = colorString
                        binding!!.mainGradOneHexIcon.imageTintList = ColorStateList.valueOf(it)
                        binding!!.mainGradColorCard.setCardBackgroundColor(it)

                        colors[0] = colorString


                        updateGradientIcon()

                        listener.onMainGradientColorsUpdate(colors)

                    }
                    else {
                        val colorString = AppUtils.intToHex(it)

                        binding!!.mainGradTwoHexTxt.text = colorString
                        binding!!.mainGradTwoHexIcon.imageTintList = ColorStateList.valueOf(it)
                        binding!!.mainGradColorCard.setCardBackgroundColor(it)


                        colors[1] = colorString


                        updateGradientIcon()


                        listener.onMainGradientColorsUpdate(colors)
                    }
                }

                override fun onDialogDismissed(dialogId: Int) {
                }

            }
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    Elements(requireActivity()).buildColorPickerDialog(listener).show(childFragmentManager, "Color")
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding!!.mainGradAngleSeek.addOnChangeListener { slider, value, fromUser ->
            currentAngle = value.toInt()
            binding!!.mainGradAngleTxt.text = value.toString()
            listener.onMainGradientAngleUpdate(value.toInt())
            updateGradientIcon()
        }





        //Color.
        binding!!.mainColorOptionCard.setOnClickListener {
            val listener = object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, color: Int) {
                    //Update the color and other UI.
                    binding!!.mainColorOptionHex.text = AppUtils.intToHex(color)

                    val drawable =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
                    DrawableCompat.wrap(drawable).setTint(color)
                    binding!!.mainColorOptionHex.setCompoundDrawablesWithIntrinsicBounds(
                        drawable,
                        null,
                        null,
                        null
                    )

                    binding!!.mainColorOptionCard.setCardBackgroundColor(color)
                    listener.onMainColorUpdate(color)
                }

                override fun onDialogDismissed(dialogId: Int) {
                }

            }
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    Elements(requireActivity()).buildColorPickerDialog(listener).show(childFragmentManager, "Color")
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        binding!!.colorSelection.setOnClickListener {
            handleSelection(AppUtils.MAIN_FOREGROUND_COLOR)
        }

        binding!!.gradientSelection.setOnClickListener {
            handleSelection(AppUtils.MAIN_FOREGROUND_GRADIENT)
        }                            

        binding!!.patternSelection.setOnClickListener {
            handleSelection(AppUtils.MAIN_FOREGROUND_PATTERN)
        }

        return binding!!.root
    }

    fun setCurrentMainForeground(mainCardData: MainCardData) {
        if (binding != null) {
            //Set the current values to the color picker.
            binding!!.mainColorOptionCard.setCardBackgroundColor(Color.parseColor(mainCardData.color))
            binding!!.mainColorOptionHex.text = mainCardData.color

            val drawable =
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_round_lens_24)!!
            DrawableCompat.wrap(drawable).setTint(Color.parseColor(mainCardData.color))
            binding!!.mainColorOptionHex.setCompoundDrawablesWithIntrinsicBounds(
                drawable,
                null,
                null,
                null
            )


            //Update gradient section.
            colors = mainCardData.gradientColors!!
            currentAngle = mainCardData.gradientAngle

            binding!!.mainGradOneHexTxt.text = mainCardData.gradientColors!![0]
            binding!!.mainGradOneHexIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(mainCardData.gradientColors!![0]))

            binding!!.mainGradTwoHexTxt.text = mainCardData.gradientColors!![1]
            binding!!.mainGradTwoHexIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(mainCardData.gradientColors!![1]))

            binding!!.mainGradColorCard.setCardBackgroundColor(Color.parseColor(mainCardData.gradientColors!![0]))

            binding!!.mainGradAngleSeek.value = mainCardData.gradientAngle.toFloat()

            updateGradientIcon()

            //Update pattern section.
            val prevItem = patternsAdapter.currentSelectedPattern
            patternsAdapter.currentSelectedPattern = mainCardData.patternData

            patternsAdapter.notifyItemChanged(patternsList.indexOf(prevItem))
            patternsAdapter.notifyItemChanged(patternsList.indexOf(patternsAdapter.currentSelectedPattern))
        }
    }

    fun animateBackgroundSelector(requiredPosX: Float, listener: Animator.AnimatorListener) {
        //Set the proper width so that it will blend well with the background
        binding!!.mainForegroundSelector.width = binding!!.gradientSelection.width

        //Get the current position x
        val currentPosX = binding!!.mainForegroundSelector.x


        //Start the animation
        val anim = ValueAnimator.ofFloat(currentPosX, requiredPosX)
        anim.addUpdateListener {
            val value = it.animatedValue
            binding!!.mainForegroundSelector.x = value as Float

        }
        anim.addListener(listener)
        anim.duration = 400
        anim.interpolator = DecelerateInterpolator()
        anim.start()
    }

    fun handleSelection(selection: String) {
        when (selection) {
           AppUtils.MAIN_FOREGROUND_COLOR -> {
                binding!!.mainForegroundSelector.visibility = View.VISIBLE
                val listener = object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                    }

                    override fun onAnimationEnd(p0: Animator) {
                        binding!!.colorSelection.background = ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.selector_bar
                        )
                        binding!!.colorSelection.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(),
                            R.color.deepPurple
                        ))
                        binding!!.colorSelection.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.white
                            )
                        )

                        binding!!.mainColorLayout.visibility = View.VISIBLE
                        binding!!.mainGradLayout.visibility = View.GONE
                        binding!!.mainPatternsRecy.visibility = View.GONE


                        binding!!.mainForegroundSelector.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: Animator) {
                    }

                    override fun onAnimationRepeat(p0: Animator) {
                    }

                }

                animateBackgroundSelector(binding!!.colorSelection.x, listener)





                binding!!.gradientSelection.setTextColor(ContextCompat.getColor(requireActivity(),
                    R.color.deepPurple
                ))
                binding!!.gradientSelection.background =
                    ColorDrawable(ContextCompat.getColor(requireActivity(), android.R.color.transparent))

                binding!!.patternSelection.setTextColor(ContextCompat.getColor(requireActivity(),
                    R.color.deepPurple
                ))
                binding!!.patternSelection.background =
                    ColorDrawable(ContextCompat.getColor(requireActivity(), android.R.color.transparent))

            }

            AppUtils.MAIN_FOREGROUND_GRADIENT-> {
                binding!!.mainForegroundSelector.visibility = View.VISIBLE
                val listener = object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                    }

                    override fun onAnimationEnd(p0: Animator) {
                        binding!!.gradientSelection.background = ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.selector_bar
                        )
                        binding!!.gradientSelection.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(),
                            R.color.deepPurple
                        ))
                        binding!!.gradientSelection.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.white
                            )
                        )

                        binding!!.mainColorLayout.visibility = View.GONE
                        binding!!.mainGradLayout.visibility = View.VISIBLE
                        binding!!.mainPatternsRecy.visibility = View.GONE

                        binding!!.mainForegroundSelector.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: Animator) {
                    }

                    override fun onAnimationRepeat(p0: Animator) {
                    }

                }

                animateBackgroundSelector(binding!!.gradientSelection.x, listener)





                binding!!.colorSelection.setTextColor(ContextCompat.getColor(requireActivity(),
                    R.color.deepPurple
                ))
                binding!!.colorSelection.background =
                    ColorDrawable(ContextCompat.getColor(requireActivity(), android.R.color.transparent))

                binding!!.patternSelection.setTextColor(ContextCompat.getColor(requireActivity(),
                    R.color.deepPurple
                ))
                binding!!.patternSelection.background =
                    ColorDrawable(ContextCompat.getColor(requireActivity(), android.R.color.transparent))
            }

            AppUtils.MAIN_FOREGROUND_PATTERN -> {
                binding!!.mainForegroundSelector.visibility = View.VISIBLE
                val listener = object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                    }

                    override fun onAnimationEnd(p0: Animator) {
                        binding!!.patternSelection.background = ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.selector_bar
                        )
                        binding!!.patternSelection.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(),
                            R.color.deepPurple
                        ))
                        binding!!.patternSelection.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.white
                            )
                        )
                        binding!!.mainColorLayout.visibility = View.GONE
                        binding!!.mainGradLayout.visibility = View.GONE
                        binding!!.mainPatternsRecy.visibility = View.VISIBLE

                        binding!!.mainForegroundSelector.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: Animator) {
                    }

                    override fun onAnimationRepeat(p0: Animator) {
                    }

                }

                animateBackgroundSelector(binding!!.patternSelection.x, listener)





                binding!!.gradientSelection.setTextColor(ContextCompat.getColor(requireActivity(),
                    R.color.deepPurple
                ))
                binding!!.gradientSelection.background =
                    ColorDrawable(ContextCompat.getColor(requireActivity(), android.R.color.transparent))

                binding!!.colorSelection.setTextColor(ContextCompat.getColor(requireActivity(),
                    R.color.deepPurple
                ))
                binding!!.colorSelection.background =
                    ColorDrawable(ContextCompat.getColor(requireActivity(), android.R.color.transparent))
            }
        }
        listener.onMainForegroundTypeUpdate(selection)
    }

    fun updateGradientIcon() {
        val intColorArray = intArrayOf(Color.parseColor(colors[0]), Color.parseColor(colors[1]))
        gradient.colors = intColorArray
        when (currentAngle) {
            0 -> {gradient.orientation = GradientDrawable.Orientation.LEFT_RIGHT}
            45 -> {gradient.orientation = GradientDrawable.Orientation.TL_BR}
            90 -> {gradient.orientation = GradientDrawable.Orientation.TOP_BOTTOM}
            135 -> {gradient.orientation = GradientDrawable.Orientation.TR_BL}
            180 -> {gradient.orientation = GradientDrawable.Orientation.RIGHT_LEFT}
            225 -> {gradient.orientation = GradientDrawable.Orientation.BR_TL}
            270 -> {gradient.orientation = GradientDrawable.Orientation.BOTTOM_TOP}
            315 -> {gradient.orientation = GradientDrawable.Orientation.BL_TR}
        }
        binding!!.mainGradOutIcon.background = gradient

    }

}