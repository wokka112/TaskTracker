package com.floatingpanda.tasktracker.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.Period
import java.time.LocalDate

class TaskCompletionHistoryAdapter(
    private val records: List<TaskRecordCompletions>,
    private val navFunction: (id: String) -> Unit
) :
    RecyclerView.Adapter<TaskCompletionHistoryAdapter.ViewHolder>() {
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
        val completions = records.get(position)
        holder.titleText.text = completions.recordTitle
        holder.periodText.text = completions.recordPeriod.toString()
        holder.completionNumberTextView.text =
            completions.completions.toString() + "/" + completions.totalCompletions
        holder.startDateText.text = completions.periodStartDate.toString()
        holder.endDateText.text =
            calculateEndDate(completions.periodStartDate, completions.recordPeriod).toString()

        if (completions.recordPeriod == Period.DAILY) {
            holder.startDateText.visibility = View.INVISIBLE
            holder.endDateText.visibility = View.INVISIBLE
            holder.dateDelimiterText.visibility = View.INVISIBLE
            holder.dailyDateText.text = completions.periodStartDate.toString()
            holder.dailyDateText.visibility = View.VISIBLE
        }

        holder.view.setOnClickListener {
            navFunction(completions.recordId.toHexString())
        }
    }

    private fun calculateEndDate(startDate: LocalDate, repeatPeriod: Period): LocalDate {
        if (repeatPeriod == Period.DAILY)
            return startDate;
        if (repeatPeriod == Period.WEEKLY)
            return startDate.plusWeeks(1)
        if (repeatPeriod == Period.MONTHLY)
            return startDate.plusMonths(1)
        if (repeatPeriod == Period.YEARLY)
            return startDate.plusYears(1)

        return startDate
    }

    override fun getItemCount() = records.size
}