
package com.example.nutierra.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nutierra.data.local.dao.CarritoDao
import com.example.nutierra.data.local.dao.PedidoDao
import com.example.nutierra.data.local.dao.ProductoDao
import com.example.nutierra.data.local.dao.UsuarioDao
import com.example.nutierra.data.local.entity.CarritoItemEntity
import com.example.nutierra.data.local.entity.PedidoDetalleEntity
import com.example.nutierra.data.local.entity.PedidoEntity
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.data.local.entity.UsuarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                .addCallback(object : RoomDatabase.Callback() {
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
