package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View

import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.databinding.FragmentTasksBinding
import com.codinginflow.mvvmtodo.util.exhaustive
import com.codinginflow.mvvmtodo.util.onQueryTextChanged

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tasks.*

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "TasksFragment"

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TasksAdapter.OnItemClickListener {

    private val viewModel: TasksViewModel by viewModels()

    private lateinit var searchView: SearchView

    private lateinit var auth: FirebaseAuth


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter =
            TasksAdapter(this) // Pass the listener to the adapter (which is the fragment itself because it implemented the interface


        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter


                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }


            // IMPORTANT!: Handle Swipe action of Task Fragment
            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove( // onMove is go up and down .. drag & drop
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task, ::checkEmpty)


                }
            }).attachToRecyclerView(recyclerViewTasks)

            fabAddTask.setOnClickListener {
                viewModel.onAddNewTaskClick()
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->

            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
            checkEmpty(it.count() ?: 0)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TasksViewModel.TasksEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.task, ::checkEmpty)
                            }
                            .show()
                    }
                    // Navigation component cần phải generate code cho navigation event. Vì vậy sau khi tạo navigation event thì phải compile project (rebuild)
                    is TasksViewModel.TasksEvent.NavigateToAddTaskScreen -> {

                        val action =
                            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                                null,
                                "New Task"
                            )
                        findNavController().navigate(action)

                        // hoặc có thể viết findNavController().navigate(R.id.taskFragment) cho gọn nhưng nên xài action
                    }
                    is TasksViewModel.TasksEvent.NavigateToEditTaskScreen -> {

                        val action =
                            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                                event.task,
                                "Edit Task"
                            )
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    TasksViewModel.TasksEvent.ShowDeleteAllCompletedDialog -> {
                        val action =
                            TasksFragmentDirections.actionGlobalDeleteAllCompletedDialogFragment()
                        findNavController().navigate(action)
                    }
                    TasksViewModel.TasksEvent.ShowSignOutConfirmDialog -> {

                        val action =
                            TasksFragmentDirections.actionGlobalSignOutConfirmDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)


    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChange(task, isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView // turn this into property

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        // to read something from the Flow we have to launch the coroutines scope
        viewLifecycleOwner.lifecycleScope.launch {

            menu.findItem(R.id.action_hide_completed_task).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {

                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created -> {

                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed_task -> {

                item.isChecked = !item.isChecked
                viewModel.onHideCompletedSelected(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {

                viewModel.onDeleteAllCompleteClick()
                true
            }

            R.id.action_sign_out -> {

                viewModel.showSignOutConfirmDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::searchView.isInitialized) {
            searchView.setOnQueryTextListener(null)
        }

    }

    fun checkEmpty(emptyCondition: Int) {

//        var emptyCondition = viewModel.tasks.value?.count() ?: 0

        Log.v(TAG, "Viewmodel tasks $emptyCondition");

        empty_view.visibility = (if (emptyCondition == 0) View.VISIBLE else View.GONE)
        recycler_view_tasks.visibility =
            (if (emptyCondition == 0) View.GONE else View.VISIBLE)
    }

}