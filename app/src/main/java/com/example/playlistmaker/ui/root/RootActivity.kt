package com.example.playlistmaker.ui.root

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootConstraint) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            view.updatePadding(
                top = statusBar.top,
                bottom = navBar.bottom
            )
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.audioPlayerFragment -> {
                    bottomNavigationView.isVisible = false
                    binding.divider.isVisible = false
                }

                R.id.playlistCreatingFragment -> {
                    bottomNavigationView.isVisible = false
                    binding.divider.isVisible = false
                }

                R.id.playlistFragment -> {
                    bottomNavigationView.isVisible = false
                    binding.divider.isVisible = false
                }

                R.id.playlistEditFragment -> {
                    bottomNavigationView.isVisible = false
                    binding.divider.isVisible = false
                }

                else -> {
                    bottomNavigationView.isVisible = true
                    binding.divider.isVisible = true
                }
            }
        }
    }
}