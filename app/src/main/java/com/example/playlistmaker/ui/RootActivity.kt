package com.example.playlistmaker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class RootActivity : AppCompatActivity() {
    private var _binding: ActivityRootBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        // в зависимости от фрагмента
        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            // покрасим тулбар
            when (destination.id) {
                R.id.playlistFragment -> {
                    toolbar.setNavigationIcon(R.drawable.arrow_back_playlist)
                    toolbar.setBackgroundColor(getColor(R.color.light_gray))
                    //toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.teal_200))
                }

                else -> {
                    toolbar.setNavigationIcon(R.drawable.arrow_back)
                    toolbar.setBackgroundColor(getColor(R.color.main_background_color))
                    //toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.main_text_color))
                }
            }
            this.setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                this.onBackPressedDispatcher.onBackPressed()
            }

            // установим тайтл и кнопку назад
            this.supportActionBar?.title = "${destination.label}"
            this.supportActionBar?.setDisplayHomeAsUpEnabled(controller.backQueue.size > 2)

            // видимость нижней навигации
            bottomNavigationView.isVisible = destination.id !in listOf(
                R.id.playlistAddFragment,
                R.id.playlistFragment
            )

        }


    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}