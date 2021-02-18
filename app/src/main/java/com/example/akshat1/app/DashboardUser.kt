package com.example.akshat1.app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akshat1.R
import com.example.akshat1.adapters.DateAdapter
import com.example.akshat1.adapters.SlotAdapter
import com.example.akshat1.databinding.DashboardUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.NonCancellable.cancel
import java.text.SimpleDateFormat
import java.util.*

class DashboardUser : AppCompatActivity() {

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var slotAdapter: SlotAdapter
    private lateinit var dateAdapter : DateAdapter
    private lateinit var auth : FirebaseAuth
    private lateinit var binding : DashboardUserBinding
    private lateinit var selectedDate : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        binding = DashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("shittes",auth.currentUser!!.uid)
        setupDateRecyclerView()
        setupSlotRecyclerView()

        subscribeToDates()

        dateAdapter.setOnItemClickListener {slots->
            selectedDate = slots["date"].toString()

            val submitList = feedSlotDataset(slots)
            slotAdapter.loadList = submitList
            slotAdapter.notifyDataSetChanged()
        }


        slotAdapter.setOnItemClickListener {slot->
            var message = "Do you want to place the appointment"
            var bool : Boolean = false
            if(slot.values.contains(auth.uid.toString())){
                bool = true
                message  = "Do you want to wish delete your appointment"
            }
            Log.d("CheckDialogListener","heyyy")

            val alertDialog: AlertDialog? = this?.let {
                val builder = AlertDialog.Builder(it)

                builder.apply {
                    setPositiveButton(R.string.ok,
                            DialogInterface.OnClickListener { dialog, id ->
                                if(bool){
                                    deleteSlot(slot)
                                    refreshSlotDataset()
                                }
                                else{
                                    addSlot(slot)
                                    refreshSlotDataset()
                                }
                            })
                    setNegativeButton(R.string.cancel,
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss()
                            })
                    setTitle(message)
                }

                builder.create()
            }

            alertDialog?.show()



        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout-> {
                logOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

        private fun logOut(){
            auth.signOut()
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            this?.finish()
        }

    private fun setupSlotRecyclerView(){
        slotAdapter = SlotAdapter(mutableListOf())
        binding.rvSlotUser.apply {
            adapter = slotAdapter
            layoutManager = LinearLayoutManager(this@DashboardUser, LinearLayoutManager.VERTICAL,false)

        }
    }

    private fun setupDateRecyclerView(){
        dateAdapter = DateAdapter()
        binding.rvDateUser.apply {
            adapter = dateAdapter
            layoutManager = LinearLayoutManager(this@DashboardUser, LinearLayoutManager.HORIZONTAL,false)

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

        if(slots["8"].toString().contains(auth.uid.toString()) && timeCalc(8)){
            var slotMap = mapOf<String, Any>("8" to auth.uid.toString() )
            submitList.add(slotMap)

        }
        else if(slots["8"].toString().split(",").size < 5 && timeCalc(8)){
            var slotMap = mapOf<String, Any>("8" to "[]")
            submitList.add(slotMap)

        }


        if(slots["9"].toString().contains(auth.uid.toString()) && timeCalc(9)){
            var slotMap = mapOf<String, Any>("9" to auth.uid.toString() )
            submitList.add(slotMap)

        }
        else if(slots["9"].toString().split(",").size < 5 && timeCalc(9)){
            var slotMap = mapOf<String, Any>("9" to "[]")
            submitList.add(slotMap)

        }

        if(slots["10"].toString().contains(auth.uid.toString()) && timeCalc(10)){
            var slotMap = mapOf<String, Any>("10" to auth.uid.toString() )
            submitList.add(slotMap)

        }
        else if(slots["10"].toString().split(",").size < 5 && timeCalc(10)){
            var slotMap = mapOf<String, Any>("10" to "[]")
            submitList.add(slotMap)

        }

        if(slots["11"].toString().contains(auth.uid.toString()) && timeCalc(11)){
            var slotMap = mapOf<String, Any>("11" to auth.uid.toString() )
            submitList.add(slotMap)

        }
        else if(slots["11"].toString().split(",").size < 5 && timeCalc(11)){
            var slotMap = mapOf<String, Any>("11" to "[]")
            submitList.add(slotMap)

        }

        if(slots["12"].toString().contains(auth.uid.toString()) && timeCalc(12)){
            var slotMap = mapOf<String, Any>("8" to auth.uid.toString() )
            submitList.add(slotMap)

        }
        else if(slots["12"].toString().split(",").size < 5 && timeCalc(12)){
            var slotMap = mapOf<String, Any>("12" to "[]")
            submitList.add(slotMap)

        }


        if(slots["14"].toString().contains(auth.uid.toString()) && timeCalc(14)){
            var slotMap = mapOf<String, Any>("14" to auth.uid.toString() )
            submitList.add(slotMap)

        }
        else if(slots["14"].toString().split(",").size < 5 && timeCalc(14)){
            var slotMap = mapOf<String, Any>("14" to "[]")
            submitList.add(slotMap)

        }

        if(slots["15"].toString().contains(auth.uid.toString()) && timeCalc(15)){
            var slotMap = mapOf<String, Any>("15" to auth.uid.toString() )
            submitList.add(slotMap)

        }
        else if(slots["15"].toString().split(",").size < 5 && timeCalc(15)){
            var slotMap = mapOf<String, Any>("15" to "[]")
            submitList.add(slotMap)

        }

        if(slots["16"].toString().contains(auth.uid.toString()) && timeCalc(16)){
            var slotMap = mapOf<String, Any>("16" to auth.uid.toString() )
            submitList.add(slotMap)

        }
        else if(slots["16"].toString().split(",").size < 5 && timeCalc(16)){
            var slotMap = mapOf<String, Any>("16" to "[]")
            submitList.add(slotMap)

        }

        if(slots["17"].toString().contains(auth.uid.toString()) && timeCalc(17)){
            var slotMap = mapOf<String, Any>("17" to auth.uid.toString() )
            submitList.add(slotMap)

        }
        else if(slots["17"].toString().split(",").size < 5 && timeCalc(17)){
            var slotMap = mapOf<String, Any>("17" to "[]")
            submitList.add(slotMap)

        }


        return submitList
    }

    private fun deleteSlot(slot : Map<String, Any>){
        Log.d("LastDebug","Waiting...")

        fireStore.collection("dates").document(selectedDate).update(
                slot.keys.toString().replaceBrackets(), FieldValue.arrayRemove(auth.uid.toString())
        ).addOnSuccessListener {

            Log.d("LastDebug","Success")
        }.addOnFailureListener{
            Log.d("LastDebug","Failure")
        }

    }

    private fun addSlot(slot : Map<String, Any>){
        fireStore.collection("dates").document(selectedDate).update(
                slot.keys.toString().replaceBrackets(), FieldValue.arrayUnion(auth.uid.toString())
        ).addOnSuccessListener {

        }.addOnFailureListener{

        }

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