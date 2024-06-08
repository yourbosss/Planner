package com.example.planner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _allTasks: MutableLiveData<List<Task>> = MutableLiveData()
    val allTasks: LiveData<List<Task>> get() = _allTasks

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.allTasks.asFlow().collect {
                _allTasks.postValue(it)
            }
        }
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    suspend fun getTasksById(): List<Task?>? {
        return repository.getTasksById()
    }

    suspend fun getTasksWhereCategory(category: String): List<Task?>? {
        return repository.getTasksWhereCategory(category)
    }

    suspend fun getTasksByDeadline1(): List<Task?>? {
        return repository.getTasksByDeadline1()
    }

    suspend fun getTasksByDeadline0(): List<Task?>? {
        return repository.getTasksByDeadline0()
    }

    suspend fun getTasksByPriority1(): List<Task?>? {
        return repository.getTasksByPriority1()
    }

    suspend fun getTasksByPriority0(): List<Task?>? {
        return repository.getTasksByPriority0()
    }

    suspend fun getTasksWhereStatus(status: Boolean): List<Task?>? {
        return repository.getTasksWhereStatus(status)
    }
}

class TaskViewModelFactory(private val mParam: TaskRepository) :
// позволяет инъектировать зависимости, необходимые для TaskViewModel, например, TaskRepository.
// Это делает ViewModel более гибкой и тестируемой, так как она не зависит от конкретной реализации зависимостей. (архитекрута mvvm)
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return TaskViewModel(mParam) as T
    }

}
