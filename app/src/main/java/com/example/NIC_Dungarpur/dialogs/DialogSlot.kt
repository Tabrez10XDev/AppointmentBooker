package com.example.NIC_Dungarpur.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.NIC_Dungarpur.R
import com.example.NIC_Dungarpur.adapters.SlotAdapter
import com.example.NIC_Dungarpur.databinding.DialogDeleteSlotsBinding
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

        fun newInstance(_slotList: List<Map<String, Any>>,
                        _selectedDate : String,
                        _dateMap : Map<String, Any>
        ): DialogSlot {
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

        slotList?.let {
            slotAdapter.loadList = slotList!!
        }

        slotAdapter.setOnItemClickListener {slots, i->

            val nameMap = slots.filter { (key, value) -> !key.endsWith("uid")}
            val uidMap = slots.filter { (key, value) -> key.endsWith("uid")}
            val key = nameMap.keys.toString()
            val uid = uidMap.values.toString().split(",")[i].trim()
            Log.d("weed",uid)
            val bundle = Bundle()
            val formDocID = dateMap[(key+uid).replaceBrackets()+"Ref"].toString()

            bundle.apply {
                putString("uid",uid.replaceBrackets())
                putString("formDocID",formDocID)
                putString("selectedDate", selectedDate)
                putString("slotKey",key.replaceBrackets())
            }
            findNavController().navigate(R.id.action_slotView_to_viewDetailsForm2,bundle)

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
        slotAdapter = SlotAdapter(mutableListOf())
        binding.rvDialog.apply {
            adapter = slotAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)

        }
    }

    override fun onResume() {
        firestore.collection("dates").document(selectedDate).get().addOnSuccessListener {slots->
            val data = slots.data
            data?.let {
            if(dateMap != data.toMap()){
                dialog?.dismiss()
            }

        }
        }

        super.onResume()
    }





    private fun String.replaceBrackets(): String {
        return replace("[","").replace("]","")
    }

}