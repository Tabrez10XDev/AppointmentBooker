package com.example.akshat1.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.akshat1.R
import com.example.akshat1.databinding.FragmentAddSlotBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

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
            val dateFormat = SimpleDateFormat("yyyy:MM:dd")
            val currDate = dateFormat.format(Date()).toString().replace(":","").toInt()
            val selectedDate = (year+month+date).toInt()
            if(currDate <= selectedDate
            //Internet Connection
            ) {
                openBooking(selectedDate)
            }
        }

    }


    private fun openBooking(date : Int){

        var clients = mutableListOf<String>()

        val slot = hashMapOf(
                "date" to date,
                "8" to clients,
                "9" to clients,
                "10" to clients,
                "11" to clients,
                "12" to clients,
                "14" to clients,
                "15" to clients,
                "16" to clients,
                "17" to clients
        )


        fireStore.collection("dates").document(date.toString()).set(slot)
                .addOnFailureListener {
                    Toast.makeText(requireActivity(),"Unknown error occured",Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Toast.makeText(requireActivity(),"Success",Toast.LENGTH_SHORT).show()
                }
    }

}

