package `in`.nic.raj_dungarpur.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.NIC_Dungarpur.R
import com.example.NIC_Dungarpur.databinding.SlotItemPreviewBinding

class SlotAdapter(rvList : List<Map<String, Any>>) :RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    var loadList = rvList
    private lateinit var binding: SlotItemPreviewBinding

    inner class SlotViewHolder(itemView : View): RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        Log.d("createView",loadList.size.toString()
        )
        return SlotViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.slot_item_preview,parent,false ))
    }

    override fun getItemCount(): Int {
        return loadList.size
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = loadList[position]
        binding = SlotItemPreviewBinding.bind(holder.itemView)

        binding.apply {
            val filteredMap = slot.filter { (key, value) -> !key.endsWith("uid") }

            tvTime.text = "TIME: " + filteredMap.keys.toString().replaceBrackets() + ".00"
            tvAppointments.text = filteredMap.values.toString().replaceBrackets()

            holder.itemView.apply {

                setOnClickListener {
                    onItemClickListener?.let { it(slot, position) }
                    Log.d("soby", slot.toString())

                }
            }
        }


    }

    private var onItemClickListener : ((Map<String,Any>, position : Int) -> Unit)?= null

    fun setOnItemClickListener(listener:(Map<String,Any>, Int)->Unit){
        onItemClickListener = listener
    }

}

private fun String.replaceBrackets(): String {
    return replace("[","").replace("]","")
}
