package com.example.NIC_Dungarpur.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.NIC_Dungarpur.R
import com.example.NIC_Dungarpur.api.RetrofitInstance
import com.example.NIC_Dungarpur.databinding.FragmentUserDetailsFormBinding
import com.example.NIC_Dungarpur.util.apikey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class UserDetailsForm : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentUserDetailsFormBinding
    private lateinit var firestore: FirebaseFirestore
    private var selectedDate = ""
    private var selectedUri : String ?= ""
    private var slotKey : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_details_form, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentUserDetailsFormBinding.bind(view)
        hidebar()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        firestore = FirebaseFirestore.getInstance()
        selectedDate = this.arguments?.getString("selectedDate") ?: ""
        slotKey = this.arguments?.getString("slotKey") ?: ""
        auth = FirebaseAuth.getInstance()
        binding.tvFormDate.text = selectedDate.subSequence(6,8).toString() + ":" + selectedDate.subSequence(4,6) + ":" +selectedDate.subSequence(0,4)
        binding.tvFormTime.text = slotKey + ":00"
        binding.Formbtn.setOnClickListener{
            val name = binding.etName.text
            val number = binding.etNumber.text
            val email = binding.etEmail.text
            val address = binding.etAddress.text
            val visitPurpose = binding.etVisitPurpose.text

            if(name.isNotEmpty()
                && number.isNotEmpty()
                && email.isNotEmpty()
                && address.isNotEmpty()
                && visitPurpose.isNotEmpty()
                && number.length == 10){
                val slotMap = mapOf<String,Any>(
                        "name" to name.toString(),
                        "number" to number.toString(),
                        "email" to email.toString(),
                        "address" to address.toString(),
                        "visitPurpose" to visitPurpose.toString(),
                        "selectedUri" to selectedUri.toString(),
                        "time" to slotKey,
                        "date" to selectedDate,
                        "meetLink" to ""
                )
                showbar()

                uploadForm(slotMap, name.toString())
            }
            else{
                Toast.makeText(requireActivity(),"Fill all the fields",Toast.LENGTH_SHORT).show()
            }

        }


    }





    private fun hidebar(){
        binding.progressBar.visibility = View.INVISIBLE

    }

    private fun showbar(){
        binding.progressBar.visibility = View.VISIBLE
    }

//    private fun deleteSlot(slot : Map<String, Any>){
//
//        firestore.collection("dates").document(selectedDate).update(
//                slot.keys.toString().replaceBrackets(), FieldValue.arrayRemove(auth.uid.toString())
//        ).addOnSuccessListener {
//        }.addOnFailureListener{
//        }
//    }

    private fun addSlot(documentReference: DocumentReference, name : String, meetLink : String) = CoroutineScope(Dispatchers.IO).launch{
        val fireStore = firestore.collection("dates").document(selectedDate)
        fireStore.update(
                slotKey, FieldValue.arrayUnion(auth.uid.toString())
        )
        val slotMap = mapOf<String, Any>(
                slotKey+auth.uid.toString() to  name,
                slotKey+auth.uid.toString()+"Ref" to documentReference.id
        )
        fireStore.set(slotMap,SetOptions.merge())
        withContext(Dispatchers.Main){
            Toast.makeText(requireActivity(),"Created Booking",Toast.LENGTH_SHORT).show()
            hidebar()
            setDisabled()
            binding.tvAPI.text = meetLink

        }
    }

    private fun setDisabled(){
        binding.etName.isEnabled = false
        binding.etNumber.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etAddress.isEnabled = false
        binding.etVisitPurpose.isEnabled = false
        binding.Formbtn.isEnabled = false

    }

    private fun uploadForm(slotMap : Map<String, Any>, name : String) = CoroutineScope(Dispatchers.IO).launch {

        val fireStore = firestore.collection("dates").document()

        fireStore.set(slotMap).addOnSuccessListener {
            Log.d("onccc","yes")

            setMeetLink(fireStore)

        }
        addSlot(fireStore, name, slotMap["meetLink"].toString())
    }

    private fun setMeetLink(docRef : DocumentReference){
        val data = JsonObject()
        data.addProperty("apikey", apikey)
        var time = ""
        time = if(slotKey.length == 1){
            "0$slotKey:00"
        }
        else{
            slotKey + ":00"
        }

        val date = selectedDate.subSequence(0,4).toString() + "-" + selectedDate.subSequence(4,6) + "-" + selectedDate.subSequence(6,8)
        data.addProperty("time", time )
        data.addProperty("date", date)

        Log.d("onccc", apikey + "\n" + time + "\n" + date)
        var link = ""
        CoroutineScope(Dispatchers.IO).launch{
            val retrievedData = RetrofitInstance.api.getMeetLink(data)
            Log.d("onccc",retrievedData.toString())

            if(retrievedData.isSuccessful){
                Log.d("onccc","success")

                link = retrievedData.body()?.link.toString()
                if(link.isEmpty()){
                    link = retrievedData.message()
                }
                docRef.update("meetLink", link)
                withContext(Dispatchers.Main){
                    binding.tvAPI.text = link
                }

            }
        }

    }
}