package com.example.nutierra.notifications

import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.Manifest
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat
import androidx.annotation.RequiresPermission
import com.example.nutierra.MainActivity
import com.example.nutierra.R

object NotificacionesHelper {

    private const val CANAL_RECORDATORIOS = "recordatorios_carrito"
    private const val ID_RECORDATORIO = 1001

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun mostrarRecordatorioCarrito(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_RECORDATORIOS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Nutierra")
            .setContentText("Hora de llenar tu carrito con las mejores nueces de calidad.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Hora de llenar tu carrito con las mejores nueces de calidad. " +
                            "Cosecha seleccionada, directo del Valle Central a tu mesa."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(ID_RECORDATORIO, notificacion)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun mostrarPedidoActualizado(context: Context, codigo: String, nuevoEstado: String) {
        val notificacion = NotificationCompat.Builder(context, CANAL_RECORDATORIOS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Pedido $codigo")
            .setContentText("Tu pedido pasó a: $nuevoEstado")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(codigo.hashCode(), notificacion)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun mostrarNuevoPedidoAdmin(context: Context, codigo: String, cliente: String, total: Int) {
        val notificacion = NotificationCompat.Builder(context, CANAL_RECORDATORIOS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Nuevo pedido $codigo")
            .setContentText("$cliente — $total CLP")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(codigo.hashCode(), notificacion)
    }
}