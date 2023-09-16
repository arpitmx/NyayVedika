package com.ncs.nyayvedika.UI.Chat.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

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



class ChatAdapter constructor() : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TagItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        with(holder.binding.tag) {
            this.text = tagItem.tagText
            this.setBackgroundColor(Color.parseColor(tagItem.bgColor))
            this.setTextColor(Color.parseColor(tagItem.textColor))
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    inner class ViewHolder(val binding: TagItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}