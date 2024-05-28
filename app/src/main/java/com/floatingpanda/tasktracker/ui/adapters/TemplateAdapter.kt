package com.floatingpanda.tasktracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate

class TemplateAdapter(private val templates: List<RepeatableTaskTemplate>) :
    RecyclerView.Adapter<TemplateAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view: View
        val titleText: TextView
        val categoryText: TextView

        init {
            this.view = view;
            titleText = view.findViewById(R.id.template_name)
            categoryText = view.findViewById(R.id.template_category)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_template, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val template = templates.get(position)
        holder.titleText.text = template.title
        holder.categoryText.text = template.category

        holder.view.setOnClickListener {
            //TODO navigate to template page and send template id as attribute
        }
    }

    override fun getItemCount() = templates.size
}