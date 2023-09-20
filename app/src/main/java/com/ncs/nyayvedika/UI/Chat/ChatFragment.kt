package com.ncs.nyayvedika.UI.Chat

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.ncs.nyayvedika.Constants.ApiEndpoints
import com.ncs.nyayvedika.Domain.Api.ChatApiService
import com.ncs.nyayvedika.Domain.Api.RetrofitClient
import com.ncs.nyayvedika.Domain.Models.Answer
import com.ncs.nyayvedika.Domain.Models.PdfMessage
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.UI.Chat.Adapters.ChatAdapter
import com.ncs.nyayvedika.UI.Chat.BottomSheets.OptionsBottomSheet
import com.ncs.nyayvedika.databinding.FragmentChatBinding
import com.ncs.o2.Domain.Models.Message
import com.ncs.o2.Domain.Models.ServerResult
import com.ncs.o2.Domain.Utility.ExtensionsUtil.animFadeOut
import com.ncs.o2.Domain.Utility.ExtensionsUtil.animFadein
import com.ncs.o2.Domain.Utility.ExtensionsUtil.bounce
import com.ncs.o2.Domain.Utility.ExtensionsUtil.gone
import com.ncs.o2.Domain.Utility.ExtensionsUtil.setOnClickSingleTimeBounceListener
import com.ncs.o2.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.o2.Domain.Utility.ExtensionsUtil.visible
import com.ncs.o2.Domain.Utility.GlobalUtils
import com.ncs.versa.Constants.MessageTypes
import com.shockwave.pdfium.PdfiumCore
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.data.DataUriSchemeHandler
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.datafaker.Faker
import retrofit2.Response
import retrofit2.create


