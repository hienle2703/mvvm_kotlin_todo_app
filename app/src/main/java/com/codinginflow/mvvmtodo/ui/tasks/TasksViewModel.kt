package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*

import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.data.realtimedata.TaskModel
import com.codinginflow.mvvmtodo.data.repositories.TaskDataSource
import com.codinginflow.mvvmtodo.ui.home.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.home.EDIT_TASK_RESULT_OK

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    //    val searchQuery = MutableStateFlow("")
    val searchQuery = state.getLiveData("searchQuery", "")

    val auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val userId = user?.uid ?: ""
    private val reference = FirebaseDatabase.getInstance().reference.child("tasks").child(userId)

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow() // turn the event into flow

    // Chỗ return ra cái list - tổng hợp data từ searchQuery + data thì Jetpack datastore
    private val tasksFlow = combine(
        searchQuery.asFlow(), preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun getTasks() {
        viewModelScope.launch {
            val taskDataSource = TaskDataSource()
            taskDataSource.getTasks().collect { response ->
                for (item in response) {
                    println("=======item $item")
                }
                println("======== $response")
            }

        }
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedSelected(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: TaskModel) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChange(task: TaskModel, isChecked: Boolean) = viewModelScope.launch {
        val newTask = task.copy(completed = isChecked)
        reference.child(task.id).setValue(newTask)
    }

    fun onTaskSwiped(task: TaskModel, checkEmpty: (emptyCondition: Int) -> Unit) =
        viewModelScope.launch {
            reference.child(task.id).removeValue()
            tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
        }

    fun onUndoDeleteClick(task: TaskModel, checkEmpty: (emptyCondition: Int) -> Unit) =
        viewModelScope.launch {
            reference.child(task.id).setValue(task)
        }

    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> {
                showTaskSavedConfirmationMessage("Task added")
            }
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")

        }
    }

    fun onDeleteAllCompleteClick() = viewModelScope.launch {

        tasksEventChannel.send(TasksEvent.ShowDeleteAllCompletedDialog)
    }

    fun showSignOutConfirmDialog() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowSignOutConfirmDialog)
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }

    sealed class TasksEvent {
        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task: TaskModel) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: TaskModel) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TasksEvent()
        object ShowDeleteAllCompletedDialog : TasksEvent()
        object ShowSignOutConfirmDialog : TasksEvent()
    }
}

