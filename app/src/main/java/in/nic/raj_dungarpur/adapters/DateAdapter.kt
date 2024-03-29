package `in`.nic.raj_dungarpur.adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.NIC_Dungarpur.R
import com.example.NIC_Dungarpur.databinding.DateItemPreviewBinding

class DateAdapter :RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    inner class DateViewHolder(itemView : View): RecyclerView.ViewHolder(itemView)

    private lateinit var binding: DateItemPreviewBinding

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
        Log.d("SlotUser",differ.currentList.toString())
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = differ.currentList[position]

        binding = DateItemPreviewBinding.bind(holder.itemView)
        binding.apply {

            val appoinmentDate = date["date"].toString()
            val displayDate =
                    (appoinmentDate.subSequence(6, 8).toString() + "/"
                            + appoinmentDate.subSequence(4, 6) + "/"
                            + appoinmentDate.subSequence(0, 4))
            tvDate.text = displayDate
            holder.itemView.apply {
                setOnClickListener {
                    onItemClickListener?.let { it(date)
                    Log.d("shabba",date.toString())}
                    Log.d("Nazz",position.toString())
                    Log.d("Nazz",date.toString())
                }
            }
        }
    }

    private var onItemClickListener : ((Map<String,Any>) -> Unit)?= null

    fun setOnItemClickListener(listener:(Map<String,Any>)->Unit){
        onItemClickListener = listener
    }

}