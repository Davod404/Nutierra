
package com.example.nutierra.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutierra.data.local.dao.ProductoDao
import com.example.nutierra.data.local.dao.UsuarioDao
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.data.local.entity.UsuarioEntity

@Database(
    entities = [ProductoEntity::class, UsuarioEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NutierraDatabase : RoomDatabase() {

    // Acceso a las operaciones de productos
    abstract fun productoDao(): ProductoDao

    // Acceso a las operaciones de usuarios
    abstract fun usuarioDao(): UsuarioDao

    companion object {

        @Volatile
        private var INSTANCE: NutierraDatabase? = null

        fun getDatabase(context: Context): NutierraDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutierraDatabase::class.java,
                    "nutierra_database"
                )
                    // Proyecto académico: si cambia el esquema se recrea la base
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}