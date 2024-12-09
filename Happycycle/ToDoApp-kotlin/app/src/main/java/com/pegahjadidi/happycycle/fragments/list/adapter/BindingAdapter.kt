package com.pegahjadidi.happycycle.fragments.list.adapter

import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pegahjadidi.happycycle.R
import com.pegahjadidi.happycycle.data.model.Priority
import com.pegahjadidi.happycycle.data.model.ToDoData
import com.pegahjadidi.happycycle.data.viewModel.ToDoViewModel
import com.pegahjadidi.happycycle.fragments.list.ListFragmentDirections
import java.text.DateFormat

object BindingAdapter {

    @BindingAdapter("android:navigateToAddFragment")
    @JvmStatic
    fun navigateToAddFragment(view: FloatingActionButton, navigate: Boolean) {
        view.setOnClickListener {
            if (navigate) {
                view.findNavController().navigate(R.id.action_listFragment_to_addFragment)
            }
        }
    }


    @BindingAdapter("android:emptyDataBase")
    @JvmStatic
    fun emptyDataBase(view: View, emptyDataBase: MutableLiveData<Boolean>?) {
        when (emptyDataBase?.value) {
            true -> view.visibility = View.VISIBLE
            false -> view.visibility = View.INVISIBLE
            else -> {}
        }
    }

    @BindingAdapter("android:parsePriorityColor")
    @JvmStatic
    fun parsePriorityColor(cardView: CardView, priority: Priority) {
        when (priority) {
            Priority.HIGH -> cardView.setCardBackgroundColor(cardView.context.getColor(R.color.red))
            Priority.MEDIUM -> cardView.setCardBackgroundColor(cardView.context.getColor(R.color.yellow))
            Priority.LOW -> cardView.setCardBackgroundColor(cardView.context.getColor(R.color.green))
        }
    }

    @BindingAdapter("android:sendDataToUpdateFragment")
    @JvmStatic
    fun sendDataToUpdateFragment(view: ConstraintLayout, currentItem: ToDoData) {
        view.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            view.findNavController().navigate(action)
        }
    }

    @BindingAdapter("android:setTimeStamp")
    @JvmStatic
    fun setTimeStamp(view: TextView, timeStamp: Long) {
        view.text = DateFormat.getInstance().format(timeStamp)
    }

    @BindingAdapter(value = ["todo", "vm"])
    @JvmStatic
    fun isChecking(checkBox: CheckBox, todo: ToDoData, viewModel: ToDoViewModel) {
        checkBox.isChecked = todo.completed

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            val updatedToDo = todo.copy(completed = isChecked, timeStamp = System.currentTimeMillis())
            viewModel.updateData(updatedToDo)
        }
    }

    @BindingAdapter("android:strikeThrough")
    @JvmStatic
    fun striked(textView: TextView, isChecked: Boolean) {
        textView.paintFlags = if (isChecked) {
            textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}
