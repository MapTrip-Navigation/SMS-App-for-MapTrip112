package de.infoware.smsparser.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.infoware.smsparser.data.DestinationInfo
import de.infoware.smsparser.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DestinationInfoAdapter :
    RecyclerView.Adapter<DestinationInfoAdapter.DestinationInfoViewHolder>() {

    private val calendar = Calendar.getInstance()

    // Pattern for time displaying
    private val pattern = "HH:mm dd/MM/yyyy "

    private val df = SimpleDateFormat(pattern, Locale.GERMANY)

    private var destinationInfoList: List<DestinationInfo> = ArrayList()

    private lateinit var clickEvent: (DestinationInfo) -> Unit

    class DestinationInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var destinationReason: TextView = itemView.findViewById(R.id.tvReason)
        var destinationAddedTime: TextView = itemView.findViewById(R.id.tvTimeAdded)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinationInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.destination_info_item_view, parent, false
        )
        return DestinationInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DestinationInfoViewHolder, position: Int) {
        val currentDestinationInfo = destinationInfoList[position]

        holder.destinationReason.text = currentDestinationInfo.reason

        calendar.timeInMillis = currentDestinationInfo.addedTimestamp
        holder.destinationAddedTime.text = df.format(calendar.time)
        holder.itemView.setOnClickListener {
            clickEvent(currentDestinationInfo)
        }
    }

    fun replaceEntries(newDestinationInfoList: List<DestinationInfo>) {
        this.destinationInfoList = newDestinationInfoList
        notifyDataSetChanged()
    }

    // Set lambda expression for the click event.
    fun setOnDestinationInfoClickListener(emitClickEvent: (DestinationInfo) -> Unit) {
        this.clickEvent = emitClickEvent
    }

    override fun getItemCount() = destinationInfoList.size
}