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
import androidx.annotation.Dimension
import androidx.compose.ui.unit.DpSize
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.databinding.FragmentChatBinding
import com.ncs.o2.Domain.Utility.ExtensionsUtil.animFadeOut
import com.ncs.o2.Domain.Utility.ExtensionsUtil.animFadein
import com.ncs.o2.Domain.Utility.ExtensionsUtil.animSlideDown
import com.ncs.o2.Domain.Utility.ExtensionsUtil.animSlideRight
import com.ncs.o2.Domain.Utility.ExtensionsUtil.bounce
import com.ncs.o2.Domain.Utility.ExtensionsUtil.gone
import com.ncs.o2.Domain.Utility.ExtensionsUtil.rotateInfinity
import com.ncs.o2.Domain.Utility.ExtensionsUtil.setOnClickSingleTimeBounceListener
import com.ncs.o2.Domain.Utility.ExtensionsUtil.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import kotlin.concurrent.schedule


@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding : FragmentChatBinding

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
            }else {
                binding.inputBox.btnSend.setColorFilter(ContextCompat.getColor(requireContext(), R.color.account), PorterDuff.Mode.SRC_IN)
            }
        }

        override fun afterTextChanged(input: Editable?) {
        }

    }

    private fun setUpViews() {
        startOpeningAnim()
        setUpInputBox()

        binding.inputBox.btnVoice.setOnClickSingleTimeBounceListener {

        }

        binding.inputBox.btnSend.setOnClickSingleTimeBounceListener {

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
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

    }

}