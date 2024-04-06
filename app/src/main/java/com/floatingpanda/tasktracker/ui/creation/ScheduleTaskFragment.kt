package com.floatingpanda.tasktracker.ui.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.floatingpanda.tasktracker.R

class ScheduleTaskFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //TODO check how to make sure the viewmodel is the same as the one from the CreateTaskFragment
        val taskCreationViewModel =
            ViewModelProvider(this).get(TaskCreationViewModel::class.java)
        inflater.inflate(R.layout.fragment_task_creation_schedule, container)

        //TODO add in edit text setup

        // TODO add in on click listeners for back and continue buttons

        // TODO add in call to create new element in database (this will be done via the view model)

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}