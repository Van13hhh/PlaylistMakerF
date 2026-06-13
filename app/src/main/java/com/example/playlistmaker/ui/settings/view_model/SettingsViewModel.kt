package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.SupportInfo
import com.example.playlistmaker.ui.common.SingleLiveEvent

class SettingsViewModel(private val settingsInteractor: SettingInteractor, private val sharingInteractor: SharingInteractor) : ViewModel(){

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