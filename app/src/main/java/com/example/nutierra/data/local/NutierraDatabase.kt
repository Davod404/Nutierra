
package com.example.nutierra.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutierra.data.local.dao.CarritoDao
import com.example.nutierra.data.local.dao.PedidoDao
import com.example.nutierra.data.local.dao.ProductoDao
import com.example.nutierra.data.local.dao.UsuarioDao
import com.example.nutierra.data.local.entity.CarritoItemEntity
import com.example.nutierra.data.local.entity.PedidoDetalleEntity
import com.example.nutierra.data.local.entity.PedidoEntity
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.data.local.entity.UsuarioEntity

@Database(
    entities = [
        ProductoEntity::class,
        UsuarioEntity::class,
        CarritoItemEntity::class,
        PedidoEntity::class,
        PedidoDetalleEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class NutierraDatabase : RoomDatabase() {

    // Acceso a las operaciones de productos
    abstract fun productoDao(): ProductoDao

    // Acceso a las operaciones de usuarios
    abstract fun usuarioDao(): UsuarioDao

    // Acceso al carrito de compras
    abstract fun carritoDao(): CarritoDao

    // Acceso a los pedidos y su detalle
    abstract fun pedidoDao(): PedidoDao

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