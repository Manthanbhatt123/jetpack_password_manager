package com.example.chaintechnetwork.repository

import com.example.chaintechnetwork.db.AccountDao
import com.example.chaintechnetwork.db.AccountEntity

class AccountRepo(private val accountDao: AccountDao) {

    fun getAllData(): List<AccountEntity> {
       return accountDao.getAllAccounts()
    }

    suspend fun insert(account: AccountEntity) {
        accountDao.insert(account)
    }
    suspend fun update(password:String,accountId:String) {
        accountDao.update(password, accountId)
    }

    fun delete(account: AccountEntity) {
        accountDao.delete(account)
    }
}