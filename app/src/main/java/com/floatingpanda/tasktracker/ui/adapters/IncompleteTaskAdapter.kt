package com.floatingpanda.tasktracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import io.realm.kotlin.ext.copyFromRealm

class IncompleteTaskAdapter(
    private val incompleteRecords: List<RepeatableTaskRecord>,
    private val updateRecord: (RepeatableTaskRecord) -> Unit
) : RecyclerView.Adapter<IncompleteTaskAdapter.ViewHolder>() {
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_active_record, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = incompleteRecords.get(position)
        holder.taskText.text = record.title

        val checkBox = holder.completeCheckBox
        val checkBoxSuperscript = holder.completionsLeftSuperScript

        val timesLeft: Int = record.getTimesLeftForSubPeriod()
        if (timesLeft == 1) {
            checkBoxSuperscript.visibility = View.INVISIBLE
        } else {
            checkBoxSuperscript.text = timesLeft.toString()
            checkBoxSuperscript.visibility = View.VISIBLE
        }

        checkBox.setOnClickListener { doTaskOnce(record) }
    }

    override fun getItemCount() = incompleteRecords.size

    private fun doTaskOnce(
        record: RepeatableTaskRecord,
    ) {
        //TODO is this the best way to do this??
        val copy = record.copyFromRealm()
        copy.id = record.id
        copy.doTaskOnce()
        updateRecord(copy)
    }
}
