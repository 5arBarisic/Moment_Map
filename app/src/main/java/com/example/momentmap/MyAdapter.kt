package com.example.momentmap

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val context: Context, private var momentList: List<Moment>) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.moment_item, parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.recTitle.text = momentList[position].title
        holder.recDate.text = momentList[position].date

        holder.recCard.setOnClickListener {
            val intent = Intent(context, MomentDetails::class.java)
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
}
class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var recTitle: TextView
    var recDate: TextView
    var recCard: CardView
    init {
        recTitle = itemView.findViewById(R.id.recTitle)
        recDate = itemView.findViewById(R.id.recDate)
        recCard = itemView.findViewById(R.id.recCard)
    }
}