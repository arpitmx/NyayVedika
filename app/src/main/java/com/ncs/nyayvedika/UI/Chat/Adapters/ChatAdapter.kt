package com.ncs.nyayvedika.UI.Chat.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ncs.nyayvedika.databinding.BotMessageItemBinding
import com.ncs.o2.Domain.Models.RecieveMessage

/*
File : ChatAdapter.kt -> com.ncs.nyayvedika.UI.Chat.Adapters
Description : Adapter for chats 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : NyayVedika Android)

Creation : 5:46 pm on 16/09/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 

*/



class ChatAdapter constructor(val messageList: ArrayList<RecieveMessage>) : Adapter<ChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BotMessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = messageList.get(position).message
        holder.binding.tvMessage.text = msg
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class ViewHolder(val binding: BotMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}