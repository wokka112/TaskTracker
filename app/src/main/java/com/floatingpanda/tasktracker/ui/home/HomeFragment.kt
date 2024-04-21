package com.floatingpanda.tasktracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import com.floatingpanda.tasktracker.data.old.OldRepeatableTaskRecord
import com.floatingpanda.tasktracker.databinding.FragmentHomeBinding
import com.floatingpanda.tasktracker.ui.CompleteTaskAdapter
import com.floatingpanda.tasktracker.ui.HeaderAdapter
import com.floatingpanda.tasktracker.ui.IncompleteTaskAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recordsList

        homeViewModel.getRecords().observe(
            this.viewLifecycleOwner
        ) { records ->
            recyclerView.adapter =
                createConcatAdapter(records, homeViewModel)
        }

        return root
    }

    fun createConcatAdapter(
        records: List<OldRepeatableTaskRecord>,
        homeViewModel: HomeViewModel
    ): ConcatAdapter {
        val incompleteRecords = ArrayList<OldRepeatableTaskRecord>()
        val completeRecords = ArrayList<OldRepeatableTaskRecord>()

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