package com.ncs.nyayvedika.UI.Chat.BottomSheets

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.databinding.OptionsbottomsheetBinding
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt


/*
File : OptionsBottomSheet.kt -> com.ncs.nyayvedika.UI.Chat.BottomSheets
Description : BottomSheet for options 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : NyayVedika Android)

Creation : 3:16 am on 17/09/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 


*//*
File : OptionsBottomSheet.kt -> com.ncs.nyayvedika.UI.Chat.BottomSheets
Description : BottomSheet for options 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity (@Project : NyayVedika Android)

Creation : 3:16 am on 17/09/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 


*/
class OptionsBottomSheet constructor(val msg:com.ncs.o2.Domain.Models.Message, val receivePDFCallback: recievePDFCallback) : BottomSheetDialogFragment() {

    private val viewModel : OptionsBottomSheetViewModel by viewModels()
    lateinit var binding: OptionsbottomsheetBinding
    private val TAG = "OptionsBottomSheet"
    private lateinit var layout: LinearLayoutCompat
    private lateinit var msgText : TextView
    private lateinit var root : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.pdflayout,container,false)
        binding = OptionsbottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        layout = root.findViewById<LinearLayoutCompat>(R.id.pdfLinLayout)
        msgText = layout.findViewById<TextView>(R.id.text_message)
        msgText.text = msg.message


        setUpViews()
        }

    private fun setUpViews() {
            setUpOnclickListners()
    }

    private fun setUpOnclickListners() {
        binding.btnSendPdf.setOnClickListener{
            generatePdf()
        }

        binding.btnCopy.setOnClickListener{
            copyTextToClipboard(msg.message)
        }

        binding.btnCancel.setOnClickListener{
            dismiss()
        }
    }


    private fun copyTextToClipboard(text:String) {

        val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(activity?.applicationContext, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        dismiss()
    }

    private fun generatePdf() {


        val pdfName = "NV-Export-${Random.nextInt(9999..999999)}"

        PdfGenerator.getBuilder()
            .setContext(requireActivity())
            .fromViewSource()
            .fromView(layout)
            .setFileName(pdfName)
            .setFolderNameOrPath("pdfs/")
            .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.NONE)
            .build(object : PdfGeneratorListener() {
                override fun onFailure(failureResponse: FailureResponse) {
                    super.onFailure(failureResponse)
                    Log.d(TAG, "onFailure: " + failureResponse.errorMessage)
                }

                override fun showLog(log: String) {
                    super.showLog(log)
                    Log.d(TAG, "log: $log")
                }

                override fun onStartPDFGeneration() {}
                override fun onFinishPDFGeneration() {}
                override fun onSuccess(response: SuccessResponse) {
                    super.onSuccess(response)
                    val uri =  response.file.toUri()

                    Log.d(TAG, "Success: " + response.path)
                    receivePDFCallback.sendThisPdf(pdfName,uri)
                }
            })

        this.dismiss()
    }

    interface recievePDFCallback{
            fun sendThisPdf(pdfname : String ,uri: Uri)
    }


}




