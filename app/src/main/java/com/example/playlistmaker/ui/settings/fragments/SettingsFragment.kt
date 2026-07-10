package com.example.playlistmaker.ui.settings.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()
        setupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        binding.themeSwitcher.setOnClickListener {
            viewModel.switchTheme(binding.themeSwitcher.isChecked)
        }

        binding.btnUserAgreement.setOnClickListener { viewModel.onAgreementClick() }
        binding.settingsFrameL4.setOnClickListener { viewModel.onAgreementClick() }
        binding.settingsFrameL3.setOnClickListener { viewModel.onSupportClick() }
        binding.btnSupport.setOnClickListener { viewModel.onSupportClick() }
        binding.settingsFrameL2.setOnClickListener { viewModel.onShareClick() }
        binding.btnShare.setOnClickListener { viewModel.onShareClick() }
    }

    private fun observeViewModel() {
        viewModel.observeTheme().observe(viewLifecycleOwner) { isDark ->
            binding.themeSwitcher.isChecked = isDark
        }

        viewModel.observeNavigation().observe(viewLifecycleOwner) { action ->
            when (action) {
                is SettingsViewModel.NavigationAction.Agreement -> {
                    val intent = Intent(Intent.ACTION_VIEW, action.url.toUri())
                    startActivity(intent)
                }

                is SettingsViewModel.NavigationAction.Share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, action.url)
                    }
                    startActivity(intent)
                }

                is SettingsViewModel.NavigationAction.Support -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:".toUri()
                        putExtra(Intent.EXTRA_EMAIL, action.info.mail)
                        putExtra(Intent.EXTRA_SUBJECT, action.info.theme)
                        putExtra(Intent.EXTRA_TEXT, action.info.message)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}