package com.example.akshat1.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.akshat1.R
import com.example.akshat1.databinding.FragmentUserDetailsFormBinding
import com.example.akshat1.dialogs.DialogSlot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.sql.Ref

class ViewDetailsForm : Fragment() {

    private lateinit var uid : String
    private lateinit var formDocID : String
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding : FragmentUserDetailsFormBinding
    private lateinit var selectedDate: String
    private lateinit var slotTime : String

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
        firestore = FirebaseFirestore.getInstance()
        setDisabled()
        formDocID = arguments?.getString("formDocID").toString()
        slotTime = arguments?.getString("slotKey").toString()
        selectedDate = arguments?.getString("selectedDate").toString()
        uid = arguments?.getString("uid").toString()
        fetchForm(formDocID)
        Log.d("InsideDetails",formDocID)

        Log.d("InsideViewForm", slotTime + selectedDate)
        binding.Formbtn.setOnClickListener {
            deleteForm()
        }
    }


    private fun deleteForm() = CoroutineScope(Dispatchers.IO).launch{
    try{
        firestore.collection("dates").document(formDocID).delete().addOnSuccessListener {
            deleteSlot()
            setDisabled()
            Toast.makeText(requireActivity(),"Success", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()

        }

        }
    catch (e : Exception){
        withContext(Dispatchers.Main){
            Toast.makeText(requireActivity(),e.message, Toast.LENGTH_LONG).show()
        }
    }
    }

    private fun setDisabled(){
        binding.etName.isEnabled = false
        binding.etNumber.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etAddress.isEnabled = false
        binding.etVisitPurpose.isEnabled = false
        binding.uploadFilebtn.isEnabled = false
        binding.Formbtn.text = "Delete Slot"

    }

    private fun fetchForm(docID : String){
        firestore.collection("dates").document(docID).get().addOnSuccessListener {form->
            val data = form.data
            data?.let {
            binding.etName.setText(data!!["name"].toString())
            binding.etNumber.setText(data!!["number"].toString())
            binding.etEmail.setText(data!!["email"].toString())
            binding.etAddress.setText(data!!["address"].toString())
            binding.etVisitPurpose.setText(data!!["visitPurpose"].toString())
            binding.tvUri.text = data["selectedUri"].toString()
            val selectedDate = data["date"].toString()
           // binding.tvFormDate.text = selectedDate.subSequence(6,8).toString() + ":" + selectedDate.subSequence(4,6) + ":" +selectedDate.subSequence(0,4)
           // binding.tvFormTime.text = data["time"].toString() + ":00"
            binding.uploadFilebtn.isEnabled = false
                hidebar()
            }
        }.addOnFailureListener {
            hidebar()
        }.addOnCanceledListener {
            hidebar()
        }
    }
    private fun hidebar(){
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showbar(){
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun deleteSlot() = CoroutineScope(Dispatchers.IO).launch{
        val docRef = firestore.collection("dates").document(selectedDate)
        docRef.update(
                slotTime, FieldValue.arrayRemove(uid)
        )
        docRef.update(
                slotTime+uid+"Ref", FieldValue.delete()
        )
        docRef.update(
                slotTime + uid , FieldValue.delete()
        )
    }

}