package com.example.chaintechnetwork.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chaintechnetwork.app.MyApp
import com.example.chaintechnetwork.db.AccountEntity
import com.example.chaintechnetwork.repository.AccountRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AccountRepo

    private val _allAccounts = MutableLiveData<List<AccountEntity>>()
    val allAccounts: LiveData<List<AccountEntity>> get() = _allAccounts


    init {
        val dao = MyApp.database.accountDao()
        repository = AccountRepo(dao)
        loadAllAccounts()
    }

    private fun loadAllAccounts(){
        viewModelScope.launch(Dispatchers.IO) {
            _allAccounts.postValue(repository.getAllData())
        }
    }
    fun insert(account: AccountEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(account)
            loadAllAccounts()
        }
    }

    fun update(password:String,accountId:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(password,accountId)
            loadAllAccounts()
        }
    }

    fun deleteAccount(account: AccountEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(account)
            loadAllAccounts()
        }
    }
}