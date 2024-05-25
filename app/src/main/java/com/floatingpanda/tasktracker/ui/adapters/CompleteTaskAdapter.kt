package com.floatingpanda.tasktracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord

class CompleteTaskAdapter(
    private val records: List<RepeatableTaskRecord>
) : RecyclerView.Adapter<CompleteTaskAdapter.ViewHolder>() {
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

        val checkBox = holder.completeCheckBox
        val checkBoxSuperscript = holder.completionsLeftSuperScript
        checkBox.isChecked = true
        checkBox.isEnabled = false
        checkBoxSuperscript.visibility = View.INVISIBLE
    }

    override fun getItemCount() = records.size
}
