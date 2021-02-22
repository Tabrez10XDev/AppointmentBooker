package com.example.akshat1.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akshat1.R
import com.example.akshat1.adapters.DateAdapter
import com.example.akshat1.adapters.SlotAdapter
import com.example.akshat1.databinding.DashboardUserBinding
import com.example.akshat1.databinding.FragmentSlotViewUserBinding
import com.example.akshat1.util.BounceEdgeEffectFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class SlotViewUser : Fragment() {
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var slotAdapter: SlotAdapter
    private lateinit var dateAdapter : DateAdapter
    private lateinit var auth : FirebaseAuth
    private lateinit var binding : FragmentSlotViewUserBinding
    private lateinit var selectedDate : String
    private var dateMap = mapOf<String, Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_slot_view_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSlotViewUserBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        setupDateRecyclerView()
        setupSlotRecyclerView()

        subscribeToDates()

        dateAdapter.setOnItemClickListener {slots->
            selectedDate = slots["date"].toString()
            dateMap = slots
            val submitList = feedSlotDataset(slots)
            slotAdapter.loadList = submitList
            slotAdapter.notifyDataSetChanged()
        }

        slotAdapter.setOnItemClickListener {slot, _->
            val bundle = Bundle()
            val slotKey = slot.keys.toString().replaceBrackets()

            bundle.putString("selectedDate", selectedDate)
            bundle.putString("slotKey", slotKey.replaceBrackets())
            if(dateMap[slotKey].toString().contains(auth.uid.toString())){
                bundle.putString("uid",auth.uid)
                bundle.putString("formDocID", dateMap[slotKey+auth.uid.toString()+"Ref"].toString())
                  findNavController().navigate(R.id.action_slotViewUser_to_viewDetailsForm, bundle)
            }
            else{
                findNavController().navigate(R.id.action_slotViewUser_to_userDetailsForm, bundle)
            }

        }

    }




    private fun setupSlotRecyclerView(){
        slotAdapter = SlotAdapter(mutableListOf())
        binding.rvSlotUser.apply {
            adapter = slotAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)
            edgeEffectFactory = BounceEdgeEffectFactory()
        }
    }

    private fun setupDateRecyclerView(){
        dateAdapter = DateAdapter()
        binding.rvDateUser.apply {
            adapter = dateAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL,false)
            edgeEffectFactory = BounceEdgeEffectFactory()
        }
    }

    private fun subscribeToDates(){



        val dateFormat = SimpleDateFormat("yyyy:MM:dd")
        val currDate = dateFormat.format(Date()).toString().replace(":","").toInt()


        val fire = fireStore.collection("dates").whereGreaterThanOrEqualTo("date",currDate).orderBy("date", Query.Direction.ASCENDING)
        fire.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Log.d("Error-Dashboard",it.message.toString())

                return@addSnapshotListener
            }

            querySnapshot?.let {documents->
                val submitList : MutableList<Map<String,Any>> = arrayListOf()
                for(document in documents) {
                    submitList.add(document.data)
                }

                dateAdapter.differ.submitList(submitList)

            }
        }
    }



    private fun refreshSlotDataset(){
        fireStore.collection("dates").document(selectedDate).get().addOnSuccessListener {slots->
            val data = slots.data

            val submitList = feedSlotDataset(data!!.toMap())
            slotAdapter.loadList = submitList
            slotAdapter.notifyDataSetChanged()
        }
    }


    private fun feedSlotDataset(slots : Map<String, Any>): List<Map<String, Any>>{

        val submitList =  mutableListOf<Map<String, Any>>()

        for(i in 0..24){
            if(slots[i.toString()].toString().contains(auth.uid.toString()) && timeCalc(i)){
                var slotMap = mapOf<String, Any>(i.toString() to slots[i.toString()+auth.uid.toString()].toString() + "- your Appointment")
                submitList.add(slotMap)

            }
            else if(slots[i.toString()].toString().split(",").size < 5 && timeCalc(i)
                && slots[i.toString()] != null){
                var slotMap = mapOf<String, Any>(i.toString() to "[]")
                submitList.add(slotMap)

            }

        }



        return submitList
    }



    private fun timeCalc(selectedHour : Int): Boolean{
        val dateFormat = SimpleDateFormat("yyyy:MM:dd")
        val currDate = dateFormat.format(Date()).toString().replace(":","")
        var todayBoolean = currDate == selectedDate
        var currHour = Calendar.HOUR_OF_DAY + 1
        if(todayBoolean){
            return currHour < selectedHour
        }
        return true
    }

    private fun String.replaceBrackets(): String {
        return replace("[","").replace("]","")
    }
}