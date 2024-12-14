package com.floatingpanda.tasktracker.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.floatingpanda.tasktracker.R
import java.time.format.DateTimeFormatter

class IndividualRecordCompletionHistoryAdapter(
    private val completions: List<IndividualRecordCompletion>,
) :
    RecyclerView.Adapter<IndividualRecordCompletionHistoryAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view: View
        val completionText: TextView
        val dateTimeText: TextView

        init {
            this.view = view;
            completionText = view.findViewById(R.id.individual_completion_completions_value)
            dateTimeText = view.findViewById(R.id.individual_completion_date_time)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_individual_record_completion, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val completion = completions.get(position)
        holder.completionText.text = completion.completionNumber.toString()
        holder.dateTimeText.text =
            completion.completionDateTime.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"))
    }

    override fun getItemCount() = completions.size
}