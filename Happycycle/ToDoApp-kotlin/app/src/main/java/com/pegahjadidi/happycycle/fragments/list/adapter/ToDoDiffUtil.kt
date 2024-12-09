package com.pegahjadidi.happycycle.fragments.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.pegahjadidi.happycycle.data.model.ToDoData

class ToDoDiffUtil(
    private val oldList: List<ToDoData>,
    private val newList: List<ToDoData>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare unique IDs
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Check whether the contents are the same
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
