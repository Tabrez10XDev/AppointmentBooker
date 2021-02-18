package com.example.akshat1.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akshat1.R
import com.example.akshat1.adapters.DateAdapter
import com.example.akshat1.adapters.SlotAdapter
import com.example.akshat1.app.Dashboard
import com.example.akshat1.app.Login
import com.example.akshat1.databinding.DashboardBinding
import com.example.akshat1.databinding.FragmentSlotViewBinding
import com.example.akshat1.dialogs.DialogSlot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Stream


private lateinit var dateAdapter : DateAdapter
private lateinit var binding: FragmentSlotViewBinding
private lateinit var fireStore : FirebaseFirestore
private lateinit var auth : FirebaseAuth
private lateinit var slotAdapter : SlotAdapter
private lateinit var selectedDate : String

class SlotView : Fragment() {




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_slot_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSlotViewBinding.bind(view)

        setupDateRecyclerView()

        setupSlotRecyclerView()

        auth = FirebaseAuth.getInstance()

        fireStore = FirebaseFirestore.getInstance()

        setHasOptionsMenu(true)

        subscribeToDates()


        slotAdapter.setOnItemClickListener {slots->
            val submitList = mutableListOf<Map<String,Any>>()

            val mapValues = slots.values.toString().split(",")
            val mapKey = slots.keys.toString()
            for(mapValue in mapValues){
                val slotMap = mapOf<String,Any>(mapKey to mapValue)
                submitList.add(slotMap)
            }
            Log.d("subbmitList",submitList.toString())
            DialogSlot.newInstance(submitList, selectedDate)
                    .show(childFragmentManager, DialogSlot.TAG)

        }

        dateAdapter.setOnItemClickListener {slots->
            val submitList = feedSlotDataset(slots)

            slotAdapter.loadList = submitList
            slotAdapter.notifyDataSetChanged()

        }



    }






    private fun setupSlotRecyclerView(){
        slotAdapter = SlotAdapter(mutableListOf())
        binding.rvSlot.apply {
            adapter = slotAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)

        }
    }

    private fun setupDateRecyclerView(){
        dateAdapter = DateAdapter()
        binding.rvDate.apply {
            adapter = dateAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL,false)

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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.author_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout->{
                logOut()
            }
            R.id.add_dates->{

                findNavController().navigate(R.id.action_slotView_to_addSlot)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun logOut(){
        auth.signOut()
        val intent = Intent(requireActivity(), Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun timeCalc(selectedHour : Int): Boolean{
        val dateFormat = SimpleDateFormat("yyyy:MM:dd")
        val currDate = dateFormat.format(Date()).toString().replace(":","")
        var todayBoolean = currDate == selectedDate
        var currHour = Calendar.HOUR_OF_DAY + 1
        Log.d("times",currHour.toString())
        if(todayBoolean){
            return currHour < selectedHour
        }
        return true
    }

    private fun feedSlotDataset(slots : Map<String, Any>): List<Map<String, Any>>{

        val submitList =  mutableListOf<Map<String, Any>>()
        selectedDate = slots["date"].toString()

        if(timeCalc(8)){
            var slotMap = mapOf<String, Any>("8" to slots.get("8").toString())
            submitList.add(slotMap)
        }

        if(timeCalc(9)){
            var slotMap = mapOf<String, Any>("9" to slots.get("9").toString())
            submitList.add(slotMap)
        }

        if(timeCalc(10)){
            var slotMap = mapOf<String, Any>("10" to slots.get("10").toString())
            submitList.add(slotMap)
        }

        if(timeCalc(11)){
            var slotMap = mapOf<String, Any>("11" to slots.get("11").toString())
            submitList.add(slotMap)
        }

        if(timeCalc(12)){
            var slotMap = mapOf<String, Any>("12" to slots.get("12").toString())
            submitList.add(slotMap)
        }


        if(timeCalc(14)){
            var slotMap = mapOf<String, Any>("14" to slots.get("14").toString())
            submitList.add(slotMap)
        }


        if(timeCalc(15)){
            var slotMap = mapOf<String, Any>("15" to slots.get("15").toString())
            submitList.add(slotMap)
        }

        if(timeCalc(16)){
            var slotMap = mapOf<String, Any>("16" to slots.get("16").toString())
            submitList.add(slotMap)
        }

        if(timeCalc(17)){
            var slotMap = mapOf<String, Any>("17" to slots.get("17").toString())
            submitList.add(slotMap)
        }


        return submitList
    }


}