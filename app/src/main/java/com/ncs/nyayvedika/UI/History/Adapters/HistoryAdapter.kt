package com.ncs.nyayvedika.UI.History.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.databinding.HistoryItemBinding

/*
File : HistoryAdapter.kt -> com.ncs.nyayvedika.UI.History.Adapters
Description : Adapter for history 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : NyayVedika Android)

Creation : 5:27 am on 19/09/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 


*//*


*/
class HistoryAdapter (context: Context, private val items: List<String>) : ArrayAdapter<String>(context, R.layout.history_item, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val binding = HistoryItemBinding.inflate(inflater,parent,false)
            val textView : TextView = binding.tvChatHistory
            textView.text = items[position]
            return binding.root
        }
    }