@AndroidEntryPoint
class ChatFragment : Fragment(), ChatAdapter.OnClickCallback, OptionsBottomSheet.recievePDFCallback, ChatAdapter.PdfHandlerCallback {

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var binding : FragmentChatBinding
    private lateinit var adapter : ChatAdapter
    private lateinit var chatRecyclerView : RecyclerView
    private lateinit var markwon : Markwon
    private val TAG = "ChatFragment"
    val elements : GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireContext())
    }

    lateinit var answerLiveData : LiveData<ServerResult<Answer?>>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()

    }

    val textWatcher : TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (input.toString().isNotBlank()){
                binding.inputBox.btnSend.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
                binding.inputBox.btnSend.isEnabled = true
                binding.inputBox.btnSend.isClickable = true

            }else {
                binding.inputBox.btnSend.setColorFilter(ContextCompat.getColor(requireContext(), R.color.account), PorterDuff.Mode.SRC_IN)
                binding.inputBox.btnSend.isEnabled = false
                binding.inputBox.btnSend.isClickable = false

            }
        }

        override fun afterTextChanged(input: Editable?) {

        }

    }

    private fun setUpViews() {
        initViews()
        setUpMarkdown()
        setUpViewClicks()
        startOpeningAnim()
        setUpInputBox()
        setUpRecyclerView()
        setLivedata()
    }

    interface ViewInitCallback{
        fun show_bottombar(boolean: Boolean)
    }

    private fun initViews() {
        binding.btnScrolldown.gone()
        binding.actionbar.typing.gone()
        binding.inputBox.btnSend.isEnabled = false
        binding.inputBox.btnSend.isClickable = false
        (activity as? ViewInitCallback )?.show_bottombar(false)
        navController = findNavController()

    }

    private fun setUpMarkdown() {

        markwon =  Markwon.builder(requireContext())
            .usePlugin(ImagesPlugin.create())
            .usePlugin(GlideImagesPlugin.create(requireContext()))
            .usePlugin(TablePlugin.create(requireContext()))
            .usePlugin(TaskListPlugin.create(requireContext()))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configure(registry: MarkwonPlugin.Registry) {
                    registry.require(ImagesPlugin::class.java) { imagesPlugin ->
                        imagesPlugin.addSchemeHandler(DataUriSchemeHandler.create())
                    }
                }
            })
            .build()
    }

    private fun setUpRecyclerView() {

        chatRecyclerView = binding.chatRecyclerview
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        chatRecyclerView.layoutManager = layoutManager
        val msgList : ArrayList<Message> = arrayListOf(
            Message("Hello!, how can I help you ?", 0))


        adapter = ChatAdapter(msgList,requireContext(),markwon,this,this)
        chatRecyclerView.adapter = adapter
        Handler(Looper.getMainLooper()).postDelayed({
            binding.chatRecyclerview.visible()
            binding.chatRecyclerview.animFadein(requireContext(),500)
            binding.cover.gone()
        },6000)


        var checkScrollingUp = false

        chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scrolling up
                    if (checkScrollingUp) {
                        binding.btnScrolldown.animFadeOut(requireContext(),100)
                        binding.btnScrolldown.gone()
                        binding.actionbar.main.elevation = 5f
                        checkScrollingUp = false
                    }
                } else {
                    // User scrolls down
                    if (!checkScrollingUp) {
                        binding.btnScrolldown.animFadein(requireContext(),100)
                        binding.btnScrolldown.visible()
                        binding.actionbar.main.elevation = 0f
                        checkScrollingUp = true
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        binding.btnScrolldown.setOnClickThrottleBounceListener (600){
            chatRecyclerView.smoothScrollToPosition(adapter.msgList.size-1)
        }

    }

    private lateinit var navController: NavController

    private fun setUpViewClicks() {


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                (activity as? ViewInitCallback )?.show_bottombar(true)
                navController.navigate(R.id.action_chatFragment_to_homeFragment)
            }

        })

        binding.inputBox.btnVoice.setOnClickSingleTimeBounceListener {

        }

        binding.actionbar.btnBack.setOnClickSingleTimeBounceListener {

            (activity as? ViewInitCallback )?.show_bottombar(true)
            navController.navigate(R.id.action_chatFragment_to_homeFragment)

        }


        binding.inputBox.btnSend.setOnClickThrottleBounceListener(600) {
            val msg = binding.inputBox.editboxMessage.text.toString()
            addMessage(Message(msg, MessageTypes.MESSAGE_TYPE_USER))
            CoroutineScope(Dispatchers.Main).launch {
                val configMsg = msg+" "+ApiEndpoints.CONFIGURATION
                viewModel.getAnswer(configMsg)
            }

        }


    }

    private fun setLivedata() {

        viewModel.answerLiveData.observe(requireActivity()){result ->
            when (result){
                is ServerResult.Failure -> {
                    adapter.showTyping(false)
                    addMessage(Message("Hrrrr. some problems came between you and me :'( \n\n#### ${result.error}", MessageTypes.MESSAGE_TYPE_BOT))
                }
                ServerResult.Progress -> {
                    adapter.showTyping(true)
                    chatRecyclerView.smoothScrollToPosition(adapter.msgList.size-1)
                }
                is ServerResult.Success -> {
                    adapter.showTyping(false)
                    val answer : String = result.data?.answer.toString()
                    addMessage(Message(answer,MessageTypes.MESSAGE_TYPE_BOT))
                }
            }
        }
    }

