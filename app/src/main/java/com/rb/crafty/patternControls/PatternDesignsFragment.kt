package com.rb.crafty.patternControls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.rb.crafty.adapters.PatternsAdapter
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.dataObjects.PatternData
import com.rb.crafty.dataObjects.PatternElementData
import com.rb.crafty.databinding.FragmentPatternDesignsBinding
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.Patterns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PatternDesignsFragment() : Fragment() {

    var binding: FragmentPatternDesignsBinding? = null

    lateinit var listener: AppUtils.ElementOptionsControlsListener

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
        binding = FragmentPatternDesignsBinding.inflate(inflater, container, false)

        patternsList = Patterns(requireActivity()).patternsList()


        val layoutManager = GridLayoutManager(requireActivity(), 2)
        binding!!.patternsRecy.layoutManager = layoutManager

        patternsAdapter = PatternsAdapter(requireActivity(), patternsList, null, "element", listener)
        CoroutineScope(Dispatchers.Main).launch {
            binding!!.patternsRecy.adapter = patternsAdapter
        }

        return binding!!.root
    }

    fun setCurrentPatternDesign(elementData: ElementData) {
        val patternData = elementData.data as PatternElementData
        if (binding != null) {
            if (patternsAdapter.currentSelectedPattern != null) {
                val prevItem = patternsAdapter.currentSelectedPattern
                patternsAdapter.currentSelectedPattern = patternData.pattern

                patternsAdapter.notifyItemChanged(patternsList.indexOf(prevItem))
                patternsAdapter.notifyItemChanged(patternsList.indexOf(patternsAdapter.currentSelectedPattern))
            }
        }
    }

}