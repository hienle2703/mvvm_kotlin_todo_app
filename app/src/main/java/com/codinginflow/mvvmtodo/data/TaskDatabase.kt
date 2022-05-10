package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) :
        RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

//            val dao = database.get().taskDao()
//
//            applicationScope.launch {
//                dao.insert(Task("Wash the dishes"))
//                dao.insert(Task("Call momma", important = true))
//                dao.insert(Task("Fix my laptop", completed = true, created = 1650706547368))
//                dao.insert(Task("Buh Buh Lmao", completed = true, created = 1650706547268))
//                dao.insert(Task("Buy a Macbook", important = true))
//                dao.insert(Task("Go to bed early", important = true, created = 1650706547168))
//            }
        }
    }
}