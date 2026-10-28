package com.example.nutierra.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.local.entity.NotificacionEntity
import com.example.nutierra.data.local.entity.Roles
import com.example.nutierra.data.local.entity.TipoNotificacion
import com.example.nutierra.data.repository.NotificacionRepository
import com.example.nutierra.data.session.SesionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotificacionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotificacionRepository(
        NutierraDatabase.getDatabase(application).notificacionDao()
    )

    val notificaciones: StateFlow<List<NotificacionEntity>> =
        SesionManager.usuarioActual
            .flatMapLatest { usuario ->
                val rol = when (usuario?.rol) {
                    Roles.ADMIN -> TipoNotificacion.ROL_ADMIN
                    Roles.CLIENTE -> TipoNotificacion.ROL_CLIENTE
                    else -> TipoNotificacion.ROL_TODOS
                }
                repository.observarPorRol(rol)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val noLeidas: StateFlow<Int> =
        SesionManager.usuarioActual
            .flatMapLatest { usuario ->
                val rol = when (usuario?.rol) {
                    Roles.ADMIN -> TipoNotificacion.ROL_ADMIN
                    Roles.CLIENTE -> TipoNotificacion.ROL_CLIENTE
                    else -> TipoNotificacion.ROL_TODOS
                }
                repository.observarNoLeidasPorRol(rol)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun marcarComoLeida(id: Int) {
        viewModelScope.launch { repository.marcarComoLeida(id) }
    }

    fun marcarTodasComoLeidas() {
        viewModelScope.launch { repository.marcarTodasComoLeidas() }
    }

    fun borrarTodas() {
        viewModelScope.launch { repository.borrarTodas() }
    }
}