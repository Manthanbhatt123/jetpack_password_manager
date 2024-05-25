package com.example.chaintechnetwork.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_table")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val accountId: String,
    val password: String

)
