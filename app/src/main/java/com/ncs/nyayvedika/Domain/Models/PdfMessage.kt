package com.ncs.nyayvedika.Domain.Models

import android.graphics.Bitmap
import android.net.Uri
import com.ncs.o2.Domain.Models.Message


/*
File : RecievedPdfMessage.kt -> com.ncs.nyayvedika.Constants
Description : Data class for recieved pdf message 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : NyayVedika Android)

Creation : 7:27 am on 17/09/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 

*/

data class PdfMessage
    (val msg:String="", override var msgType: Int, val uri: Uri, val bitmap: Bitmap, val fileName : String) : Message(msg,msgType)