package com.example.chaintechnetwork.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chaintechnetwork.utils.TypeConverterADTFiles

@Database(entities = [AccountEntity::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverterADTFiles::class)
abstract class AccountDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}