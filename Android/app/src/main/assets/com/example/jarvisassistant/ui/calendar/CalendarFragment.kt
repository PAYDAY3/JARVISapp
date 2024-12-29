package com.example.jarvisassistant.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jarvisassistant.databinding.FragmentCalendarBinding
import com.example.jarvisassistant.ui.tasks.TaskAdapter
import java.util.*

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendarView()
        setupRecyclerView()
        observeTasks()
    }

    private fun setupCalendarView() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            viewModel.setSelectedDate(selectedDate)
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter { task ->
            viewModel.toggleTaskCompletion(task)
        }
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }

    private fun observeTasks() {
        viewModel.tasksForSelectedDate.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            binding.textViewNoTasks.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

