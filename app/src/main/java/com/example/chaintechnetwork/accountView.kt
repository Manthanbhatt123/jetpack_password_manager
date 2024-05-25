package com.example.chaintechnetwork

import com.example.chaintechnetwork.db.AccountEntity

interface accountView {
    fun getAccount(account: AccountEntity)
}