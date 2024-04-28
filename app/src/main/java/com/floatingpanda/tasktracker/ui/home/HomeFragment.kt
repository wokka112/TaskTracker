package com.floatingpanda.tasktracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.databinding.FragmentHomeBinding
import com.floatingpanda.tasktracker.ui.CompleteTaskAdapter
import com.floatingpanda.tasktracker.ui.HeaderAdapter
import com.floatingpanda.tasktracker.ui.IncompleteTaskAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel> { HomeViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recordsList

        homeViewModel.getRecords().observe(
            this.viewLifecycleOwner
        ) { records: List<RepeatableTaskRecord> ->
            if (records != null && records.isNotEmpty())
                recyclerView.adapter =
                    createConcatAdapter(records, homeViewModel)
        }

        return root
    }

    fun createConcatAdapter(
        records: List<RepeatableTaskRecord>,
        homeViewModel: HomeViewModel
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
        val incompleteTaskAdapter =
            IncompleteTaskAdapter(incompleteRecords, homeViewModel::updateRecord)
        val completeHeaderAdapter = HeaderAdapter("COMPLETE TASKS");
        val completeTaskAdapter = CompleteTaskAdapter(completeRecords)
        if (completeRecords.isEmpty())
            return ConcatAdapter(incompleteHeaderAdapter, incompleteTaskAdapter)
        else
            return ConcatAdapter(
                incompleteHeaderAdapter,
                incompleteTaskAdapter,
                completeHeaderAdapter,
                completeTaskAdapter
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}