//    private fun typing(show:Boolean){
//        if (show){
//            binding.actionbar.titleTv.text = "Typing..."
//        }else{
//            binding.actionbar.titleTv.text = "Vedika"
//
//        }
//    }

    private fun addMessage(msg: Message){
        if (msg.msgType == MessageTypes.MESSAGE_TYPE_USER || msg.msgType == MessageTypes.MESSAGE_TYPE_BOT ){
        adapter.appendMessage(Message(msg.message,msg.msgType))
        chatRecyclerView.smoothScrollToPosition(adapter.msgList.size-1)
        binding.inputBox.editboxMessage.text.clear()
        }
        else {

            adapter.appendMessage(msg)
            chatRecyclerView.smoothScrollToPosition(adapter.msgList.size-1)
            binding.inputBox.editboxMessage.text.clear()

        }
    }

    private fun setUpInputBox() {
        binding.inputBox.editboxMessage.addTextChangedListener(textWatcher)
    }

    private fun startOpeningAnim() {


        binding.vedikaTitle.animFadein(requireContext(),1500)
        binding.lottieProgressInclude.progressbarBlock.animFadein(requireContext(),3000)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.vedikaTitle.animFadeOut(requireContext(),200)
            binding.vedikaTitle.gone()
            binding.lottieProgressInclude.progressbarBlock.animFadeOut(requireContext(),250)
            binding.lottieProgressInclude.progressLayout.gone()

            binding.actionbar.titleTv.animFadein(requireContext(),1500)
            binding.actionbar.titleTv.visible()

            setMarginGlobeMargin()

        },2500)

    }

    private fun setMarginGlobeMargin(){

        Handler(Looper.getMainLooper()).postDelayed({
            val marginStart = resources.getDimension(R.dimen.margin_start_80)
            val params = binding.lottieProgressInclude.progressLayout.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = marginStart.toInt()
            binding.lottieProgressInclude.progressLayout.layoutParams = params

            Handler(Looper.getMainLooper()).postDelayed({
                binding.lottieProgressInclude.progressLayout.visible()
                binding.lottieProgressInclude.progressbarBlock.bounce(requireContext(),200)
                binding.lottieProgressInclude.progressLayout.scaleX = 1.8f
                binding.lottieProgressInclude.progressLayout.scaleY = 1.8f
            },500)

            Handler(Looper.getMainLooper()).postDelayed({
                binding.comment.animFadein(requireContext(),2000)
                binding.comment.visible()
            },1000)
        },500)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }



    ////////////////////// CALLBACKS ////////////////////////

    override fun onLongClick(msg: Message) {
        val optionBottomSheet :OptionsBottomSheet = OptionsBottomSheet(msg,this)
        optionBottomSheet.show(childFragmentManager,"option bottomsheet")

    }

    override fun copyClick(msg: String) {
        val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", msg)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(activity?.applicationContext, "Text copied", Toast.LENGTH_LONG).show()
    }

    override fun sendThisPdf(fileName: String ,uri: Uri) {

        val pdfName = "$fileName.pdf"
        val bitmap = generateImageFromPdf(uri)
        val msg  = PdfMessage("Here is your exported pdf", MessageTypes.MESSAGE_TYPE_BOT_PDF, uri,bitmap!!,pdfName)
        addMessage(msg)
    }

    private fun generateImageFromPdf(pdfUri: Uri): Bitmap? {

        val pageNumber = 0
        val pdfiumCore = PdfiumCore(requireContext())
        try {
            val fd = activity?.contentResolver?.openFileDescriptor(pdfUri, "r")
            val pdfDocument = pdfiumCore.newDocument(fd)
            pdfiumCore.openPage(pdfDocument, pageNumber)
            val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
            val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
            pdfiumCore.closeDocument(pdfDocument) // important!
            return bmp

        } catch (e: Exception) {
            Log.d("Bitmap error", "generateImageFromPdf: "+e.stackTraceToString())
            return null
        }}

    override fun openPdf(uri: Uri) {

        val contentUri = FileProvider.getUriForFile(requireContext(),"com.ncs.nyayvedika.provider",uri.toFile())
        val pdfOpenIntent = Intent(Intent.ACTION_VIEW)

        pdfOpenIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pdfOpenIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, contentUri)
        }
        pdfOpenIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        pdfOpenIntent.setDataAndType(contentUri, "application/pdf")
        try {
            requireActivity().startActivity(pdfOpenIntent)
        } catch (e: ActivityNotFoundException) {
            Log.d("Opening fault", "openPdf: ${e.stackTrace} ")
        }
    }

    override fun sendPdf(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/pdf"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri) // Replace with the Uri of your PDF file
        startActivity(Intent.createChooser(shareIntent, "Share PDF using..."))
    }


    ////////////////////// CALLBACKS ////////////////////////


}





