package com.pegahjadidi.happycycle.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pegahjadidi.happycycle.data.model.ToDoData
import com.pegahjadidi.happycycle.data.model.Priority

class ToDoRepository(context: Context) {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getAllData(): List<ToDoData> {
        val dataListJson = sharedPrefs.getString("todo_list", null)
        return if (dataListJson.isNullOrEmpty()) emptyList()
        else gson.fromJson(dataListJson, object : TypeToken<List<ToDoData>>() {}.type)
    }

    fun saveToDoData(toDoData: ToDoData) {
        val dataList = getAllData().toMutableList()
        val index = dataList.indexOfFirst { it.id == toDoData.id }
        if (index != -1) {
            dataList[index] = toDoData
        } else {
            val newId = if (dataList.isNotEmpty()) dataList.maxOf { it.id } + 1 else 1
            dataList.add(toDoData.copy(id = newId))
        }
        saveDataList(dataList)
    }

    fun deleteToDoData(toDoData: ToDoData) {
        val dataList = getAllData().toMutableList()
        dataList.removeAll { it.id == toDoData.id }
        saveDataList(dataList)
    }

    fun clearAllData() {
        saveDataList(emptyList())
    }

    fun searchDataBase(searchQuery: String): List<ToDoData> {
        return getAllData().filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    fun sortByHighPriority(): List<ToDoData> = getAllData().sortedByDescending { it.priority }

    fun sortByLowPriority(): List<ToDoData> = getAllData().sortedBy { it.priority }

    private fun saveDataList(dataList: List<ToDoData>) {
        sharedPrefs.edit().putString("todo_list", gson.toJson(dataList)).apply()
    }
}

