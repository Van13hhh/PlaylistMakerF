package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.sharing.model.SupportInfo
import com.example.playlistmaker.ui.common.SingleLiveEvent

class SettingsViewModel(application: Application) : AndroidViewModel(application){

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SettingsViewModel(app)
            }
        }
    }

    private val settingsInteractor = Creator.getSettingsInteractor(application)
    private val sharingInteractor = Creator.getSharingInteractor(application)

    private val _isDarkTheme = MutableLiveData<Boolean>()
    fun observeTheme(): LiveData<Boolean> = _isDarkTheme

    private val _navigation = SingleLiveEvent<NavigationAction>()
    fun observeNavigation(): LiveData<NavigationAction> = _navigation

    init {
        _isDarkTheme.value = settingsInteractor.isDarkTheme()
    }

    fun switchTheme(isDark: Boolean) {
        settingsInteractor.applyTheme(isDark)
        _isDarkTheme.value = isDark
    }

    fun onAgreementClick(){
        val url =  sharingInteractor.getAgreementUrl()
        _navigation.postValue(NavigationAction.Agreement(url))
    }

    fun onSupportClick(){
        val supportInfo = sharingInteractor.getSupportInfo()
        _navigation.postValue(NavigationAction.Support(supportInfo))
    }

    fun onShareClick() {
        val url =  sharingInteractor.getShareUrl()
        _navigation.postValue(NavigationAction.Share(url))
    }

    sealed interface NavigationAction {
        data class Share(val url: String) : NavigationAction
        data class Support(val info: SupportInfo) : NavigationAction
        data class Agreement(val url: String) : NavigationAction
    }

}