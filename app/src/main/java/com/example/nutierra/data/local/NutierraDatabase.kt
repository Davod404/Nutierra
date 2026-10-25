package com.example.nutierra.data.local

import com.example.nutierra.data.local.entity.PedidoDetalleEntity
import com.example.nutierra.data.local.entity.CarritoItemEntity
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.data.local.entity.UsuarioEntity
import com.example.nutierra.data.local.entity.PedidoEntity
import com.example.nutierra.data.local.dao.ProductoDao
import com.example.nutierra.data.local.dao.UsuarioDao
import com.example.nutierra.data.local.dao.CarritoDao
import com.example.nutierra.data.local.dao.PedidoDao
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context

@Database(
    entities = [
        ProductoEntity::class,
        UsuarioEntity::class,
        CarritoItemEntity::class,
        PedidoEntity::class,
        PedidoDetalleEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class NutierraDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun carritoDao(): CarritoDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: NutierraDatabase? = null

        fun getDatabase(context: Context): NutierraDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NutierraDatabase::class.java,
                    "nutierra_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.productoDao()?.insertarProductos(
                                DatosIniciales.productos
                            )
                        }
                    }
                })
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}