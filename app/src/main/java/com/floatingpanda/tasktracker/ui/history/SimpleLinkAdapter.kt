package com.floatingpanda.tasktracker.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.floatingpanda.tasktracker.R

class SimpleLinkAdapter(
    private val linkTextsAndArguments: List<Pair<String, String>>,
    private val navFunction: (linkArgument: String) -> Unit
) : RecyclerView.Adapter<SimpleLinkAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view: View
        val linkTextView: TextView

        init {
            this.view = view;
            linkTextView = view.findViewById(R.id.link_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textAndArgument = linkTextsAndArguments.get(position)
        val text = textAndArgument.first
        val argument = textAndArgument.second

        holder.linkTextView.text = text
        holder.view.setOnClickListener {
            navFunction(argument)
        }
    }

    override fun getItemCount() = 1
}