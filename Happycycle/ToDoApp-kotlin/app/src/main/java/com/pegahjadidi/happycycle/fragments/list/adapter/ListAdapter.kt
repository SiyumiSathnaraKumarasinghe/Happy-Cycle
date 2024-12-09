package com.pegahjadidi.happycycle.fragments.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pegahjadidi.happycycle.R
import com.pegahjadidi.happycycle.data.model.ToDoData
import com.pegahjadidi.happycycle.data.viewModel.ToDoViewModel
import com.pegahjadidi.happycycle.databinding.RowLayoutBinding

class ListAdapter(private val viewModel: ToDoViewModel) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private var todoDataLists: List<ToDoData> = emptyList()

    class ListViewHolder(private val binding: RowLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(toDoData: ToDoData, viewModel: ToDoViewModel) {
            binding.toDoData = toDoData
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ListViewHolder {
                val view: RowLayoutBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_layout,
                    parent,
                    false
                )
                return ListViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = todoDataLists[position]
        holder.bind(currentItem, viewModel)
    }

    override fun getItemCount(): Int {
        return todoDataLists.size
    }


    fun setData(newToDoData: List<ToDoData>) {
        val diffUtilCallback = ToDoDiffUtil(todoDataLists, newToDoData)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        todoDataLists = newToDoData
        diffResult.dispatchUpdatesTo(this) // Efficiently update the RecyclerView
    }


    // Add this getter method
    fun getToDoDataLists(): List<ToDoData> {
        return todoDataLists
    }
}
