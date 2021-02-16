package com.example.akshat1.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akshat1.R
import com.example.akshat1.adapters.SlotAdapter
import com.example.akshat1.databinding.DialogDeleteSlotsBinding


class DialogSlot : DialogFragment() {

    private lateinit var slotAdapter: SlotAdapter
    private lateinit var binding: DialogDeleteSlotsBinding

    companion object {

        const val TAG = "DialogSlot"

        var slotList = listOf<Map<String, Any>>()

        fun newInstance(_slotList: List<Map<String, Any>>): DialogSlot {
            slotList = _slotList
            val fragment = DialogSlot()
            return fragment
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

        setupSlotRecyclerView()

        setupClickListeners(view)

        slotAdapter.setOnItemClickListener {
            Log.d("final",it.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )
    }


    private fun setupClickListeners(view: View) {
//        view.btnPositive.setOnClickListener {
//            // TODO: Do some task here
//            dismiss()
//        }
//        view.btnNegative.
//        setOnClickListener {
//            // TODO: Do some task here
//            dismiss()
//        }
    }

    private fun setupSlotRecyclerView(){
        slotAdapter = SlotAdapter(slotList)
        binding.rvDialog.apply {
            adapter = slotAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)

        }
    }
}