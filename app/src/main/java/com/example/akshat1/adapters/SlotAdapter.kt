package com.example.akshat1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.akshat1.R
import com.example.akshat1.databinding.SlotItemPreviewBinding

class SlotAdapter(rvList : List<Map<String, Any>>) :RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    var loadList = rvList
    private lateinit var binding: SlotItemPreviewBinding

    inner class SlotViewHolder(itemView : View): RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        return SlotViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.slot_item_preview,parent,false ))
    }

    override fun getItemCount(): Int {
        return loadList.size
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = loadList[position]
        binding = SlotItemPreviewBinding.bind(holder.itemView)

        binding.apply {
            tvTime.text = slot.keys.toString()
            tvAppointments.text = slot.values.toString()
        }
         holder.itemView.apply {

            setOnClickListener {
                onItemClickListener?.let { it(slot) }

            }
           }


    }

    private var onItemClickListener : ((Map<String,Any>) -> Unit)?= null

    fun setOnItemClickListener(listener:(Map<String,Any>)->Unit){
        onItemClickListener = listener
    }

}