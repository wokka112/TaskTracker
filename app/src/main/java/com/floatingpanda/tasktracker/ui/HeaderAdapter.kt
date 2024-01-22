package com.floatingpanda.tasktracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.floatingpanda.tasktracker.R

class HeaderAdapter(
    private val header: String
) : RecyclerView.Adapter<HeaderAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerText: TextView

        init {
            headerText = view.findViewById(R.id.header_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.headerText.text = header
    }

    override fun getItemCount() = 1
}