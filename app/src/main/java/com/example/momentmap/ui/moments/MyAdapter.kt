package com.example.momentmap.ui.moments

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.momentmap.ui.moment_detail.MomentDetails
import com.example.momentmap.R
import com.example.momentmap.data.Moment

class MyAdapter(private val context: Context, private var momentList: List<Moment>) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.moment_item, parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.recTitle.text = momentList[position].title
        holder.recDate.text = momentList[position].date
        holder.recLocation.text = momentList[position].location

        holder.recCard.setOnClickListener {
            val intent = Intent(context, MomentDetails::class.java)
            intent.putExtra("Id",momentList[holder.adapterPosition].id)
            intent.putExtra("Image", momentList[holder.adapterPosition].imageUrl)
            intent.putExtra("Description", momentList[holder.adapterPosition].description)
            intent.putExtra("Title", momentList[holder.adapterPosition].title)
            intent.putExtra("Location", momentList[holder.adapterPosition].location)
            intent.putExtra("Date", momentList[holder.adapterPosition].date)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return momentList.size
    }
    fun searchMomentList(searchList: List<Moment>) {
        momentList = searchList
        notifyDataSetChanged()
    }
}
class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var recTitle: TextView
    var recDate: TextView
    var recLocation: TextView
    var recCard: CardView
    init {
        recTitle = itemView.findViewById(R.id.recTitle)
        recLocation = itemView.findViewById(R.id.recLocation)
        recDate = itemView.findViewById(R.id.recDate)
        recCard = itemView.findViewById(R.id.recCard)
    }
}