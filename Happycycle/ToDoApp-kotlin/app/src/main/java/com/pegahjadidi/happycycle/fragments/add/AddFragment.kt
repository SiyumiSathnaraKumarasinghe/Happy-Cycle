package com.pegahjadidi.happycycle.fragments.add

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.pegahjadidi.happycycle.AlarmManager.AlarmReceiver
import com.pegahjadidi.happycycle.MainActivity
import com.pegahjadidi.happycycle.R
import com.pegahjadidi.happycycle.data.model.ToDoData
import com.pegahjadidi.happycycle.data.viewModel.SharedViewModel
import com.pegahjadidi.happycycle.data.viewModel.ToDoViewModel
import com.pegahjadidi.happycycle.databinding.FragmentAddBinding
import java.util.*
import androidx.navigation.fragment.findNavController


class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private lateinit var mToDoViewModel: ToDoViewModel
    private lateinit var mSharedViewModel: SharedViewModel

    private var selectedDateTime: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        val view = binding.root

        // Setting up the toolbar
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.fragment_add_title)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ViewModel initialization
        mToDoViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        mSharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Spinner Listener for priority
        binding.prioritySpinner.onItemSelectedListener = mSharedViewModel.listener

        // Set Reminder Button Listener
        binding.setReminderBtn.setOnClickListener { showDatePickerDialog() }

        binding.startStopwatchBtn.setOnClickListener { openStopwatchFragment() }
        // Menu setup
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_add -> insertDataToDB()
                    android.R.id.home -> findNavController().navigateUp() // Use navigateUp to go back
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return view
    }

    private fun insertDataToDB() {
        val mTitle = binding.titleEt.text.toString()
        val mPriority = binding.prioritySpinner.selectedItem.toString()
        val mDescription = binding.descriptionEt.text.toString()
        val validation = mSharedViewModel.verifyDataFromUser(mTitle, mDescription)

        if (validation) {
            val newToDoData = ToDoData(
                0,
                mTitle,
                mSharedViewModel.parsePriority(mPriority),
                mDescription,
                selectedDateTime.timeInMillis,
                false
            )
            mToDoViewModel.insertData(newToDoData)

            // Schedule the alarm for the reminder
            scheduleNotification(newToDoData)

            Toast.makeText(requireContext(), R.string.successfully_added, Toast.LENGTH_SHORT).show()

            // Navigate to MainActivity
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()  // Finish the current activity to avoid back stack issues
        } else {
            Toast.makeText(requireContext(), R.string.please_fill_out_all_fields, Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleNotification(toDoData: ToDoData) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("title", toDoData.title)
            putExtra("description", toDoData.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            toDoData.id,  // Unique id for each notification
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Use setExactAndAllowWhileIdle to trigger the alarm more accurately
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            toDoData.timeStamp,  // Use the reminder timestamp
            pendingIntent
        )
    }
    private fun openStopwatchFragment() {
        // Navigate to the StopwatchFragment
        findNavController().navigate(R.id.stopwatchFragment)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
            selectedDateTime.set(Calendar.YEAR, year)
            selectedDateTime.set(Calendar.MONTH, monthOfYear)
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            showTimePickerDialog()
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val hour = selectedDateTime.get(Calendar.HOUR_OF_DAY)
        val minute = selectedDateTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minuteOfHour ->
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedDateTime.set(Calendar.MINUTE, minuteOfHour)

            val formattedDate = "${selectedDateTime.get(Calendar.DAY_OF_MONTH)}/" +
                    "${selectedDateTime.get(Calendar.MONTH) + 1}/" +
                    "${selectedDateTime.get(Calendar.YEAR)} " +
                    "${String.format("%02d", hourOfDay)}:${String.format("%02d", minuteOfHour)}"

            binding.reminderText.text = getString(R.string.reminder_set_for, formattedDate)
        }, hour, minute, true)

        timePickerDialog.show()
    }
}
