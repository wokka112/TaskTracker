package com.floatingpanda.tasktracker.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.databinding.FragmentTaskCompletionHistorySelectionBinding

class TaskCompletionHistorySelectionFragment : Fragment() {
    private var _fragmentBinding: FragmentTaskCompletionHistorySelectionBinding? = null

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
            FragmentTaskCompletionHistorySelectionBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val recyclerView = binding.taskCompletionHistorySelectionList
        taskCompletionHistoryViewModel.getTemplates()
            .observe(this.viewLifecycleOwner) { templates: List<RepeatableTaskTemplate> ->
                if (templates.isNotEmpty()) {
                    val templateTitlesAndIds: ArrayList<Pair<String, String>> = ArrayList()
                    templateTitlesAndIds.add(
                        Pair(
                            "ALL",
                            TaskCompletionHistoryViewModel.ALL_TEMPLATES_ID
                        )
                    )
                    templates.stream().forEach { template ->
                        templateTitlesAndIds.add(
                            Pair(
                                template.title,
                                template.id.toHexString()
                            )
                        )
                    }

                    recyclerView.adapter =
                        SimpleLinkAdapter(templateTitlesAndIds) { linkArgument ->
                            val action =
                                TaskCompletionHistorySelectionFragmentDirections.actionTaskCompletionHistorySelectionFragmentToTaskCompletionHistoryFragment(
                                    linkArgument
                                )
                            root.findNavController().navigate(action)
                        }
                }
            }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}