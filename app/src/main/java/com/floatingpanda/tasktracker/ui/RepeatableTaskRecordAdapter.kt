package com.floatingpanda.tasktracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord

class RepeatableTaskRecordAdapter(
    private val records: List<RepeatableTaskRecord>
) : RecyclerView.Adapter<RepeatableTaskRecordAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskText: TextView
        val completeCheckBox: CheckBox
        val completionsLeftSuperScript: TextView

        init {
            taskText = view.findViewById(R.id.item_name)
            completeCheckBox = view.findViewById(R.id.item_checkbox)
            completionsLeftSuperScript = view.findViewById(R.id.item_checkbox_superscript)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_active_record, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records.get(position)

        holder.taskText.text = record.title
        holder.completionsLeftSuperScript.text = "HElllooo"

        val checkBox = holder.completeCheckBox
        val checkBoxSuperscript = holder.completionsLeftSuperScript
        if (record.isCompleteForSubPeriod()) {
            checkBox.isChecked = true
            checkBoxSuperscript.visibility = View.INVISIBLE
        } else {
            val timesLeft: Int = record.getTimesLeftForSubPeriod()
            if (timesLeft == 1) {
                checkBoxSuperscript.visibility = View.INVISIBLE
            } else {
                checkBoxSuperscript.text = timesLeft.toString()
                checkBoxSuperscript.visibility = View.VISIBLE
            }
        }

        checkBox.setOnClickListener { doTaskOnce(record, checkBox, checkBoxSuperscript) }
    }

    override fun getItemCount() = records.size

    private fun doTaskOnce(
        record: RepeatableTaskRecord,
        checkBox: CheckBox,
        superscript: TextView
    ) {
        checkBox.isChecked = true
        record.doTaskOnce()

        if (!record.isCompleteForSubPeriod()) {
            // TODO make fade over several seconds
            // TODO do we need to do this if we're observing livedata for the array? Will it automatically
            //   update the details and display? If so, can we send a little tick symbol up via animation
            //   instead of doing this?
            checkBox.isChecked = false
            val timesLeft: Int = record.getTimesLeftForSubPeriod()
            if (timesLeft <= 1) {
                superscript.visibility = View.INVISIBLE
            } else {
                superscript.text = timesLeft.toString()
                superscript.visibility = View.VISIBLE
            }
        } else {
            superscript.visibility = View.INVISIBLE
        }
    }
}