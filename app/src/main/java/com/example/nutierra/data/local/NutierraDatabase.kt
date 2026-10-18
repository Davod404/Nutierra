NutierraDatabase.kt

package com.example.nutierra.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutierra.data.local.dao.ProductoDao
import com.example.nutierra.data.local.entity.ProductoEntity

@Database(
    entities = [ProductoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NutierraDatabase : RoomDatabase() {

    // Acceso a las operaciones de productos
    abstract fun productoDao(): ProductoDao

    companion object {

        @Volatile
        private var INSTANCE: NutierraDatabase? = null

        fun getDatabase(context: Context): NutierraDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutierraDatabase::class.java,
                    "nutierra_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}