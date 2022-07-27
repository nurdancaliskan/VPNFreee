package com.liberty.apps.studio.libertyvpn.view.activites

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.liberty.apps.studio.libertyvpn.R
import com.liberty.apps.studio.libertyvpn.databinding.ActivityControllerBinding
import com.liberty.apps.studio.libertyvpn.model.Server

class ControllerActivity : AppCompatActivity(),ChangeServerBottomSheetDialog.OnInputListener {

    private lateinit var binding: ActivityControllerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControllerBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // setupNavigation()
    }

    /*
    * Controller activity control the Navigation behaviour of the Fragments
    * */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.main_nav_menu)
        val menu = popupMenu.menu
       binding.mainBottomNav.setupWithNavController(menu, navHostFragment.navController)
    }

    override fun sendServer(server: Server?) {
        val navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        val bundle = Bundle()
        bundle.putParcelable("serverextra", server)
        navController.navigate(R.id.homeFragment, bundle)
    }


    }
