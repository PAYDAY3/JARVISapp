package com.example.jarvisassistant.ui.taskdetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jarvisassistant.R
import com.example.jarvisassistant.databinding.FragmentTaskDetailBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskDetailViewModel by viewModels()
    private val args: TaskDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.loadTask(args.taskId)
        
        viewModel.task.observe(viewLifecycleOwner) { task ->
            binding.editTextTitle.setText(task.title)
            binding.editTextDescription.setText(task.description)
            binding.textViewDueDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(task.dueDate)
            binding.checkBoxCompleted.isChecked = task.isCompleted
        }

        binding.buttonSetDueDate.setOnClickListener {
            showDatePicker()
        }

        binding.buttonSave.setOnClickListener {
            saveTask()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select due date")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            viewModel.updateDueDate(Date(selection))
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun saveTask() {
        val title = binding.editTextTitle.text.toString()
        val description = binding.editTextDescription.text.toString()
        val isCompleted = binding.checkBoxCompleted.isChecked

        viewModel.updateTask(title, description, isCompleted)
        Snackbar.make(binding.root, "Task updated", Snackbar.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_task_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteTask()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteTask() {
        viewModel.deleteTask()
        Snackbar.make(binding.root, "Task deleted", Snackbar.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

