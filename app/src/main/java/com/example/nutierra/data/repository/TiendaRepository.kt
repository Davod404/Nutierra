package com.example.nutierra.data.repository

import com.example.nutierra.model.TiendaModels

class TiendaRepository {
    fun obtenerTiendas(): List<TiendaModels> = listOf(
        TiendaModels(
            id = 1,
            nombre = "Nutierra — Providencia",
            direccion = "Av. Providencia 1945, Local 4, Providencia",
            lat = -33.4246122,
            lon = -70.6145784,
            telefono = "+56 9 8765 4321",
            horario = "Lun a Vie: 09:30 - 20:00\nSábados: 10:00 - 15:00"
        ),
        TiendaModels(
            id = 2,
            nombre = "Nutierra — Ñuñoa",
            direccion = "Av. Irarrázaval 3400, Ñuñoa",
            lat = -33.4545,
            lon = -70.6045,
            telefono = "+56 9 8765 4322",
            horario = "Lun a Vie: 10:00 - 19:00\nSábados: 10:00 - 14:00"
        ),
        TiendaModels(
            id = 3,
            nombre = "Nutierra — Las Condes",
            direccion = "Av. Apoquindo 4500, Las Condes",
            lat = -33.4080,
            lon = -70.5700,
            telefono = "+56 9 8765 4323",
            horario = "Lun a Vie: 10:00 - 20:00\nSábados: 10:00 - 16:00"
        )
    )
}