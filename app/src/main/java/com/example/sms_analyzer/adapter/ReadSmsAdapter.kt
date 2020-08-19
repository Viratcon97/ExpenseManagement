package com.example.sms_analyzer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sms_analyzer.R
import com.example.sms_analyzer.model.SmsData
import kotlinx.android.synthetic.main.layout_sms.view.*

class ReadSmsAdapter(val smslist: ArrayList<SmsData>) : RecyclerView.Adapter<ReadSmsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_sms, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return smslist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(smslist[position])
    }


    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(smsData: SmsData) {

            val address = itemView.findViewById(R.id.textViewNumber) as TextView
            val message = itemView.findViewById(R.id.textViewMessage) as TextView

            address.text = smsData.address
            message.text = smsData.body
        }
    }

}

