package com.codinginflow.mvvmtodo.ui.addedittask

import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.data.realtimedata.TaskModel
import com.codinginflow.mvvmtodo.ui.home.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.home.EDIT_TASK_RESULT_OK
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val userId = user?.uid ?: ""
    val reference = FirebaseDatabase.getInstance().reference.child("tasks").child(userId)

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        // input validation

        if (taskName.isBlank()) {
            // show invalid input message
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        // update/add to database
        if (task != null) {

            val updatedTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updatedTask)
        } else {
            // --- If using Local DB
            // val newTask = Task(taskName, taskImportance)
            // createTask(newTask)

            // --- If using Realtime DB from Firebase
            val id: String = reference.push().key ?: ""
            val newTask = TaskModel(id, taskName, taskImportance)
            reference.child(id).setValue(newTask).addOnCompleteListener {
                if (it.isSuccessful) {
                    // TODO: Create an event to show Toast
                } else {
                    // TODO: Create an event to show Toast
                }
            }
        }
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        // navigate back => must do through events
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        // navigate back => must do through events
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}