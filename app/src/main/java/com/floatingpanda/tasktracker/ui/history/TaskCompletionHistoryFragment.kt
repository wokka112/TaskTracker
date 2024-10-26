package com.floatingpanda.tasktracker.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.floatingpanda.tasktracker.databinding.FragmentTaskCompletionHistoryBinding

class TaskCompletionHistoryFragment : Fragment() {
    private var _fragmentBinding: FragmentTaskCompletionHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _fragmentBinding!!
    private val taskCompletionHistoryViewModel: TaskCompletionHistoryViewModel by viewModels<TaskCompletionHistoryViewModel> { TaskCompletionHistoryViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBinding =
            FragmentTaskCompletionHistoryBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val args: TaskCompletionHistoryFragmentArgs by navArgs()
        val recyclerView = binding.taskCompletionHistoryList

        taskCompletionHistoryViewModel.getTaskRecordCompletions(args.taskIdHexString).observe(
            this.viewLifecycleOwner
        ) { recordCompletions: List<TaskRecordCompletions> ->
            if (recordCompletions.isNotEmpty())
                recyclerView.adapter = TaskCompletionHistoryAdapter(recordCompletions) { taskId ->
                    val action =
                        TaskCompletionHistoryFragmentDirections.actionRecordCompletionsFragmentToIndividualRecordCompletionsFragment(
                            taskId
                        )
                    root.findNavController().navigate(action)
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}