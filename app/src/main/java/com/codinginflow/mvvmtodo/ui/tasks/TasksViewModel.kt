package com.codinginflow.mvvmtodo.ui.tasks

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.home.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.home.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
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


    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow() // turn the event into flow

    private val tasksFlow = combine(
        searchQuery.asFlow(), preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedSelected(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    // update is a suspend function so we need coroutines
    fun onTaskCheckedChange(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    // handle swipe task fragment
    fun onTaskSwiped(task: Task, checkEmpty: (emptyCondition: Int) -> Unit) =
        viewModelScope.launch {

            taskDao.delete(task)

            // CHỖ NÀY NÊN SHOW SNACK BAR SAU KHI DELETE
            // ViewModel không nên liên kết tới fragment/activity vì có khả năng dẫn tới memory leak
            // Vậy nên thay vì gọi tới fragment, thì mình dispatch 1 cái event để show snackbar
            tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))

            checkEmpty((tasks.value?.count()?.minus(1)) ?: 0)
        }

    fun onUndoDeleteClick(task: Task, checkEmpty: (emptyCondition: Int) -> Unit) = viewModelScope.launch {
        taskDao.insert(task)
        checkEmpty(tasks.value?.count()?.plus(1) ?: 0)
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

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }

    sealed class TasksEvent {
        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TasksEvent()
        object ShowDeleteAllCompletedDialog : TasksEvent()
    }
}

