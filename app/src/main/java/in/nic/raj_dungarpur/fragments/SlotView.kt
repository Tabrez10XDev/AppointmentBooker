package `in`.nic.raj_dungarpur.fragments

import `in`.nic.raj_dungarpur.adapters.DateAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.NIC_Dungarpur.R
import `in`.nic.raj_dungarpur.adapters.SlotAdapter
import `in`.nic.raj_dungarpur.app.Login
import com.example.NIC_Dungarpur.databinding.FragmentSlotViewBinding
import `in`.nic.raj_dungarpur.dialogs.DialogSlot
import `in`.nic.raj_dungarpur.util.BounceEdgeEffectFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*


private lateinit var dateAdapter : DateAdapter
private lateinit var binding: FragmentSlotViewBinding
private lateinit var fireStore : FirebaseFirestore
private lateinit var auth : FirebaseAuth
private lateinit var slotAdapter : SlotAdapter
private var selectedDate : String ?= null
private var dateMap = mapOf<String,Any>()

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


        slotAdapter.setOnItemClickListener { slots, _->
            val nameMap = slots.filter { (key, value) -> !key.endsWith("uid")}
            val uidMap = slots.filter { (key, value) -> key.endsWith("uid")}
            val submitList = mutableListOf<Map<String,Any>>()
            val mapValues = nameMap.values.toString().split(",")
            val mapKey = nameMap.keys.toString()

            for(mapValue in mapValues){
                val slotMap = mapOf<String,Any>(
                        mapKey to mapValue,
                        mapKey+"uid" to uidMap[mapKey.replaceBrackets()+"uid"].toString()
                )
                submitList.add(slotMap)

            }
            val dialog = DialogSlot.newInstance(submitList, selectedDate.toString(),
                dateMap
            )
            dialog.show(childFragmentManager, DialogSlot.TAG)

        }

        dateAdapter.setOnItemClickListener { slots->
            dateMap = slots
            val submitList = feedSlotDataset(slots)
            slotAdapter.loadList= submitList
            slotAdapter.notifyDataSetChanged()

        }



    }

    override fun onResume() {
        selectedDate?.let {
            Log.d("megasuper", selectedDate.toString())
          //  refreshSlotDataset(selectedDate.toString())
        }
        super.onResume()
    }








    private fun setupSlotRecyclerView(){
        slotAdapter =
            SlotAdapter(mutableListOf())
        binding.rvSlot.apply {
            adapter = slotAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)
            edgeEffectFactory =
                BounceEdgeEffectFactory()
        }
    }

    private fun setupDateRecyclerView(){
        dateAdapter =
            DateAdapter()
        binding.rvDate.apply {
            adapter = dateAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL,false)
            edgeEffectFactory =
                BounceEdgeEffectFactory()

        }
    }

    private fun subscribeToDates(){



        val dateFormat = SimpleDateFormat("yyyy:MM:dd")
        val currDate = dateFormat.format(Date()).toString().replace(":","").toInt()


        val fire = fireStore.collection("dates").whereGreaterThanOrEqualTo("date",currDate).orderBy("date", Query.Direction.ASCENDING)
        fire.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Log.d("Error-Dashboard",it.message.toString())
                Log.d("Lj","Dates")

                return@addSnapshotListener
            }

            Log.d("Lj",querySnapshot.toString() + "q")

            querySnapshot?.let {documents->
                val submitList : MutableList<Map<String,Any>> = arrayListOf()
                for(document in documents) {
                    submitList.add(document.data)
                }
                Log.d("Lj",submitList.toString() + "l")

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
        val current = Date()
        val formatter = SimpleDateFormat("HH")
        val time = formatter.format(current)
        var currHour = time.toInt()

        if(todayBoolean){
            return currHour <= selectedHour
        }
        return true
    }

    private fun feedSlotDataset(slots : Map<String, Any>): List<Map<String, Any>>{

        val submitList : MutableList<Map<String,Any>> = arrayListOf()
        selectedDate = slots["date"].toString()

        for(i in 0..24){
            if(timeCalc(i)){
                slots.get((i.toString()))?.let {
                    var nameList = mutableListOf<String>()
                    var uidList = mutableListOf<String>()
                    val slotList = slots[i.toString()].toString().split(",")
                    for (slot in slotList){

                        uidList.add(slot.replaceBrackets().trim())
                        nameList.add(dateMap[i.toString()+slot.replaceBrackets().trim()].toString())
                    }
                    nameList?.let{nameList->
                        var slotMap = mapOf<String, Any>(
                                i.toString() to nameList.toString().replaceBrackets(),
                                i.toString()+"uid" to uidList.toString().replaceBrackets()
                        )
                        submitList?.add(slotMap)
                    }
                }
            }

        }


        return submitList
    }

    private fun String.replaceBrackets(): String {
        return replace("[","").replace("]","")
    }


}