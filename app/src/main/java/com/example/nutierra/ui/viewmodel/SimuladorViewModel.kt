package com.example.nutierra.ui.viewmodel

import com.example.nutierra.data.local.entity.PedidoDetalleEntity
import com.example.nutierra.data.local.entity.EstadosPedido
import com.example.nutierra.data.local.entity.PedidoEntity
import com.example.nutierra.data.local.entity.Roles
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.session.SesionManager
import com.example.nutierra.notifications.NotificacionesHelper
import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutierra.data.local.entity.NotificacionEntity
import com.example.nutierra.data.local.entity.TipoNotificacion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.random.Random

class SimuladorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NutierraDatabase.getDatabase(application)
    private val pedidoDao = db.pedidoDao()
    private val usuarioDao = db.usuarioDao()
    private val productoDao = db.productoDao()
    private val notificacionDao = db.notificacionDao()

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private var iniciado = false

    fun iniciar() {
        if (iniciado) return
        iniciado = true
        viewModelScope.launch {
            while (true) {
                delay(30_000.milliseconds)

                if (Random.nextFloat() > 0.3f) continue

                val usuario = SesionManager.usuarioActual.value ?: continue

                try {
                    when (usuario.rol) {
                        Roles.CLIENTE -> simularCliente(usuario.id)
                        Roles.ADMIN -> simularAdmin()
                    }
                } catch (e: Exception) {
                    Log.e("SimuladorViewModel", "Error en simulación", e)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun simularCliente(usuarioId: Int) {
        val pedidos = pedidoDao.obtenerPedidosDeUsuarioUnaVez(usuarioId)
        val pendiente = pedidos.firstOrNull { it.estado != EstadosPedido.ENTREGADO } ?: return
        val siguiente = EstadosPedido.siguiente(pendiente.estado) ?: return

        pedidoDao.actualizarEstado(pendiente.id, siguiente)

        NotificacionesHelper.mostrarPedidoActualizado(
            context,
            pendiente.codigo,
            siguiente
        )

        notificacionDao.insertar(
            NotificacionEntity(
                titulo = "Pedido ${pendiente.codigo}",
                mensaje = "Tu pedido pasó a: $siguiente",
                tipo = TipoNotificacion.PEDIDO_CLIENTE,
                rolDestino = TipoNotificacion.ROL_CLIENTE
            )
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun simularAdmin() {
        val cliente = usuarioDao.obtenerUsuarioAleatorioPorRol(Roles.CLIENTE) ?: return
        val productos = productoDao.obtenerProductosConStock()
        if (productos.isEmpty()) return

        val items = productos.shuffled()
            .take(Random.nextInt(1, 4))
            .map {
                val cantidad = Random.nextInt(1, 4)
                Triple(it, cantidad, it.precio * cantidad)
            }

        val total = items.sumOf { it.third }
        val cantidadUnidades = items.sumOf { it.second }

        val fecha = System.currentTimeMillis()
        val pedido = PedidoEntity(
            usuarioId = cliente.id,
            codigo = "#PED-" + (fecha % 1_000_000).toString().padStart(6, '0'),
            fecha = fecha,
            total = total,
            cantidadProductos = items.size,
            cantidadUnidades = cantidadUnidades,
            estado = EstadosPedido.PENDIENTE
        )

        val pedidoId = pedidoDao.insertarPedido(pedido).toInt()

        pedidoDao.insertarDetalles(
            items.map { (producto, cantidad, _) ->
                PedidoDetalleEntity(
                    pedidoId = pedidoId,
                    productoId = producto.id,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    cantidad = cantidad
                )
            }
        )

        notificacionDao.insertar(
            NotificacionEntity(
                titulo = "Nuevo pedido ${pedido.codigo}",
                mensaje = "${cliente.nombre} ${cliente.apellido} — $total CLP",
                tipo = TipoNotificacion.PEDIDO_ADMIN,
                rolDestino = TipoNotificacion.ROL_ADMIN
            )
        )

        items.forEach { (producto, cantidad, _) ->
            productoDao.actualizarStock(
                producto.id,
                (producto.stock - cantidad).coerceAtLeast(0)
            )
        }

        NotificacionesHelper.mostrarNuevoPedidoAdmin(
            context,
            pedido.codigo,
            "${cliente.nombre} ${cliente.apellido}",
            total
        )
    }
}