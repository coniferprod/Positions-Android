package com.coniferproductions.positions

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class PositionsListAdapter internal constructor(context: Context): RecyclerView.Adapter<PositionsListAdapter.ViewHolder>() {
    private val inflater: LayoutInflater
    private var positionsList: List<Position>? = null

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, index: Int) {
        if (positionsList != null) {
            val position = positionsList!![index]
            holder.description.text = position.description
        }
        else {
            holder.description.text = "(unknown)"
        }
    }

    override fun getItemCount(): Int {
        return if (positionsList != null) positionsList!!.size else 0
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

    internal fun setPositions(positionsList: List<Position>) {
        this.positionsList = positionsList
        notifyDataSetChanged()
    }
}
