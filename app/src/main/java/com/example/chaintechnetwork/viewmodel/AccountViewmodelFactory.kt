package com.example.chaintechnetwork.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AccountViewModelFactory (private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AccountViewModel::class.java)){
            AccountViewModel(application) as T
        }else{
            throw IllegalArgumentException("ViewModel not provided")
        }
    }
}


