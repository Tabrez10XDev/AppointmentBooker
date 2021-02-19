package com.example.akshat1.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.akshat1.R
import com.example.akshat1.adapters.SlotAdapter
import com.example.akshat1.databinding.DialogDeleteSlotsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class DialogSlot : DialogFragment() {

    private lateinit var slotAdapter: SlotAdapter
    private lateinit var binding: DialogDeleteSlotsBinding
    private lateinit var firestore: FirebaseFirestore

    companion object {

        const val TAG = "DialogSlot"

        var slotList = listOf<Map<String, Any>>()
        var selectedDate = ""
        var dateMap = mapOf<String, Any>()

        fun newInstance(_slotList: List<Map<String, Any>>, _selectedDate : String, _dateMap : Map<String, Any>): DialogSlot {
            slotList = _slotList
            dateMap = _dateMap
            selectedDate = _selectedDate
            return DialogSlot()
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_delete_slots, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogDeleteSlotsBinding.bind(view)

        firestore = FirebaseFirestore.getInstance()

        setupSlotRecyclerView()

        slotAdapter.setOnItemClickListener {

        }

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//
//                val position = viewHolder.adapterPosition
//                val slot = slotAdapter.loadList[position]
//                Log.d("delete", selectedDate)
//                deleteSlot(slot)
//                Snackbar.make(requireView(),"Article Deleted",Snackbar.LENGTH_LONG).apply {
//                    setAction("Undo"){
//                 //       viewModel.saveArticle(article)
//                    }
//                }
            }

        }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
//            attachToRecyclerView(binding.rvDialog)
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )
    }




    private fun setupSlotRecyclerView(){
        slotAdapter = SlotAdapter(slotList)
        binding.rvDialog.apply {
            adapter = slotAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)

        }
    }

    private fun deleteSlot(slot : Map<String, Any>){
        firestore.collection("dates").document(selectedDate).update(
            slot.keys.toString().replaceBrackets(),FieldValue.arrayRemove(slot.values.toString().replaceBrackets())).addOnCanceledListener {
            Log.d("delete", "cancel")
        }.addOnSuccessListener {
            Log.d("delete", "success")

        }.addOnFailureListener{
            Log.d("delete", it.message.toString())
        }

    }

    private fun String.replaceBrackets(): String {
        return replace("[","").replace("]","")
    }

}