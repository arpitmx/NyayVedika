package com.ncs.nyayvedika.UI.Home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.databinding.FragmentHomeBinding
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import org.imaginativeworld.whynotimagecarousel.model.CarouselType

class HomeFragment : Fragment() {


    private val viewmodel : HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    companion object {
        private val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= FragmentHomeBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViews()
    }


    private fun setUpViews() {

        setUpCarousel()

    }

    private fun setUpCarousel() {
        binding.carousel.registerLifecycle(this)
        binding.carousel.carouselType= CarouselType.SHOWCASE
        val list : List<CarouselItem> = listOf(CarouselItem(R.drawable.c1),CarouselItem(R.drawable.c2),CarouselItem(R.drawable.c3))
        binding.carousel.setData(list)
    }


}