package com.example.planner

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(),
    RVAdapter.TaskClickDeleteInterface,
    RVAdapter.TaskClickInterface,
    RVAdapter.TaskStatusChangeInterface {
    lateinit var tasksRV: RecyclerView //отображения интерфейса.
    lateinit var addFAB: FloatingActionButton //добавление новых задач.
    lateinit var viewModel: TaskViewModel //управление задачами.
    lateinit var filterCategories: Spinner //фильтрация
    lateinit var filterDone: Spinner
    lateinit var sort: Spinner
    lateinit var repository: TaskRepository //для раюоты с даннымми.

    private var sortArray: Array<String> = arrayOf<String>(
        "По id",
        "По приближению дедлайна",
        "По отдалению дедлайна",
        "По возрастанию важности",
        "По убыванию важности"
    )
    private var doneArray: Array<String> = //массив строк для необходимой фильтрации.
        arrayOf<String>("Не выбрано", "Не выполненные", "Выполненные")
    private var categoriesArray: Array<String> =
        arrayOf<String>("Не выбрано", "Дом", "Работа", "Спорт", "Учеба", "Хобби", "Семья", "Друзья")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tasksRV = findViewById(R.id.tasksRV) //отображения списка задач.
        addFAB = findViewById(R.id.add) //добавление кнопка.
        filterCategories = findViewById(R.id.spinnerCategories)
        filterDone = findViewById(R.id.spinnerDone)
        sort = findViewById(R.id.spinnerSort)

        tasksRV.layoutManager = LinearLayoutManager(this) //линейный порядок отображения.

        val taskRVAdapter = RVAdapter(this, this, this, this)
        //текущаая активность будет создаваться, эта активность будет использована для обработки кнопки и удаления задачи.

        tasksRV.adapter = taskRVAdapter //устанавливаем адаптер.

        val database =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "task_database")
                .build()
        repository = TaskRepository(database.taskDao())

        viewModel =
            ViewModelProvider(this, TaskViewModelFactory(repository))[TaskViewModel::class.java]

        viewModel.allTasks.observe(this, Observer { list ->
            list?.let {
                taskRVAdapter.updateList(it)
            }
        })

        val doneArrayAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doneArray)
        doneArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterDone.adapter = doneArrayAdapter

        filterDone.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val chosen = parent.getItemAtPosition(position).toString()

                lifecycleScope.launchWhenCreated {
                    if (chosen == "Не выполненные") {
                        viewModel.getTasksWhereStatus(false)
                    }
                    if (chosen == "Выполненные") {
                        viewModel.getTasksWhereStatus(true)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        val categoriesArrayAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesArray)
        categoriesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterCategories.adapter = categoriesArrayAdapter

        val sortArrayAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortArray)
        sortArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sort.adapter = sortArrayAdapter

        sort.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                lifecycleScope.launchWhenCreated {
                    val chosen = parent.getItemAtPosition(position).toString()
                    if (chosen == "По id") {
                        viewModel.getTasksById()
                    }
                    if (chosen == "По приближению дедлайна") {
                        viewModel.getTasksByDeadline0()
                    }
                    if (chosen == "По отдалению дедлайна") {
                        viewModel.getTasksByDeadline1()
                    }
                    if (chosen == "По возрастанию важности") {
                        viewModel.getTasksByPriority0()
                    }
                    if (chosen == "По убыванию важности") {
                        viewModel.getTasksByPriority1()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        addFAB.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onTaskClick(task: Task) {
        val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
        intent.putExtra("taskType", "Edit")
        intent.putExtra("taskTitle", task.title)
        intent.putExtra("taskDescription", task.description)
        intent.putExtra("taskDeadline", task.deadline)
        intent.putExtra("notePriority", task.priority)
        intent.putExtra("taskCategory", task.category)
        intent.putExtra("taskDone", task.done)
        intent.putExtra("noteId", task.id)
        startActivity(intent)
        this.finish()
    }

    override fun onDeleteIconClick(task: Task) {
        viewModel.delete(task)
    }

    override fun onTaskStatusChange(task: Task, isChecked: Boolean) {
        task.done = isChecked
        viewModel.update(task)
    }

}
