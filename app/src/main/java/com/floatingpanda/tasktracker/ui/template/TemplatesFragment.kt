package com.floatingpanda.tasktracker.ui.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.databinding.FragmentTemplatesBinding
import com.floatingpanda.tasktracker.ui.adapters.TemplateAdapter

class TemplatesFragment : Fragment() {
    private var _binding: FragmentTemplatesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val templateViewModel: TemplateViewModel by viewModels<TemplateViewModel> { TemplateViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemplatesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.templatesList

        templateViewModel.getTemplates().observe(
            this.viewLifecycleOwner
        ) { templates: List<RepeatableTaskTemplate> ->
            if (templates.isNotEmpty())
                recyclerView.adapter = TemplateAdapter(templates) { id ->
                    val action =
                        TemplatesFragmentDirections.actionTemplatesToTemplateDetailsFragment(id)
                    root.findNavController().navigate(action)
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}