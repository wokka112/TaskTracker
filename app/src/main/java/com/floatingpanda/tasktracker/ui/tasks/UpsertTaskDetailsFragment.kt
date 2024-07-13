package com.floatingpanda.tasktracker.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.floatingpanda.tasktracker.R
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class UpsertTaskDetailsFragment : Fragment() {
    private val taskUpsertViewModel: TaskUpsertViewModel by activityViewModels { TaskUpsertViewModel.Factory }

    private lateinit var continueButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val taskArgs: UpsertTaskDetailsFragmentArgs by navArgs()
        if (!taskArgs.taskIdHexString.isNullOrBlank()) {
            val taskId: ObjectId =
                BsonObjectId.invoke(taskArgs.taskIdHexString)
            taskUpsertViewModel.populateTaskIfTaskNotPresent(taskId)
        }

        val view = inflater.inflate(R.layout.fragment_task_creation_create, container, false)
        continueButton = view.findViewById(R.id.continue_button)

        setupEditTexts(view, taskUpsertViewModel)

        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            findNavController().navigateUp()
        }
        continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_task_upsert_details_fragment_to_task_upsert_schedule_fragment)
        }

        return view
    }

    private fun setupEditTexts(rootView: View, taskUpsertViewModel: TaskUpsertViewModel) {
        val titleInput: EditText = rootView.findViewById(R.id.title_input)
        val categoryInput: EditText = rootView.findViewById(R.id.category_input)
        val infoInput: EditText = rootView.findViewById(R.id.info_input)

        taskUpsertViewModel.getTitle().observe(this.viewLifecycleOwner) {
            if (titleInput.text == null || titleInput.text.toString() != it) {
                titleInput.setText(it, TextView.BufferType.EDITABLE)
            }
            enableContinueButtonIfParametersValid()
        }
        titleInput.doAfterTextChanged {
            if (!taskUpsertViewModel.getTitle().value.equals(it?.toString())) {
                taskUpsertViewModel.setTitle(it.toString())
            }
        }

        taskUpsertViewModel.getCategory().observe(this.viewLifecycleOwner) {
            if (categoryInput.text == null || categoryInput.text.toString() != it) {
                categoryInput.setText(it, TextView.BufferType.EDITABLE)
            }
            enableContinueButtonIfParametersValid()
        }
        categoryInput.doAfterTextChanged {
            if (!taskUpsertViewModel.getCategory().value.equals(it?.toString())) {
                taskUpsertViewModel.setCategory(it.toString())
            }
        }

        taskUpsertViewModel.getInfo().observe(this.viewLifecycleOwner) {
            if (infoInput.text == null || infoInput.text.toString() != it)
                infoInput.setText(it, TextView.BufferType.EDITABLE)
        }
        infoInput.doAfterTextChanged {
            if (!taskUpsertViewModel.getInfo().value.equals(it?.toString()))
                taskUpsertViewModel.setInfo(it.toString())
        }
    }

    private fun enableContinueButtonIfParametersValid() {
        continueButton.isEnabled = taskUpsertViewModel.hasValidTemplateDetails()
    }
}