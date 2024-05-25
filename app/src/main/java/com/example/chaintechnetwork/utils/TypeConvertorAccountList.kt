package com.example.chaintechnetwork.utils

import androidx.room.TypeConverter
import com.example.chaintechnetwork.db.AccountEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections

class TypeConverterADTFiles {

    private var gson = Gson()

    @TypeConverter
    fun stringToMenuList(data: String?): ArrayList<AccountEntity> {
        if (data == null) {
            return Collections.emptyList<AccountEntity>() as ArrayList<AccountEntity>
        }

        return gson.fromJson(data, object : TypeToken<ArrayList<AccountEntity>>() {}.type)
    }

    @TypeConverter
    fun menuListToString(someObjects: ArrayList<AccountEntity>): String {
        return gson.toJson(someObjects)
    }
}