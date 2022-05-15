package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.view.*

import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
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
import com.codinginflow.mvvmtodo.data.realtimedata.TaskModel
import com.codinginflow.mvvmtodo.databinding.FragmentTasksBinding
import com.codinginflow.mvvmtodo.databinding.ItemTaskBinding
import com.codinginflow.mvvmtodo.util.exhaustive
import com.codinginflow.mvvmtodo.util.onQueryTextChanged
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tasks.*

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "TasksFragment"

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    val viewModel: TasksViewModel by viewModels()

    private lateinit var searchView: SearchView

    val auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val userId = user?.uid ?: ""
    private val reference = FirebaseDatabase.getInstance().reference.child("tasks").child(userId)


    val options: FirebaseRecyclerOptions<TaskModel> =
        FirebaseRecyclerOptions.Builder<TaskModel>()
            .setQuery(reference, TaskModel::class.java)
            .build()

    val adapterFirebase =
        object : FirebaseRecyclerAdapter<TaskModel, TasksViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
                val binding: ItemTaskBinding = ItemTaskBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return TasksViewHolder(binding)
            }

            override fun onBindViewHolder(
                holder: TasksViewHolder,
                position: Int,
                model: TaskModel
            ) {
                val currentItem = getItem(position)
                holder.bind(currentItem)
            }
        }

    override fun onStart() {
        super.onStart()
        adapterFirebase.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterFirebase.stopListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)


        // Pass the listener to the adapter (which is the fragment itself because it implemented the interface
//        val taskAdapter = TasksAdapter(this)


        binding.apply {
            recyclerViewTasks.apply {
                adapter = adapterFirebase

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
                    val task = adapterFirebase.getItem(viewHolder.adapterPosition)
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

//        viewModel.tasks.observe(viewLifecycleOwner) { taskList ->
//            adapterFirebase.submitList(taskList) // provide new data for the Adapter
//            checkEmpty(taskList.count() ?: 0)
//        }

        // GATHER & DISPATCH EVENTS FROM VIEWMODEL
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

        binding.recyclerViewTasks.adapter = adapterFirebase

    }

    fun onItemClick(task: TaskModel) {
        viewModel.onTaskSelected(task)
    }

    fun onCheckBoxClick(task: TaskModel, isChecked: Boolean) {
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

    inner class TasksViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {

                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = adapterFirebase.getItem(position)
                        onItemClick(task)
                    }
                }
                checkBoxCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = adapterFirebase.getItem(position)
                        onCheckBoxClick(task, checkBoxCompleted.isChecked)
                    }
                }

            }
        }

        fun bind(task: TaskModel) {
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }
    }

    fun checkEmpty(emptyCondition: Int) {

        empty_view.visibility = (if (emptyCondition == 0) View.VISIBLE else View.GONE)
        recycler_view_tasks.visibility =
            (if (emptyCondition == 0) View.GONE else View.VISIBLE)
    }

}