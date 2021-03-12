package com.example.akshat1.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.akshat1.R
import com.example.akshat1.databinding.FragmentAddSlotBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.EnumSet.range

class AddSlot : Fragment() {

    private lateinit var binding: FragmentAddSlotBinding
    private lateinit var fireStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_slot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentAddSlotBinding.bind(view)
        fireStore = FirebaseFirestore.getInstance()
        var date = ""
        var month = ""
        var year = ""
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        binding.calendarView.setOnDateChangeListener { view, yearView, monthView, dayOfMonth ->
            date = dayOfMonth.toString()
            if (monthView > 8) {
                month = (monthView + 1).toString()
            } else {
                month = "0" + (monthView + 1).toString()
            }

            year = yearView.toString()
        }




        binding.openBookingb.setOnClickListener {

            if(date.isEmpty()){
                Toast.makeText(requireContext(),"Pick a date",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateFormat = SimpleDateFormat("yyyy:MM:dd")
            val currDate = dateFormat.format(Date()).toString().replace(":","").toInt()
            val selectedDate = (year+month+date).toInt()
            if(currDate <= selectedDate
            //Internet Connection
            ) {
                try {
                    openBooking(selectedDate)
                }catch (e : Exception){
                    Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
                }
            }
        }

    }


    private fun openBooking(date : Int){

        var clients = mutableListOf<String>()
        val from : Int = binding.evFrom.text.split(":")[0].toInt()
        val to : Int = binding.evTo.text.split(":")[0].toInt()

        var slot = mutableMapOf<String, Any>(
                "date" to date
        )

        if(binding.evFrom.text.isEmpty()
            || binding.evTo.text.isEmpty()
            || to < from){
            return
        }


        for(i in from until to) {
            var date = i.toString()
            slot[date] = clients
        }



        fireStore.collection("dates").document(date.toString()).set(slot)
                .addOnFailureListener {
                    Toast.makeText(requireActivity(),"Unknown error occured",Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Toast.makeText(requireActivity(),"Success",Toast.LENGTH_SHORT).show()
                }
    }

}

