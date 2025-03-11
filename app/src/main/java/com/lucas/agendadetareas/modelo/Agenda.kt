package com.lucas.agendadetareas.modelo

import java.time.LocalDateTime
import java.util.UUID

data class Agenda(
    var nombreAgenda: String,
    var descripcionAgenda: String?,
    var fechaCreacionAgenda: LocalDateTime,
    var listaDeTareas : MutableList<Tarea>,
    val idAgenda: String = UUID.randomUUID().toString()
)
