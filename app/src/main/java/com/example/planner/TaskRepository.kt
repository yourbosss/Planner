package com.example.planner

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    suspend fun getTasksById(): List<Task?>? {
        return taskDao.getTasksById()
    }

    suspend fun getTasksWhereCategory(category: String): List<Task?>? {
        return taskDao.getTasksWhereCategory(category)
    }

    suspend fun getTasksByDeadline1(): List<Task?>? {
        return taskDao.getTasksByDeadline1()
    }

    suspend fun getTasksByDeadline0(): List<Task?>? {
        return taskDao.getTasksByDeadline0()
    }

    suspend fun getTasksByPriority1(): List<Task?>? {
        return taskDao.getTasksByPriority1()
    }

    suspend fun getTasksByPriority0(): List<Task?>? {
        return taskDao.getTasksByPriority0()
    }

    suspend fun getTasksWhereStatus(status: Boolean): List<Task?>? {
        return taskDao.getTasksWhereStatus(status)
    }

}