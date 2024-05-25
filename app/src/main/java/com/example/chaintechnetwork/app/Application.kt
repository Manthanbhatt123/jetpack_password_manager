package com.example.chaintechnetwork.app

import android.app.Application
import androidx.room.Room
import com.example.chaintechnetwork.db.AccountDatabase

class MyApp : Application() {

    companion object {
        lateinit var database: AccountDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AccountDatabase::class.java,
            "account_db"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }
}