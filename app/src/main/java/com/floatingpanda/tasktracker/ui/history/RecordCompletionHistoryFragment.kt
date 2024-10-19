package com.floatingpanda.tasktracker.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.floatingpanda.tasktracker.databinding.FragmentRecordCompletionHistoryBinding

class RecordCompletionHistoryFragment : Fragment() {
    private var _fragmentBinding: FragmentRecordCompletionHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _fragmentBinding!!
    private val recordCompletionHistoryViewModel: RecordCompletionHistoryViewModel by viewModels<RecordCompletionHistoryViewModel> { RecordCompletionHistoryViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBinding =
            FragmentRecordCompletionHistoryBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val recyclerView = binding.completionHistoryList

        recordCompletionHistoryViewModel.getRecordCompletions().observe(
            this.viewLifecycleOwner
        ) { recordCompletions: List<RecordCompletions> ->
            if (recordCompletions.isNotEmpty())
                recyclerView.adapter = RecordCompletionHistoryAdapter(recordCompletions) { taskId ->
                    val action =
                        RecordCompletionHistoryFragmentDirections.actionRecordCompletionsFragmentToIndividualRecordCompletionsFragment(
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