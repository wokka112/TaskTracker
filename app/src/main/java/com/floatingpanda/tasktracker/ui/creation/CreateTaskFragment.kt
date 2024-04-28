package com.floatingpanda.tasktracker.ui.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.floatingpanda.tasktracker.R

class CreateTaskFragment : Fragment() {
    private val taskCreationViewModel: TaskCreationViewModel by viewModels { TaskCreationViewModel.Factory }
    
    private lateinit var continueButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_creation_create, container, false)
        continueButton = view.findViewById(R.id.continue_button)

        setupEditTexts(view, taskCreationViewModel)

        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            findNavController().navigateUp()
        }
        continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_task_creation_create_fragment_to_task_creation_schedule_fragment)
        }

        return view
    }

    private fun setupEditTexts(rootView: View, taskCreationViewModel: TaskCreationViewModel) {
        val titleInput: EditText = rootView.findViewById(R.id.title_input)
        val categoryInput: EditText = rootView.findViewById(R.id.category_input)
        val infoInput: EditText = rootView.findViewById(R.id.info_input)

        taskCreationViewModel.title.observe(this.viewLifecycleOwner) {
            if (titleInput.text == null || titleInput.text.toString() != it) {
                titleInput.setText(it, TextView.BufferType.EDITABLE)
                enableContinueButtonIfParametersValid()
            }
        }
        titleInput.doAfterTextChanged {
            if (!taskCreationViewModel.title.value.equals(it?.toString())) {
                taskCreationViewModel.title.postValue(it.toString())
            }
        }

        taskCreationViewModel.category.observe(this.viewLifecycleOwner) {
            if (categoryInput.text == null || categoryInput.text.toString() != it) {
                categoryInput.setText(it, TextView.BufferType.EDITABLE)
                enableContinueButtonIfParametersValid()
            }
        }
        categoryInput.doAfterTextChanged {
            if (!taskCreationViewModel.category.value.equals(it?.toString())) {
                taskCreationViewModel.category.postValue(it.toString())
            }
        }

        taskCreationViewModel.info.observe(this.viewLifecycleOwner) {
            if (infoInput.text == null || infoInput.text.toString() != it)
                infoInput.setText(it, TextView.BufferType.EDITABLE)
        }
        infoInput.doAfterTextChanged {
            if (!taskCreationViewModel.info.value.equals(it?.toString()))
                taskCreationViewModel.info.postValue(it.toString())
        }
    }

    private fun enableContinueButtonIfParametersValid() {
        continueButton.isEnabled = taskCreationViewModel.hasValidTemplateDetails()
    }
}