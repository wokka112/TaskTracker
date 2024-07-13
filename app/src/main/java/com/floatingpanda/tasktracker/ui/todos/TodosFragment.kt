package com.floatingpanda.tasktracker.ui.todos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.databinding.FragmentTasksBinding
import com.floatingpanda.tasktracker.ui.tasks.TaskUpsertViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodosFragment : Fragment() {
    private var _fragmentBinding: FragmentTasksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _fragmentBinding!!
    private val todoViewModel: TodoViewModel by viewModels<TodoViewModel> { TodoViewModel.Factory }
    private val taskUpsertViewModel: TaskUpsertViewModel by activityViewModels { TaskUpsertViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBinding = FragmentTasksBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val recyclerView = binding.tasksList

        todoViewModel.getRecords().observe(
            this.viewLifecycleOwner
        ) { records: List<RepeatableTaskRecord> ->
            if (records != null && records.isNotEmpty())
                recyclerView.adapter =
                    createConcatAdapter(records, todoViewModel)
        }

        binding.root.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            taskUpsertViewModel.clear()
            root.findNavController().navigate(
                R.id.action_todo_fragment_to_task_upsert_details_fragment,
            )
        }
        return root
    }

    fun createConcatAdapter(
        records: List<RepeatableTaskRecord>,
        todoViewModel: TodoViewModel
    ): ConcatAdapter {
        val incompleteRecords = ArrayList<RepeatableTaskRecord>()
        val completeRecords = ArrayList<RepeatableTaskRecord>()

        for (record in records) {
            if (record.isCompleteForSubPeriod())
                completeRecords.add(record)
            else
                incompleteRecords.add(record)
        }

        val incompleteHeaderAdapter = HeaderAdapter("INCOMPLETE TASKS")
        val incompleteTodoAdapter =
            IncompleteTodoAdapter(incompleteRecords, todoViewModel::updateRecord)
        val completeHeaderAdapter = HeaderAdapter("COMPLETE TASKS");
        val completeTodoAdapter = CompleteTodoAdapter(completeRecords)
        if (completeRecords.isEmpty())
            return ConcatAdapter(incompleteHeaderAdapter, incompleteTodoAdapter)
        else
            return ConcatAdapter(
                incompleteHeaderAdapter,
                incompleteTodoAdapter,
                completeHeaderAdapter,
                completeTodoAdapter
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}