package com.coniferproductions.positions

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class PositionAdapter(private val ctx: Context, private val positions: List<Position>): RecyclerView.Adapter<PositionAdapter.ViewHolder>() {
    private val inflater: LayoutInflater

    init {
        inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = positions[position]
        holder.description.text = pos.description
    }

    override fun getItemCount(): Int {
        return positions.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val description: TextView

        init {
            description = itemView.findViewById<View>(R.id.description) as TextView
        }
    }
}
