package com.example.chaintechnetwork.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chaintechnetwork.accountView

class AccountViewModelFactory (private val application: Application,private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AccountViewModel::class.java)){
            AccountViewModel(application, context) as T
        }else{
            throw IllegalArgumentException("ViewModel not provided")
        }
    }
}


