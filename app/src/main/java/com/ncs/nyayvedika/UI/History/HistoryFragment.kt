package com.ncs.nyayvedika.UI.History

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.UI.History.Adapters.HistoryAdapter
import com.ncs.nyayvedika.databinding.FragmentHistoryBinding
import com.ncs.o2.Domain.Utility.ExtensionsUtil.setOnClickSingleTimeBounceListener
import net.datafaker.Faker

class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var viewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentHistoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        setUpViews()
    }

    private fun setUpViews() {
        setUpClicks()
       setUpHistory()
    }

    private fun setUpClicks() {
        binding.btnChat.setOnClickSingleTimeBounceListener {
            findNavController().navigate(R.id.action_historyFragment_to_chat_fragment)
        }
    }

    private fun setUpHistory() {
        val listView = binding.pastList
        val items:List<String> = listOf(Faker().famousLastWords().lastWords(), Faker().famousLastWords().lastWords(), Faker().famousLastWords().lastWords(), Faker().famousLastWords().lastWords(),Faker().famousLastWords().lastWords()) // Replace with your data
        val adapter = HistoryAdapter(requireContext(), items)
        listView.adapter = adapter
    }

}