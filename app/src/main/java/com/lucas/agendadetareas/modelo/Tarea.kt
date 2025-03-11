package com.lucas.agendadetareas.modelo

import java.time.LocalDateTime

data class Tarea(
    val idAgenda: String?,
    var tituloTarea: String,
    var contenidoTarea: String?,
    var tareaEstaCompletada: Boolean,
    var fechaCreacionTarea: LocalDateTime,
    var fechaLimiteTarea: String?
)
