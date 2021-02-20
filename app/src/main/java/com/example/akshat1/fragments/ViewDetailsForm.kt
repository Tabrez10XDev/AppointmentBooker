package com.example.akshat1.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.akshat1.R
import com.example.akshat1.databinding.FragmentUserDetailsFormBinding
import com.google.firebase.firestore.FirebaseFirestore

class ViewDetailsForm : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding : FragmentUserDetailsFormBinding

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
        val formDocID = arguments?.getString("formDocID")
        Log.d("formDoc",formDocID.toString())
        fetchForm(formDocID.toString())
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

}