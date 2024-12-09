package com.pegahjadidi.happycycle.fragments.update

import android.app.AlarmManager
import android.app.AlertDialog
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pegahjadidi.happycycle.AlarmManager.AlarmReceiver
import com.pegahjadidi.happycycle.MainActivity
import com.pegahjadidi.happycycle.R
import com.pegahjadidi.happycycle.data.model.ToDoData
import com.pegahjadidi.happycycle.data.viewModel.SharedViewModel
import com.pegahjadidi.happycycle.data.viewModel.ToDoViewModel
import com.pegahjadidi.happycycle.databinding.FragmentUpdateBinding
import java.util.*

class UpdateFragment : Fragment() {
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private lateinit var mToDoViewModel: ToDoViewModel
    private lateinit var mSharedViewModel: SharedViewModel
    private val args by navArgs<UpdateFragmentArgs>()
    private var selectedDateTime: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.fragment_update_title)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ViewModel initialization
        mToDoViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        mSharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Spinner Listener
        binding.updatePrioritySpinner.onItemSelectedListener = mSharedViewModel.listener

        // Prepopulate the fields using SafeArgs
        binding.updateTitleEt.setText(args.currentItem.title)
        binding.updateDescriptionEt.setText(args.currentItem.description)
        binding.updatePrioritySpinner.setSelection(mSharedViewModel.parsePriorityToInt(args.currentItem.priority))

        // Reminder button listener
        binding.updateSetReminderBtn.setOnClickListener { showDatePickerDialog() }

        // Menu setup
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_save -> updateItem()
                    R.id.menu_delete -> confirmItemRemoval()
                    android.R.id.home -> findNavController().navigateUp() // Navigate up instead of hardcoding the fragment destination
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return view
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

            binding.updateReminderText.text = getString(R.string.reminder_set_for, formattedDate)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun updateItem() {
        val updatedTitle = binding.updateTitleEt.text.toString()
        val updatedDescription = binding.updateDescriptionEt.text.toString()
        val updatedPriority = binding.updatePrioritySpinner.selectedItem.toString()
        val validation = mSharedViewModel.verifyDataFromUser(updatedTitle, updatedDescription)

        if (validation) {
            val updatedItem = ToDoData(
                args.currentItem.id,
                updatedTitle,
                mSharedViewModel.parsePriority(updatedPriority),
                updatedDescription,
                selectedDateTime.timeInMillis, // Update the reminder timestamp
                false
            )
            mToDoViewModel.updateData(updatedItem)
            scheduleNotification(updatedItem)

            Toast.makeText(requireContext(), R.string.successfully_updated, Toast.LENGTH_SHORT).show()

            // Use Intent to navigate back to MainActivity
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish() // End the current fragment's activity
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


    private fun confirmItemRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(R.string.yes) { _, _ ->
            mToDoViewModel.deleteData(args.currentItem)
            Toast.makeText(requireContext(), R.string.successfully_removed, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp() // Navigate back to MainActivity
        }
        builder.setNegativeButton(R.string.no, null)
        builder.setTitle(R.string.delete_the_task)
        builder.setMessage(R.string.are_you_sure_you_want_to_delete_this_task)
        builder.create().show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
