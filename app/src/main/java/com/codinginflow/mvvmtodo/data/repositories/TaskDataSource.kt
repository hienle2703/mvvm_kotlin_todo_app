package com.codinginflow.mvvmtodo.data.repositories

import android.util.Log
import com.codinginflow.mvvmtodo.data.realtimedata.TaskModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TaskDataSource : TaskRepository {
    val auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val userId = user?.uid ?: ""
    private val reference = Firebase.database.reference.child("tasks").child(userId)

    override fun getTasks(): Flow<List<TaskModel>> = callbackFlow {

        val snapShowListener = reference.get().addOnCompleteListener { snapshot ->
            var list: List<TaskModel> = emptyList()
            if (snapshot.isSuccessful) {
//                list.clear()
                for (item in snapshot.result.children) {
                    val task = item.getValue(TaskModel::class.java)
//                    list.add(task)
                }
            }
//            val response: List<TaskModel> = snapshot
//
//            trySend(response).isSuccess
//            println("==========response $response")
        }
        awaitClose {
            println("========== ${snapShowListener.result}")
            println("Closed!")
        }
    }
}
