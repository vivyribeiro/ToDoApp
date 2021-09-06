package com.vivysr.todoapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.vivysr.todoapp.databinding.ActivityMainBinding
import com.vivysr.todoapp.datasource.TaskDataSource

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { TaskListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTasks.adapter = adapter
        updateList()

        insertListeners()

    }

    private fun insertListeners() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            resultLauncher.launch(intent)
        }

        editItem()
        deletItem()

    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            insertListeners()
            updateList()
        }
    }

    private fun updateList(){
        val list = TaskDataSource.getList()
        binding.includeEmpty.emptyState.visibility = if (list.isEmpty()) View.VISIBLE
        else View.GONE
        adapter.submitList(list)
    }

    fun editItem() {
        adapter.listenerEdit = {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(AddTaskActivity.TASK_ID, it.id)
            resultLauncher.launch(intent)
        }
    }

    fun deletItem(){
        adapter.listenerDelete = {
            TaskDataSource.deleteTask(it)
            updateList()
        }
    }

}