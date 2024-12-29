package com.example.jarvisassistant.ui.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.jarvisassistant.databinding.FragmentStatsBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPieChart()
        observeTaskStats()
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setExtraOffsets(5f, 10f, 5f, 5f)
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400)
            legend.isEnabled = false
        }
    }

    private fun observeTaskStats() {
        viewModel.taskStats.observe(viewLifecycleOwner) { stats ->
            updatePieChart(stats)
        }
    }

    private fun updatePieChart(stats: Map<String, Int>) {
        val entries = stats.map { (label, value) ->
            PieEntry(value.toFloat(), label)
        }

        val dataSet = PieDataSet(entries, "Task Statistics").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            setDrawIcons(false)
            sliceSpace = 3f
            iconsOffset = 0f
        }

        val data = PieData(dataSet).apply {
            setValueTextSize(11f)
            setValueTextColor(Color.WHITE)
        }

        binding.pieChart.apply {
            this.data = data
            highlightValues(null)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

