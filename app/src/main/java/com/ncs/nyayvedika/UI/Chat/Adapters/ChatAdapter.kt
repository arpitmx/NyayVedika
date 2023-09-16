package com.ncs.nyayvedika.UI.Chat.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ncs.nyayvedika.databinding.BotMessageItemBinding
import com.ncs.nyayvedika.databinding.UserMessageItemBinding
import com.ncs.o2.Domain.Models.Message
import io.noties.markwon.Markwon

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



//class ChatAdapter constructor(val messageList: ArrayList<RecieveMessage>) : Adapter<ChatAdapter.ViewHolder,>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding =
//            BotMessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val msg = messageList.get(position).message
//        holder.binding.tvMessage.text = msg
//    }
//
//    override fun getItemCount(): Int {
//        return messageList.size
//    }
//
//    inner class ViewHolder(val binding: BotMessageItemBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    inner class ViewHolder(val binding: UserMessageItemBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//}


class ChatAdapter(var msgList: MutableList<Message>, val context : Context, val markwon: Markwon) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface onMessageClick {
        fun onClick(position: Int)
    }

    interface onRecentAppClick {
        fun onRecentClick(position: Int)
    }

    companion object {
        const val MESSAGE_TYPE_BOT = 0
        const val MESSAGE_TYPE_USER = 1
    }


    private inner class BotMessage_ViewHolder( val binding : BotMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root){

            fun bind(position: Int){
                val msg = msgList.get(position).message
                //binding.tvMessage.setText(msg)
                markwon.setMarkdown(binding.tvMessage, msg)
            }
        }

    private inner class UserMessage_ViewHolder( val binding: UserMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            val msg = msgList.get(position).message
            binding.tvMessage.setText(msg)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_TYPE_BOT -> {
                BotMessage_ViewHolder(BotMessageItemBinding.inflate(LayoutInflater.from(context),parent,false))

            }
            MESSAGE_TYPE_USER -> {
                UserMessage_ViewHolder(UserMessageItemBinding.inflate(LayoutInflater.from(context),parent,false))
            }
            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return msgList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (msgList[position].msgType == MESSAGE_TYPE_BOT) {
            (holder as BotMessage_ViewHolder).bind(position)

        } else if (msgList[position].msgType == MESSAGE_TYPE_USER) {
            (holder as UserMessage_ViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return msgList[position].msgType
    }

    public fun appendMessage(msg:Message){
        msgList.add(msg)
        notifyDataSetChanged()
    }

//    override fun onClicking(position: Int) {
//        Toast.makeText(context, "app from list is clicked", Toast.LENGTH_SHORT).show()
//        //TODO App advertise list functionality add
//    }

}