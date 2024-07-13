package com.floatingpanda.tasktracker.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.databinding.FragmentTasksBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by viewModels<TaskViewModel> { TaskViewModel.Factory }
    private val taskCreationViewModel: TaskCreationViewModel by activityViewModels { TaskCreationViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.tasksList

        taskViewModel.getTemplates().observe(
            this.viewLifecycleOwner
        ) { templates: List<RepeatableTaskTemplate> ->
            if (templates.isNotEmpty())
                recyclerView.adapter = TaskAdapter(templates) { id ->
                    val action =
                        TasksFragmentDirections.actionTasksToTaskDetailsFragment(id)
                    root.findNavController().navigate(action)
                }
        }

        binding.root.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            taskCreationViewModel.clear()
            root.findNavController().navigate(
                R.id.action_todo_fragment_to_task_creation_create_fragment,
            )
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}