package com.lucas.agendadetareas.vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.lucas.agendadetareas.controlador.MainActivityAgendas
import com.lucas.agendadetareas.R
import com.lucas.agendadetareas.controlador.MainActivityTareas
import com.lucas.agendadetareas.modelo.Agenda
import com.lucas.agendadetareas.modelo.Tarea
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Dialogos {

    /**
     * Muestra el cuadro de diálogo para añadir una nueva tarea
     */
    @SuppressLint("MissingInflatedId")
    fun mostrarDialogoAnyadirAgenda(mainActivityAgendas: MainActivityAgendas){

        // Creación del constructor del cuadro de diálogo para añadir una nueva agenda
        val constructorAlerta = AlertDialog.Builder(mainActivityAgendas)
        constructorAlerta.setTitle("Añadir nueva agenda")
        val layoutDialogo = mainActivityAgendas.layoutInflater.inflate(R.layout.dialogo_agenda,null)

        // Agregado de los campos de entrada para los campos de la agenda
        val etNombreAgenda = layoutDialogo.findViewById<EditText>(R.id.etNombreAgenda)
        val etDescripcionAgenda = layoutDialogo.findViewById<EditText>(R.id.etDescripcionAgenda)

        // Establecimiento del foco en la celda del nombre de la agenda para que cuando se abra el cuadro de diálogo
        // se muestre también el teclado para poder teclear directamente en la celda del nombre de la agenda
        // sin tener que presionar ninguna celda
        etNombreAgenda.requestFocus()
        etNombreAgenda.post{
            val systemService = mainActivityAgendas.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            systemService.showSoftInput(etNombreAgenda, InputMethodManager.SHOW_IMPLICIT)
        }

        // Establecimiento de la vista del cuadro de diálogo
        constructorAlerta.setView(layoutDialogo)

        // Estableciimento del texto y funcionalidad de los botones positivo y negativo del cuadro de diálogo
        constructorAlerta.setPositiveButton("Añadir",null)
        constructorAlerta.setNegativeButton("Cancelar"){dialog, _ -> dialog.dismiss()}

        // Creación y Mostrado del cuadro de diálogo
        val dialogo = constructorAlerta.create()
        dialogo.show()

        // Detallado de la funcionalidad del botón positivo
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{

            // Obtención del valor de las celdas del nombre y descipción de la agenda
            val nombreAgenda = etNombreAgenda.text.toString().trim()
            val descripcionAgenda = etDescripcionAgenda.text.toString().trim()

            // Guardado de la agenda o Mostrado de un mensaje indicando que faltan campos por rellenar
            if(nombreAgenda.isNotBlank()){

                // Creación de la nueva tarea con los datos introducidos y la casilla desmarcada
                val nuevaAgenda = Agenda(nombreAgenda,descripcionAgenda, LocalDateTime.now(), mutableListOf())
                // Añadido de la nueva tarea a la lista de tareas
                mainActivityAgendas.getListaDeAgendas().add(nuevaAgenda)
                // Guardado(Persistencia) de la nueva tarea a la base de datos de la aplicación
                mainActivityAgendas.guardarLasTareasEnLaBaseDeDatos()
                // Notificación al adaptador del RecyclerView de las tareas del cambio realizado en su lista
                mainActivityAgendas.getAdaptadorRecyclerViewAgendas().notifyItemInserted(mainActivityAgendas.getListaDeAgendas().size - 1)
                // Reordenación de la lista de tareas por fecha límite
                mainActivityAgendas.getAdaptadorRecyclerViewAgendas().ordenarPorOrderAlfabetico()
                // Cerrado del cuadro de diálogo
                dialogo.dismiss()

                // Mensaje de inserción correcta de la tarea
                Toast.makeText(mainActivityAgendas,"Tarea insertada", Toast.LENGTH_SHORT).show()

            } else{
                Toast.makeText(mainActivityAgendas,"La tarea tiene que tener título", Toast.LENGTH_SHORT).show()
            }

        }

    }

    /**
     * Muestra el cuadro de diálogo para modificar o eliminar la tarea seleccionada
     */
    @SuppressLint("MissingInflatedId")
    fun mostrarDialogoModificarOEliminarTarea(mainActivityAgendas: MainActivityAgendas,/* holder: ViewHolder, */agenda: Agenda, posicionDeLaAgenda: Int){

        // Creación del constructor del cuadro de diálogo para añadir una nueva agenda
        val constructorAlerta = AlertDialog.Builder(mainActivityAgendas)
        constructorAlerta.setTitle("Modificar o Eliminar tarea")
        val layoutDialogo = mainActivityAgendas.layoutInflater.inflate(R.layout.dialogo_agenda,null)

        // Agregado de los campos de entrada para los campos de la agenda
        val etNombreAgenda = layoutDialogo.findViewById<EditText>(R.id.etNombreAgenda)
        val etDescripcionAgenda = layoutDialogo.findViewById<EditText>(R.id.etDescripcionAgenda)

        // Obtención del valor actual de los campos de la agenda
        etNombreAgenda.setText(agenda.nombreAgenda)
        etDescripcionAgenda.setText(agenda.descripcionAgenda)

        // Establecimiento de la vista del cuadro de diálogo
        constructorAlerta.setView(layoutDialogo)

        // Estableciimento del texto y funcionalidad de los botones positivo y negativo del cuadro de diálogo
        constructorAlerta.setPositiveButton("Modificar",null)
        constructorAlerta.setNegativeButton("Eliminar",null)

        // Creación y Mostrado del cuadro de diálogo
        val dialogo = constructorAlerta.create()
        dialogo.show()

        // Detallado de la funcionalidad del botón positivo
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{

            // Obtención del valor de las celdas del nombre y descripción de la agenda
            val nombreNuevoAgenda = etNombreAgenda.text.toString().trim()
            val descripcionNuevaAgenda = etDescripcionAgenda.text.toString().trim()

            // Guardado de la agenda o Mostrado de un mensaje indicando que faltan campos por rellenar
            if(nombreNuevoAgenda.isNotBlank()){

                // Modificación de los datos de la agenda con los datos modificados
                agenda.nombreAgenda = nombreNuevoAgenda
                agenda.descripcionAgenda = descripcionNuevaAgenda

                // Guardado(Persistencia) de la nueva tarea a la base de datos de la aplicación
                mainActivityAgendas.guardarLasTareasEnLaBaseDeDatos()
                // Notificación al adaptador del RecyclerView de las tareas del cambio realizado en su lista
                mainActivityAgendas.getAdaptadorRecyclerViewAgendas().notifyItemChanged(posicionDeLaAgenda)
                // Reordenación de la lista de tareas por fecha límite
                mainActivityAgendas.getAdaptadorRecyclerViewAgendas().ordenarPorOrderAlfabetico()
                // Cerrado del cuadro de diálogo
                dialogo.dismiss()

                // Mensaje de modificación correcta de la tarea
                Toast.makeText(mainActivityAgendas,"Tarea modificada", Toast.LENGTH_SHORT).show()

            } else{
                Toast.makeText(mainActivityAgendas,"La tarea tiene que tener título", Toast.LENGTH_SHORT).show()
            }

        }

        // Detallado de la funcionalidad del botón negativo
        dialogo.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener{

            // Eliminación de la tarea de la lista de agendas
            mainActivityAgendas.getListaDeAgendas().remove(agenda)
            // Guardado(Persistencia) de la nueva tarea a la base de datos de la aplicación
            mainActivityAgendas.guardarLasTareasEnLaBaseDeDatos()
            // Notificación al adaptador del RecyclerView de las tareas de la tarea eliminada
            mainActivityAgendas.getAdaptadorRecyclerViewAgendas().notifyItemRemoved(posicionDeLaAgenda)
            // Cerrado del cuadro de diálogo
            dialogo.dismiss()

            // Mensaje de eliminación correcta de la tarea
            Toast.makeText(mainActivityAgendas,"Tarea eliminada", Toast.LENGTH_SHORT).show()

        }

    }

    /**
     * Muestra el cuadro de diálogo para añadir una nueva tarea
     */
    @SuppressLint("MissingInflatedId")
    fun mostrarDialogoAnyadirTarea(mainActivityTareas: MainActivityTareas, idAgenda: String?){

        // Creación del constructor del cuadro de diálogo para añadir una nueva tarea
        val constructorAlerta = AlertDialog.Builder(mainActivityTareas)
        constructorAlerta.setTitle("Añadir nueva tarea")
        val layoutDialogo = mainActivityTareas.layoutInflater.inflate(R.layout.dialogo_tarea,null)

        // Agregado de los campos de entrada para los campos de la tarea
        val etTituloTarea = layoutDialogo.findViewById<EditText>(R.id.etTituloTarea)
        val etContenidoTarea = layoutDialogo.findViewById<EditText>(R.id.etContenidoTarea)
        val etFechaLimiteTarea = layoutDialogo.findViewById<EditText>(R.id.etFechaLimiteTarea)
        // Configuración del DatePickerDialog para elegir la fecha del calendario
        val calendario = Calendar.getInstance()
        val formateoFecha = SimpleDateFormat("dd/MM/yyyy")
        etFechaLimiteTarea.setOnClickListener{
            DatePickerDialog(
                mainActivityTareas,
                { _, anyo, mes, diaDelMes ->
                    calendario.set(anyo,mes,diaDelMes)
                    etFechaLimiteTarea.setText(formateoFecha.format(calendario.time))

                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Establecimiento del foco en la celda del título de la tarea para que cuando se abra el cuadro de diálogo
        // se muestre también el teclado para poder teclear directamente en la celda del título de la tarea
        // sin tener que presionar ninguna celda
        etTituloTarea.requestFocus()
        etTituloTarea.post{
            val systemService = mainActivityTareas.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            systemService.showSoftInput(etTituloTarea, InputMethodManager.SHOW_IMPLICIT)
        }

        // Establecimiento de la vista del cuadro de diálogo
        constructorAlerta.setView(layoutDialogo)

        // Estableciimento del texto y funcionalidad de los botones positivo y negativo del cuadro de diálogo
        constructorAlerta.setPositiveButton("Añadir",null)
        constructorAlerta.setNegativeButton("Cancelar"){dialog, _ -> dialog.dismiss()}

        // Creación y Mostrado del cuadro de diálogo
        val dialogo = constructorAlerta.create()
        dialogo.show()

        // Detallado de la funcionalidad del botón positivo
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{

            // Obtención del valor de las celdas del título y contenido de la tarea
            val tituloTarea = etTituloTarea.text.toString().trim()
            val contenidoTarea = etContenidoTarea.text.toString().trim()
            val fechaLimiteTarea = etFechaLimiteTarea.text.toString()

            // Guardado de la tarea o Mostrado de un mensaje indicando que faltan campos por rellenar
            if(tituloTarea.isNotBlank() && fechaLimiteTarea.isNotBlank()){

                // Creación de la nueva tarea con los datos introducidos y la casilla desmarcada
                val nuevaTarea = Tarea(idAgenda,tituloTarea,contenidoTarea,false, LocalDateTime.now(),
                    /*LocalDateTime.parse(fechaLimiteTarea)*/
                    /*null*/
                    fechaLimiteTarea
                )
                // Añadido de la nueva tarea a la lista de tareas de esta agenda y a la total
                mainActivityTareas.getListaDeTareasDeEstaAgenda().add(nuevaTarea)
                mainActivityTareas.getListaDeTareasTotal().add(nuevaTarea)
                // Guardado(Persistencia) de la nueva tarea a la base de datos de la aplicación
                mainActivityTareas.guardarLasTareasEnLaBaseDeDatos()
                // Notificación al adaptador del RecyclerView de las tareas del cambio realizado en su lista
                mainActivityTareas.getAdaptadorRecyclerViewTareas().notifyItemInserted(mainActivityTareas.getListaDeTareasTotal().size - 1)
                // Reordenación de la lista de tareas por fecha límite
                mainActivityTareas.getAdaptadorRecyclerViewTareas().ordenarPorFechaLimite()
                // Cerrado del cuadro de diálogo
                dialogo.dismiss()

                // Mensaje de inserción correcta de la tarea
                Toast.makeText(mainActivityTareas,"Tarea insertada", Toast.LENGTH_SHORT).show()

            } else{
                Toast.makeText(mainActivityTareas,"La tarea tiene que tener título y fecha límite", Toast.LENGTH_SHORT).show()
            }

        }

    }

    /**
     * Muestra el cuadro de diálogo para modificar o eliminar la tarea seleccionada
     */
    @SuppressLint("MissingInflatedId")
    fun mostrarDialogoModificarOEliminarTarea(mainActivityTareas: MainActivityTareas,/* holder: ViewHolder, */tarea: Tarea, posicionDeLaTarea: Int){

        // Creación del constructor del cuadro de diálogo para añadir una nueva tarea
        val constructorAlerta = AlertDialog.Builder(mainActivityTareas)
        constructorAlerta.setTitle("Modificar o Eliminar tarea")
        val layoutDialogo = mainActivityTareas.layoutInflater.inflate(R.layout.dialogo_tarea,null)

        // Agregado de los campos de entrada para los campos de la tarea
        val etTituloTarea = layoutDialogo.findViewById<EditText>(R.id.etTituloTarea)
        val etContenidoTarea = layoutDialogo.findViewById<EditText>(R.id.etContenidoTarea)
        val etFechaLimiteTarea = layoutDialogo.findViewById<EditText>(R.id.etFechaLimiteTarea)
        // Configuración del DatePickerDialog para elegir la fecha del calendario
        val calendario = Calendar.getInstance()
        val formateoFecha = SimpleDateFormat("dd/MM/yyyy")
        etFechaLimiteTarea.setOnClickListener{
            DatePickerDialog(
                mainActivityTareas,
                { _, anyo, mes, diaDelMes ->
                    calendario.set(anyo,mes,diaDelMes)
                    etFechaLimiteTarea.setText(formateoFecha.format(calendario.time))

                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Obtención del valor actual de los campos de la tarea
        etTituloTarea.setText(tarea.tituloTarea)
        etContenidoTarea.setText(tarea.contenidoTarea)
        etFechaLimiteTarea.setText(tarea.fechaLimiteTarea)

        // Establecimiento de la vista del cuadro de diálogo
        constructorAlerta.setView(layoutDialogo)

        // Estableciimento del texto y funcionalidad de los botones positivo y negativo del cuadro de diálogo
        constructorAlerta.setPositiveButton("Modificar",null)
        constructorAlerta.setNegativeButton("Eliminar",null)

        // Creación y Mostrado del cuadro de diálogo
        val dialogo = constructorAlerta.create()
        dialogo.show()

        // Detallado de la funcionalidad del botón positivo
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{

            // Obtención del valor de las celdas del título y contenido de la tarea
            val tituloNuevoTarea = etTituloTarea.text.toString().trim()
            val contenidoNuevoTarea = etContenidoTarea.text.toString().trim()
            val fechaLimiteNuevaTarea = etFechaLimiteTarea.text.toString().trim()
            val fechaSplit = fechaLimiteNuevaTarea.split("/")
            val diaDeLaFecha = fechaSplit[0]
            val mesDeLaFecha = fechaSplit[1]
            val anyoDeLaFecha = fechaSplit[2]

            // Guardado de la tarea o Mostrado de un mensaje indicando que faltan campos por rellenar
            if(tituloNuevoTarea.isNotBlank()){

                // Modificación de los datos de la tarea con los datos modificados
                tarea.tituloTarea = tituloNuevoTarea
                tarea.contenidoTarea = contenidoNuevoTarea
                // Comprobación de la introducción correcta de la fecha
                try{
                    val formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    LocalDate.parse(fechaLimiteNuevaTarea,formateadorFecha)
                    tarea.fechaLimiteTarea = "$diaDeLaFecha/$mesDeLaFecha/$anyoDeLaFecha"
//                    tarea.fechaLimiteTarea = fechaComprobada.toString()

                    // Guardado(Persistencia) de la nueva tarea a la base de datos de la aplicación
                    mainActivityTareas.guardarLasTareasEnLaBaseDeDatos()
                    // Notificación al adaptador del RecyclerView de las tareas del cambio realizado en su lista
                    mainActivityTareas.getAdaptadorRecyclerViewTareas().notifyItemChanged(posicionDeLaTarea)
                    // Reordenación de la lista de tareas por fecha límite
                    mainActivityTareas.getAdaptadorRecyclerViewTareas().ordenarPorFechaLimite()
                    // Cerrado del cuadro de diálogo
                    dialogo.dismiss()

                    // Mensaje de modificación correcta de la tarea
                    Toast.makeText(mainActivityTareas,"Tarea modificada", Toast.LENGTH_SHORT).show()

                } catch(e: DateTimeParseException){
                    // Mensaje de error de introducción de la fecha
                    Toast.makeText(mainActivityTareas,"Formato de fecha errónea", Toast.LENGTH_SHORT).show()
                }

            } else{
                Toast.makeText(mainActivityTareas,"La tarea tiene que tener título", Toast.LENGTH_SHORT).show()
            }

        }

        // Detallado de la funcionalidad del botón negativo
        dialogo.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener{

            // Eliminación de la tarea de la lista de tareas de esta agenda y de la lista de tareas total
            mainActivityTareas.getListaDeTareasDeEstaAgenda().remove(tarea)
            mainActivityTareas.getListaDeTareasTotal().remove(tarea)
            // Guardado(Persistencia) de la nueva tarea a la base de datos de la aplicación
            mainActivityTareas.guardarLasTareasEnLaBaseDeDatos()
            // Notificación al adaptador del RecyclerView de las tareas de la tarea eliminada
            mainActivityTareas.getAdaptadorRecyclerViewTareas().notifyItemRemoved(posicionDeLaTarea)
            // Cerrado del cuadro de diálogo
            dialogo.dismiss()

            // Mensaje de eliminación correcta de la tarea
            Toast.makeText(mainActivityTareas,"Tarea eliminada", Toast.LENGTH_SHORT).show()

        }

    }

}