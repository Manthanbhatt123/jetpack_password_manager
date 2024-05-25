package com.example.chaintechnetwork.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query


@Dao
interface AccountDao {

    @Query("SELECT * FROM account_table")
    fun getAllAccounts(): List<AccountEntity>

    @Insert(onConflict = REPLACE)
    suspend fun insert(account: AccountEntity)

    @Query("update account_table set password =:newPassword where accountId=:accountId ")
    suspend fun update(newPassword:String,accountId:String)
    @Delete
    fun delete(account: AccountEntity)
}