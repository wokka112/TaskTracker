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
        val titleText: TextView
        val completionDateTimeText: TextView
        val periodText: TextView
        val completionNumberTextView: TextView

        init {
            this.view = view;
            titleText = view.findViewById(R.id.individual_completion_record_name)
            completionDateTimeText = view.findViewById(R.id.individual_completion_date_time)
            periodText = view.findViewById(R.id.individual_completion_record_period)
            completionNumberTextView = view.findViewById(R.id.individual_completion_number)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_individual_record_completion, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val completion = completions.get(position)
        holder.titleText.text = completion.recordTitle
        holder.completionDateTimeText.text = completion.completionDateTime.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"))
        holder.periodText.text = completion.recordPeriod.value
        holder.completionNumberTextView.text = completion.completionNumber.toString()
    }

    override fun getItemCount() = completions.size
}