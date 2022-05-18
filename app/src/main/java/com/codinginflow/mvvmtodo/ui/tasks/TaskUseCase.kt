package com.codinginflow.mvvmtodo.ui.tasks

import android.util.Log
import com.codinginflow.mvvmtodo.data.realtimedata.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.flow


class TaskUseCase {
    val auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val userId = user?.uid ?: ""
    private val reference = Firebase.database.reference.child("tasks").child(userId)

    fun getTasks() = flow<List<TaskModel>> {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val tasks = dataSnapshot.getValue()
                println("TaskUseCase: $tasks")
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TaskUseCase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        reference.addValueEventListener(postListener)
    }
}