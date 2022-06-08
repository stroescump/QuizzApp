package com.irinamihaila.quizzapp.ui.dashboard

import androidx.lifecycle.ViewModel
import com.irinamihaila.quizzapp.models.UserType
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils

class DashboardViewModel(val sharedPrefs: SharedPrefsUtils) : ViewModel() {
    val userType: UserType? by lazy {
        sharedPrefs.getUserType()?.let { return@let UserType.valueOf(it) }
    }

}