package com.floatingpanda.tasktracker.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord

class RepeatableTaskRecordArrayAdapter(
    context: Context,
    val records: MutableList<RepeatableTaskRecord>
) :
    ArrayAdapter<RepeatableTaskRecord>(context, 0, records) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_active_record, parent)

        val record = records.get(position)

        val taskText = listView.findViewById<TextView>(R.id.item_name)
        taskText.setText(record.title)


        val checkBox = listView.findViewById<CheckBox>(R.id.item_checkbox)
        val checkBoxSuperscript = listView.findViewById<TextView>(R.id.item_checkbox_superscript)
        if (record.isCompleteForSubPeriod()) {
            checkBox.isChecked = true
            checkBoxSuperscript.visibility = View.INVISIBLE
        } else {
            val timesLeft = record.getTimesLeftForSubPeriod()
            if (timesLeft == 1) {
                checkBoxSuperscript.visibility = View.INVISIBLE
            } else {
                checkBoxSuperscript.setText(timesLeft)
                checkBoxSuperscript.visibility = View.VISIBLE
            }
        }

        checkBox.setOnClickListener { doTaskOnce(record, checkBox, checkBoxSuperscript) }

        return super.getView(position, convertView, parent)
    }

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
            val timesLeft = record.getTimesLeftForSubPeriod()
            if (timesLeft == 1) {
                superscript.visibility = View.INVISIBLE
            } else {
                superscript.setText(timesLeft)
                superscript.visibility = View.INVISIBLE
            }
        } else {
            superscript.visibility = View.INVISIBLE
        }
    }
}