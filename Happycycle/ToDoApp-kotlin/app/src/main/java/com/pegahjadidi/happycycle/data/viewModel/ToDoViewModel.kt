package com.pegahjadidi.happycycle.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pegahjadidi.happycycle.data.model.ToDoData
import com.pegahjadidi.happycycle.data.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToDoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ToDoRepository = ToDoRepository(application)

    private val _allData = MutableLiveData<List<ToDoData>>()
    val allData: LiveData<List<ToDoData>> get() = _allData

    private val _sortByHighPriority = MutableLiveData<List<ToDoData>>()
    val sortByHighPriority: LiveData<List<ToDoData>> get() = _sortByHighPriority

    private val _sortByLowPriority = MutableLiveData<List<ToDoData>>()
    val sortByLowPriority: LiveData<List<ToDoData>> get() = _sortByLowPriority

    init {
        refreshData()
    }

    fun insertData(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveToDoData(toDoData)
            refreshData() // Refresh the data to reflect the new task
        }
    }

    fun updateData(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveToDoData(toDoData)
            refreshData()
        }
    }

    fun deleteData(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteToDoData(toDoData)
            refreshData()
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllData()
            refreshData()
        }
    }

    fun searchDataBase(searchQuery: String): LiveData<List<ToDoData>> {
        val searchResults = MutableLiveData<List<ToDoData>>()
        viewModelScope.launch(Dispatchers.IO) {
            val results = repository.searchDataBase(searchQuery)
            searchResults.postValue(results)
        }
        return searchResults
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            val allTasks = repository.getAllData()
            _allData.postValue(allTasks)

            val highPriorityTasks = repository.sortByHighPriority()
            _sortByHighPriority.postValue(highPriorityTasks)

            val lowPriorityTasks = repository.sortByLowPriority()
            _sortByLowPriority.postValue(lowPriorityTasks)
        }
    }
}
