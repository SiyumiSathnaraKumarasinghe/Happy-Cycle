package com.pegahjadidi.happycycle.fragments.list

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pegahjadidi.happycycle.R
import com.pegahjadidi.happycycle.data.model.ToDoData
import com.pegahjadidi.happycycle.data.viewModel.SharedViewModel
import com.pegahjadidi.happycycle.data.viewModel.ToDoViewModel
import com.pegahjadidi.happycycle.databinding.FragmentListBinding
import com.pegahjadidi.happycycle.fragments.list.adapter.ListAdapter
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ListFragment : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var mToDoViewModel: ToDoViewModel
    private lateinit var mSharedViewModel: SharedViewModel
    private val adapter: ListAdapter by lazy { ListAdapter(mToDoViewModel) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        mToDoViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        mSharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setupRecyclerView()
        observeLiveData()
        setupMenu()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply { addDuration = 300 }
        swipeToDelete(binding.recyclerView)
    }

    private fun observeLiveData() {
        mToDoViewModel.allData.observe(viewLifecycleOwner) { data ->
            adapter.setData(data) // Update the adapter's data
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)
                val search = menu.findItem(R.id.menu_search)
                val searchView = search?.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_deleteAll -> confirmRemoval()
                    R.id.menu_priority_high -> mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner) {
                        adapter.setData(it)
                    }
                    R.id.menu_priority_low -> mToDoViewModel.sortByLowPriority.observe(viewLifecycleOwner) {
                        adapter.setData(it)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = adapter.getToDoDataLists()[viewHolder.adapterPosition]
                mToDoViewModel.deleteData(itemToDelete)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                restoreDeletedItem(viewHolder.itemView, itemToDelete)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedItem(view: View, deletedItem: ToDoData) {
        val snackBar = Snackbar.make(view, "Deleted '${deletedItem.title}'", Snackbar.LENGTH_LONG)
        snackBar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
        }
        snackBar.show()
    }

    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(R.string.yes) { _, _ ->
            mToDoViewModel.deleteAllData()
            Toast.makeText(requireContext(), R.string.successfully_removed_all, Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(R.string.no, null)
        builder.setTitle(R.string.delete_everything)
        builder.setMessage(R.string.are_you_sure_you_want_to_delete_everything)
        builder.create().show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) searchThroughDB(query)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) searchThroughDB(query)
        return true
    }

    private fun searchThroughDB(query: String) {
        val searchQuery = "%$query%"
        mToDoViewModel.searchDataBase(searchQuery).observe(viewLifecycleOwner) { list ->
            list?.let { adapter.setData(it) }
        }
    }


    override fun onResume() {
        super.onResume()
        mToDoViewModel.refreshData() // Ensure the data is refreshed when returning to this fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
