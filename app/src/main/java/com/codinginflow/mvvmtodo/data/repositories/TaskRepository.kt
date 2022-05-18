package com.codinginflow.mvvmtodo.data.repositories

import com.codinginflow.mvvmtodo.data.realtimedata.TaskModel
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<TaskModel>>
}