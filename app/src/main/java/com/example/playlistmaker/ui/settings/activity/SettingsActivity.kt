package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.common.Helper
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper.setToolbar(this)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        // подпишемся на изменение темы [извне]
        viewModel.observeState().observe(this) {
            setDarkModeChecked(it)
        }

        // обработчики кнопок через modelView
        with(binding) {
            themeSwitcher.setOnCheckedChangeListener { _, checked ->
                viewModel.switchDarkTheme(checked)
            }
            actionShare.setOnClickListener {
                viewModel.shareApp()
            }
            actionSupport.setOnClickListener {
                viewModel.messageSupport()
            }
            actionAgreement.setOnClickListener {
                viewModel.showAgreement()
            }
        }

    }

    private fun setDarkModeChecked(darkModeOn: Boolean) {
        binding.themeSwitcher.isChecked = darkModeOn
    }
}