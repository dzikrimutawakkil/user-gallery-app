package com.techtestuserapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.techtestuserapp.data.local.dao.MediaDao
import com.techtestuserapp.data.local.dao.UserDao
import com.techtestuserapp.data.local.entity.MediaEntity
import com.techtestuserapp.data.local.entity.UserEntity

@Database(entities = [UserEntity::class, MediaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mediaDao(): MediaDao
}