package com.floatingpanda.tasktracker.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.floatingpanda.tasktracker.databinding.FragmentIndividualRecordCompletionHistoryBinding
import com.floatingpanda.tasktracker.ui.tasks.TaskDetailsFragmentArgs
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class IndividualRecordCompletionHistoryFragment : Fragment() {
    private var _fragmentBinding: FragmentIndividualRecordCompletionHistoryBinding? = null

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
            FragmentIndividualRecordCompletionHistoryBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val args: TaskDetailsFragmentArgs by navArgs()
        val id: ObjectId = BsonObjectId.invoke(args.taskIdHexString)

        val record = recordCompletionHistoryViewModel.getRecord(id)


        if (record == null) {
            val title = binding.individualCompletionHistoryTitle
            title.text = "ERROR: NO RECORD FOUND"

            Log.e("onCreateView", "Unable to find record for $id")
        } else {
            binding.individualCompletionHistoryTitle.text = record.title
            binding.individualCompletionHistoryDate.text =
                record.startDate.toString() + " - " + record.endDate.toString()

            val completions = record.getIndividualRecordCompletions()
            if (completions.isNotEmpty()) {
                val recyclerView = binding.completionHistoryList
                recyclerView.adapter = IndividualRecordCompletionHistoryAdapter(completions)
                binding.noCompletionsText.visibility = View.INVISIBLE
            } else {
                binding.noCompletionsText.visibility = View.VISIBLE
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}