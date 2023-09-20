package com.ncs.nyayvedika.UI.MainHolder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.UI.Chat.ChatFragment
import com.ncs.nyayvedika.databinding.ActivityMainBinding
import com.ncs.o2.Domain.Utility.ExtensionsUtil.visibleIf
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ChatFragment.ViewInitCallback {

    val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        setUpBottomNavigation()
     }

    private fun setUpBottomNavigation() {

        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNav = binding.bottomNav
        bottomNav.setupWithNavController(navController)
    }

    override fun show_bottombar(boolean: Boolean) {
        binding.bottomNav.visibleIf(boolean)
    }

}