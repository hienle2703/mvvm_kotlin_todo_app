package com.codinginflow.mvvmtodo.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

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
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val userId = user?.uid ?: ""
    private val reference = FirebaseDatabase.getInstance().reference.child("tasks").child(userId)

    val task = state.get<TaskModel>("taskModel")

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
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }
        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updatedTask)
        } else {
            createTask()
        }
    }

    private fun createTask() = viewModelScope.launch {
        val id: String = reference.push().key ?: ""
        val newTask = TaskModel(id, taskName, taskImportance)
        reference.child(id).setValue(newTask)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: TaskModel) = viewModelScope.launch {
        reference.child(task.id).setValue(task)
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