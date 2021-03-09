package com.example.akshat1.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.example.akshat1.api.RetrofitInstance
import com.example.akshat1.databinding.FragmentUserDetailsFormBinding
import com.example.akshat1.util.apikey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.sql.Time
import java.sql.Timestamp
import java.util.*
import kotlin.random.Random

class UserDetailsForm : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentUserDetailsFormBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
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
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
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
                uploadForm(slotMap, name.toString())
            }
            else{
                Toast.makeText(requireActivity(),"Fill all the fields",Toast.LENGTH_SHORT).show()
            }

        }

        binding.uploadFilebtn.setOnClickListener {
            selectFile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null ){
            data.data?.let {uri->
                showbar()
                uploadPhoto(uri)

            }
        }
    }

    private fun uploadPhoto(file : Uri) = CoroutineScope(Dispatchers.IO).launch{
        val filePath = Objects.hash(auth.uid + file).toString()
        val imageRef = firebaseStorage.reference.child(filePath)
            imageRef.putFile(file).addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {  uri->
                    selectedUri = uri.toString()
                    binding.tvUri.text = selectedUri
                    hidebar()
                }
                hidebar()
            }.addOnCanceledListener {
                hidebar()
            }.addOnFailureListener {
                hidebar()
            }
    }

    private fun selectFile(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*" //check in shameers phone TODO
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(Intent.createChooser(intent, "Select File"),1);
    }

    private fun hidebar(){
        binding.progressBar.visibility = View.INVISIBLE
        binding.uploadFilebtn.isEnabled = true

    }

    private fun showbar(){
        binding.progressBar.visibility = View.VISIBLE
        binding.uploadFilebtn.isEnabled = false
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
        binding.uploadFilebtn.isEnabled = false
        binding.Formbtn.isEnabled = false

    }

    private fun uploadForm(slotMap : Map<String, Any>, name : String) = CoroutineScope(Dispatchers.IO).launch {
        val fireStore = firestore.collection("dates").document()

        fireStore.set(slotMap).addOnSuccessListener {
            setMeetLink(fireStore)
        }
        addSlot(fireStore, name, slotMap["meetLink"].toString())
    }

    private fun setMeetLink(docRef : DocumentReference){
        val data = JsonObject()
        data.addProperty("apikey", apikey)
        val time = slotKey+":00"
        val date = selectedDate.subSequence(0,4).toString() + "-" + selectedDate.subSequence(4,6) + "-" + selectedDate.subSequence(6,8)
        data.addProperty("time", time )
        data.addProperty("date", date)

        var link = ""
        CoroutineScope(Dispatchers.IO).launch{
            val retrievedData = RetrofitInstance.api.getMeetLink(data)
            if(retrievedData.isSuccessful){
            link = retrievedData.body()?.link.toString()
                docRef.update("meetLink", link)
                withContext(Dispatchers.Main){
                    binding.tvAPI.text = link
                }

            }
        }

    }
}