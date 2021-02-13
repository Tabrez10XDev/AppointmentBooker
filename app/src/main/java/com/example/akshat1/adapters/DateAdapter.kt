package com.example.akshat1.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.akshat1.R

class DateAdapter :RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    inner class DateViewHolder(itemView : View): RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Map<String, Any>>(){
        override fun areItemsTheSame(oldItem: Map<String, Any>, newItem: Map<String, Any>): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Map<String, Any>, newItem: Map<String, Any>): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        return DateViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.date_item_preview,parent,false ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        //val article = differ.currentList[position]
       // holder.itemView.apply {

//            setOnClickListener {
//                Toast.makeText(context,"yex",Toast.LENGTH_SHORT).show()
//                onItemClickListener?.let { it(article) }
//            }
     //   }


    }
//
//    private var onItemClickListener : ((Map<String,Any>) -> Unit)?= null
//
//    fun setOnItemClickListener(listener:(Map<String,Any>)->Unit){
//        onItemClickListener = listener
//    }

}