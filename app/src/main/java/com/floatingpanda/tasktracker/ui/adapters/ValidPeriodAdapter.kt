package com.floatingpanda.tasktracker.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.floatingpanda.tasktracker.data.Period
import java.util.stream.Collectors


class ValidPeriodAdapter(
    context: Context,
    @LayoutRes private val resource: Int,
    objects: List<Period>,
    private var validPeriods: List<Period>
) : ArrayAdapter<String>(
    context,
    resource,
    objects.stream().map(Period::value).collect(
        Collectors.toList()
    )
) {

    fun setValidPeriods(validPeriods: List<Period>) {
        this.validPeriods = validPeriods
    }

    override fun isEnabled(position: Int): Boolean {
        val period = Period.convertFromString(getItem(position))
        return validPeriods.contains(period)
    }

    @SuppressLint("ResourceType")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val mView = super.getDropDownView(position, convertView, parent)
//        val mTextView = mView.findViewById<TextView>(android.R.layout.simple_spinner_item)
        val mTextView = mView as TextView

        if (!isEnabled(position))
            mTextView.setTextColor(Color.GRAY)
        else
            mTextView.setTextColor(Color.BLACK)

        return mView
    }
}