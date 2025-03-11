package com.lucas.agendadetareas.controlador

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lucas.agendadetareas.R
import com.lucas.agendadetareas.modelo.Agenda
import com.lucas.agendadetareas.modelo.Tarea
import com.lucas.agendadetareas.vista.Dialogos
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivityTareas : AppCompatActivity() {

    private lateinit var adaptadorRecyclerViewTareas: AdaptadorRecyclerViewTareas
    private lateinit var recyclerViewTareas: RecyclerView
    private lateinit var listaDeTareasTotal: MutableList<Tarea>
    private lateinit var listaDeTareasDeEstaAgenda: MutableList<Tarea>
    private lateinit var idAgenda: String
    private lateinit var btnAnyadirTarea : ImageButton
    private lateinit var buttonRegresar: Button
    private lateinit var sharedPreferences: SharedPreferences

    /**
     * Crea la Actividad de las tareas de la aplicación
     * y establece las funcionalidades de los elementos
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pantalla_tareas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listaDeTareasTotal = mutableListOf()
        listaDeTareasDeEstaAgenda = mutableListOf()

        // Inicialización y Configuración del RecyclerView de la lista de tareas
        recyclerViewTareas = findViewById(R.id.recyclerViewTareas)
        recyclerViewTareas.layoutManager = LinearLayoutManager(this)

        // Configuración del "SharedPreferences" para guardar y cargar contenido
        // en la base de datos de la aplicación
        sharedPreferences = getSharedPreferences("MisTareas", Context.MODE_PRIVATE)

        // Carga inicial en la aplicación de las tareas contenidas en la base de datos
        cargarLasTareasDeLaBaseDeDatos()

        // Establecimiento de las funcionalidades a los elementos de la aplicación
        establecerLasFuncionalidadesALosElementos()

//        // Recepción del ID de la agenda de la pantalla de agendas
//        val idAgenda = intent.getStringExtra("idAgenda")
//        Log.d("DEBUG", "ID de la agenda recibida: $idAgenda")
////        Log.d("DEBUG", "Lista de agendas recibidas: ${listaDeAgendas.map { it.idAgenda }}")
//        listaDeTareasDeEstaAgenda = listaDeTareasTotal.filter{it.idAgenda == idAgenda}.toMutableList()
//        val listaDeAgendas = obtenerAgendas()
//        val agendaSeleccionada = listaDeAgendas.find{it.idAgenda == idAgenda}
//
//        if(agendaSeleccionada != null){
//            listaDeTareasTotal = agendaSeleccionada.listaDeTareas
//        } else{
//            Toast.makeText(this,"No se ha encontrado la agenda",Toast.LENGTH_SHORT).show()
//        }

//        // Recepción de la lista de tareas en formato JSON y reconversión de la lista a una MutableList<Tarea>
//        val jsonString = intent.getStringExtra("listaDeTareasJSON")
//        val tipoLista = object : TypeToken<MutableList<Tarea>>(){}.type
//        val gson = Gson()
//        listaDeTareas = gson.fromJson(jsonString,tipoLista)

//        // Configuración del "SharedPreferences" para guardar y cargar contenido
//        // en la base de datos de la aplicación
//        sharedPreferences = getSharedPreferences("MisTareas", Context.MODE_PRIVATE)

//        // Carga inicial en la aplicación de las tareas contenidas en la base de datos
//        cargarLasTareasDeLaBaseDeDatos()

//        // Establecimiento de las funcionalidades a los elementos de la aplicación
//        establecerLasFuncionalidadesALosElementos()

        /*// Ordenación de la lista de tareas por fecha límite
        adaptadorRecyclerViewTareas.ordenarPorFechaLimite()*/

    }

    /**
     * Establece las funcionalidades de todos los elementos de la aplicación
     */
    private fun establecerLasFuncionalidadesALosElementos(){

        // Filtrado de todas las tareas de esta agenda:
        // Recepción del ID de la agenda de la pantalla de agendas
        val idAgenda = intent.getStringExtra("idAgenda")
        listaDeTareasDeEstaAgenda = listaDeTareasTotal.filter{it.idAgenda == idAgenda}.toMutableList()

        // Establecimiento de la funcionalidad al botón "+" para agregar una nueva tarea
        btnAnyadirTarea = findViewById(R.id.btnAnyadirTarea)
        btnAnyadirTarea.setOnClickListener{
            Dialogos().mostrarDialogoAnyadirTarea(this, idAgenda)
        }

        buttonRegresar = findViewById(R.id.buttonRegresar)
        buttonRegresar.setOnClickListener{
            finish()
        }

        // Establecimiento de las funcionalidades al botón de "Eliminar"
        // y al checkbox "Completada"
        establecerLasFuncionalidadesALosDemasElementos()

    }

    private fun establecerLasFuncionalidadesALosDemasElementos(){

        // Obtención del nombre de la Agenda para mostrarlo en la pantalla de las tareas
        val nombreDeLaAgenda = intent.getStringExtra("nombreDeLaAgenda")
        val tvNombreAgenda = findViewById<TextView>(R.id.textViewNombreAgenda)
        tvNombreAgenda.text = nombreDeLaAgenda

        // Configuración del adaptador del RecyclerView de las tareas
        adaptadorRecyclerViewTareas = AdaptadorRecyclerViewTareas(
            this,
            listaDeTareasDeEstaAgenda,
            {guardarLasTareasEnLaBaseDeDatos()}/*,
            {}*/
        )

        // Asignación del adaptador del RecyclerView
        recyclerViewTareas.adapter = adaptadorRecyclerViewTareas

    }

    /**
     * Obtiene las agendas de la base de datos de la aplicación
     */
    fun cargarLasTareasDeLaBaseDeDatos(){

        // Objeto Gson para la persistencia
        val gson = Gson()
        // Obtención del JSON con los valores de la lista de agendas almacenada en la base de datos
        val json = sharedPreferences.getString("tareas",null)
        // Establecimiento del tipo de datos (la lista de agendas) que se va a obtener
        val tipoDeDatos = object : TypeToken<MutableList<Tarea>>(){}.type

        // Asignación a la lista de tareas de la aplicación de los datos obtenidos del JSON
        listaDeTareasTotal = gson.fromJson(json,tipoDeDatos) ?: mutableListOf()
        // Ordenación de la lista de tareas por fecha límite
        val formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        listaDeTareasTotal.sortBy { LocalDate.parse(it.fechaLimiteTarea,formateadorFecha) }

    }

    /**
     * Guarda las tareas en la base de datos de la aplicación
     */
    fun guardarLasTareasEnLaBaseDeDatos(){

        // Establecimiento del editor de la base de datos
        val editor = sharedPreferences.edit()
        // Objeto GSON para la persistencia
        val gson = Gson()
        // Conversión de la lista de tareas a un objeto JSON
        val json = gson.toJson(listaDeTareasTotal)

        // Guardado del JSON en la base de datos
        editor.putString("tareas",json)
        editor.apply()

    }

    /**
     * Obtiene la lista de tareas de la agenda seleccionada en la pantalla de agendas
     */
    fun obtenerAgendas(): MutableList<Agenda>{

        val sharedPreferences = getSharedPreferences("MisAgendas", Context.MODE_PRIVATE)
        val jsonAgendasString = sharedPreferences.getString("agendas","[]")
        val gson = Gson()
        val type = object : TypeToken<MutableList<Agenda>>(){}.type

        return gson.fromJson(jsonAgendasString, type)

    }

    fun getListaDeTareasTotal(): MutableList<Tarea>{
        return listaDeTareasTotal
    }

    fun getListaDeTareasDeEstaAgenda(): MutableList<Tarea>{
        return listaDeTareasDeEstaAgenda
    }

    fun getAdaptadorRecyclerViewTareas(): AdaptadorRecyclerViewTareas {
        return adaptadorRecyclerViewTareas
    }

}