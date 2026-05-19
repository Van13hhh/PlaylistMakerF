package com.example.playlistmaker.ui.settings.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.llSettings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViewModel()
        observeViewModel()
        setupUI()
    }

    private fun setupUI() {
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(
                checked
            )
        }
        binding.backToMainMenu.setOnClickListener { finish() }
        binding.btnUserAgreement.setOnClickListener { viewModel.onAgreementClick() }
        binding.settingsFrameL4.setOnClickListener { viewModel.onAgreementClick() }
        binding.settingsFrameL3.setOnClickListener { viewModel.onSupportClick() }
        binding.btnSupport.setOnClickListener { viewModel.onSupportClick() }
        binding.settingsFrameL2.setOnClickListener { viewModel.onShareClick() }
        binding.btnShare.setOnClickListener { viewModel.onShareClick() }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory()
        )[SettingsViewModel::class.java]
    }

    private fun observeViewModel() {
        viewModel.observeTheme().observe(this) {
            binding.themeSwitcher.isChecked = it
        }

        viewModel.observeNavigation().observe(this) {
            when (it) {
                is SettingsViewModel.NavigationAction.Agreement -> {
                    val intent = Intent(Intent.ACTION_VIEW, it.url.toUri())
                    startActivity(intent)
                }

                is SettingsViewModel.NavigationAction.Share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, it.url)
                    }
                    startActivity(intent)
                }

                is SettingsViewModel.NavigationAction.Support -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:".toUri()
                        putExtra(Intent.EXTRA_EMAIL, it.info.mail)
                        putExtra(Intent.EXTRA_SUBJECT, it.info.theme)
                        putExtra(Intent.EXTRA_TEXT, it.info.message)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}