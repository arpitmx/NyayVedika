package com.ncs.nyayvedika.UI.Chat

import android.graphics.PorterDuff
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.UI.Chat.Adapters.ChatAdapter
import com.ncs.nyayvedika.UI.Chat.BottomSheets.OptionsBottomSheet
import com.ncs.nyayvedika.databinding.FragmentChatBinding
import com.ncs.o2.Domain.Models.Message
import com.ncs.o2.Domain.Utility.ExtensionsUtil.animFadeOut
import com.ncs.o2.Domain.Utility.ExtensionsUtil.animFadein
import com.ncs.o2.Domain.Utility.ExtensionsUtil.bounce
import com.ncs.o2.Domain.Utility.ExtensionsUtil.gone
import com.ncs.o2.Domain.Utility.ExtensionsUtil.setOnClickSingleTimeBounceListener
import com.ncs.o2.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.o2.Domain.Utility.ExtensionsUtil.visible
import com.ncs.versa.Constants.MessageTypes
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
import net.datafaker.Faker
import org.yaml.snakeyaml.error.Mark


@AndroidEntryPoint
class ChatFragment : Fragment(), ChatAdapter.OnLongClickCallback {

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding : FragmentChatBinding
    private lateinit var adapter : ChatAdapter
    private lateinit var chatRecyclerView : RecyclerView
    private lateinit var markwon : Markwon


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
        setUpMarkdown()
        setUpViewClicks()
        //startOpeningAnim()
        setUpInputBox()
        setUpRecyclerView()


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
        val msgList : ArrayList<Message> = arrayListOf(Message("Hello how can I help you ?",0),
        Message(Faker().shakespeare().asYouLikeItQuote(),0),
            Message("Fuck you bitch?!",1),
            Message("### What is the punishment for bribery under IPC?\n" +
                    "\n" +
                    "In India, the punishment for bribery is primarily governed by the Prevention of Corruption Act, 1988, rather than the Indian Penal Code (IPC). The Indian Penal Code deals with various offenses, but bribery is more specifically addressed under the Prevention of Corruption Act. Here is a general overview of the punishment for bribery under this act:\n" +
                    "\n" +
                    "### Punishment for Bribery:\n" +
                    "\n" +
                    "- **Section 7**: This section deals with taking gratification (bribe) to influence a public servant. Both the bribe-giver and bribe-taker can be punished. The punishment may include imprisonment for a term which shall not be less than six months but which may extend to five years, along with a fine.\n" +
                    "\n" +
                    "- **Section 8**: This section deals with taking gratification (bribe) by a public servant. A public servant who accepts a bribe can be punished with imprisonment for a term which shall not be less than three years but which may extend to seven years, along with a fine.\n" +
                    "\n" +
                    "- **Section 9**: This section deals with taking gratification (bribe) for the exercise of personal influence over a public servant. The punishment for this offense includes imprisonment for a term which may extend to six months, or with a fine, or with both.\n" +
                    "\n" +
                    "- **Section 10**: This section deals with the abetment of offenses defined in Section 8 or Section 9. The punishment for abetment may include imprisonment for a term which shall not be less than six months but which may extend to five years, along with a fine.\n" +
                    "\n" +
                    "- **Section 11**: This section deals with the punishment for offenses not covered by Sections 7, 8, 9, or 10. The punishment for such offenses may include imprisonment for a term which shall not be less than six months but which may extend to five years, along with a fine.\n" +
                    "\n" +
                    "Please note that these are general provisions, and the specific punishment for bribery may vary based on the nature of the offense and other factors. Additionally, the legal system can evolve, so it's advisable to consult the latest legal sources or seek legal counsel for the most up-to-date information on bribery punishments in India.\n",0),
            Message("We can also handle both the EditTexts separately. But in this case, to reduce the lines of code, the callback listener TextWatcher is implemented, and the callback listener object is passed to the addTextChangedListener method for each of the edit text.\n" + "\n" +
                    "Invoke the following code inside the MainActivity.java file comments are added for better understanding.",0),
                    Message("If there is an application containing a login form to be filled by the user, the login button should be disabled (meaning: it shouldn’t be clickable). When the user enters the credentials of the form the button should be enabled to click for the user. So in this article, we are implementing a TextWatcher to the EditText field. Have a look at the following image to get an idea of what is the TextWatcher and how that may increase user interactivity. Note that we are going to implement this project using the Java language. ",1),
            Message(Faker().famousLastWords().lastWords(),0),
            Message(Faker().shakespeare().hamletQuote(),0),
            Message(Faker().shakespeare().kingRichardIIIQuote(),0),
            Message(Faker().shakespeare().romeoAndJulietQuote(),0),
        )


        adapter = ChatAdapter(msgList,requireContext(),markwon,this)
        chatRecyclerView.adapter = adapter
        Handler(Looper.getMainLooper()).postDelayed({
            binding.chatRecyclerview.visible()
            binding.chatRecyclerview.animFadein(requireContext(),500)
            binding.cover.gone()
        },2000)


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

    private fun setUpViewClicks() {
        binding.btnScrolldown.gone()
        binding.actionbar.typing.gone()
        binding.inputBox.btnSend.isEnabled = false
        binding.inputBox.btnSend.isClickable = false

        binding.inputBox.btnVoice.setOnClickSingleTimeBounceListener {

        }

        binding.inputBox.btnSend.setOnClickListener {
            val msg = binding.inputBox.editboxMessage.toString()
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            adapter.appendMessage(Message(msg, 1))
            binding.inputBox.editboxMessage.text.clear()
        }

        binding.inputBox.btnSend.setOnClickThrottleBounceListener(600) {
            val msg = binding.inputBox.editboxMessage.text.toString()
            addMessage(msg, MessageTypes.MESSAGE_TYPE_USER)
            typing(true)
            Handler(Looper.getMainLooper()).postDelayed({
                                addMessage(Faker().famousLastWords().lastWords().toString(), MessageTypes.MESSAGE_TYPE_BOT)
                typing(false)
            },5000)
        }


    }


    private fun typing(show:Boolean){
        if (show){
            binding.actionbar.titleTv.gone()
            binding.actionbar.typing.visible()
        }else{
            binding.actionbar.typing.gone()
            binding.actionbar.titleTv.visible()

        }
    }

    private fun addMessage(msg : String, type:Int){
        adapter.appendMessage(Message(msg.trim(),type))
        chatRecyclerView.smoothScrollToPosition(adapter.msgList.size-1)
        binding.inputBox.editboxMessage.text.clear()
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
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
    }

    override fun onLongClick(msg: Message) {
        val optionBottomSheet :OptionsBottomSheet = OptionsBottomSheet(msg)
        optionBottomSheet.show(childFragmentManager,"option bottomsheet")

    }


}