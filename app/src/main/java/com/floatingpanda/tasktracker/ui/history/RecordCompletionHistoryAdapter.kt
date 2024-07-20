package com.floatingpanda.tasktracker.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.Period
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord

class RecordCompletionHistoryAdapter(
    private val records: List<RepeatableTaskRecord>,
    private val navFunction: (id: String) -> Unit
) :
    RecyclerView.Adapter<RecordCompletionHistoryAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view: View
        val titleText: TextView
        val startDateText: TextView
        val endDateText: TextView
        val dateDelimiterText: TextView
        val dailyDateText: TextView
        val periodText: TextView
        val completionNumberTextView: TextView

        init {
            this.view = view;
            titleText = view.findViewById(R.id.completion_record_name)
            startDateText = view.findViewById(R.id.completion_date_start)
            endDateText = view.findViewById(R.id.completion_date_end)
            dateDelimiterText = view.findViewById(R.id.completion_date_delimiter)
            dailyDateText = view.findViewById(R.id.completion_date_daily)
            periodText = view.findViewById(R.id.completion_record_period)
            completionNumberTextView = view.findViewById(R.id.completion_number)
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
        val record = records.get(position)
        holder.titleText.text = record.title
        holder.periodText.text = record.repeatPeriod.value
        holder.completionNumberTextView.text =
            record.completions.size.toString() + "/" + record.template.timesPerPeriod
        holder.startDateText.text = record.startDate.toString()
        holder.endDateText.text = record.endDate.toString()

        if (record.repeatPeriod == Period.DAILY) {
            holder.startDateText.visibility = View.INVISIBLE
            holder.endDateText.visibility = View.INVISIBLE
            holder.dateDelimiterText.visibility = View.INVISIBLE
            holder.dailyDateText.text = record.startDate.toString()
            holder.dailyDateText.visibility = View.VISIBLE
        }

        holder.view.setOnClickListener {
            navFunction(record.id.toHexString())
        }
    }

    override fun getItemCount() = records.size
}