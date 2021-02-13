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
import com.example.akshat1.app.Dashboard
import com.example.akshat1.app.Login
import com.example.akshat1.databinding.DashboardBinding
import com.example.akshat1.databinding.FragmentSlotViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

private lateinit var dateAdapter : DateAdapter
private lateinit var binding: FragmentSlotViewBinding
private lateinit var fireStore : FirebaseFirestore
private lateinit var auth : FirebaseAuth
class SlotView : Fragment() {






    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_slot_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSlotViewBinding.bind(view)
        setupRecyclerView()
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        setHasOptionsMenu(true)
        subscribeToDates()


    }







    private fun setupRecyclerView(){
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
                Log.d("Excep-Dashboard",it.message.toString())

                return@addSnapshotListener
            }

            querySnapshot?.let {documents->
                val sb : MutableList<Map<String,Any>> = arrayListOf()
                for(document in documents) {
                    sb.add(document.data)
                    Log.d("sb-Dashboard",document.data.toString())

                }

                dateAdapter.differ.submitList(sb)

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


}