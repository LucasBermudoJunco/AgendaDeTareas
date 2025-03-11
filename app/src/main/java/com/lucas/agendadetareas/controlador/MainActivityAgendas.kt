package com.lucas.agendadetareas.controlador

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
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
import com.lucas.agendadetareas.vista.Dialogos
import java.time.format.DateTimeFormatter

class MainActivityAgendas : AppCompatActivity() {

    private lateinit var adaptadorRecyclerViewAgendas: AdaptadorRecyclerViewAgendas
    private lateinit var recyclerViewAgendas: RecyclerView
    private lateinit var listaDeAgendasTotal: MutableList<Agenda>
    private lateinit var btnAnyadirAgenda : ImageButton
    private lateinit var sharedPreferences: SharedPreferences

    /**
     * Crea la Actividad principal de la aplicación
     * y establece las funcionalidades de los elementos
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pantalla_agendas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listaDeAgendasTotal = mutableListOf()

        // Inicialización y Configuración del RecyclerView de la lista de agendas
        recyclerViewAgendas = findViewById(R.id.recyclerViewTareas)
        recyclerViewAgendas.layoutManager = LinearLayoutManager(this)

        // Configuración del "SharedPreferences" para guardar y cargar contenido
        // en la base de datos de la aplicación
        sharedPreferences = getSharedPreferences("MisAgendas", Context.MODE_PRIVATE)

        // Carga inicial en la aplicación de las agendas contenidas en la base de datos
        cargarLasAgendasDeLaBaseDeDatos()

        // Establecimiento de las funcionalidades a los elementos de la aplicación
        establecerLasFuncionalidadesALosElementos()

    }

    /**
     * Establece las funcionalidades de todos los elementos de la aplicación
     */
    private fun establecerLasFuncionalidadesALosElementos(){

        // Establecimiento de la funcionalidad al botón "+" para agregar una nueva tarea
        btnAnyadirAgenda = findViewById(R.id.btnAnyadirAgenda)
        btnAnyadirAgenda.setOnClickListener{
            Dialogos().mostrarDialogoAnyadirAgenda(this)
        }

        // Establecimiento de las funcionalidades al botón de "Eliminar"
        establecerLasFuncionalidadesALosDemasElementos()

    }

    private fun establecerLasFuncionalidadesALosDemasElementos(){

        // Configuración del adaptador del RecyclerView de las tareas
        adaptadorRecyclerViewAgendas = AdaptadorRecyclerViewAgendas(
            this,
            listaDeAgendasTotal
        ){ idAgenda,nombreDeLaAgenda ->

            // Envío del ID de la agenda a la pantalla de tareas
            val intent = Intent(this, MainActivityTareas::class.java)
            intent.putExtra("idAgenda",idAgenda)
            intent.putExtra("nombreDeLaAgenda",nombreDeLaAgenda)
            startActivity(intent)

        }

        // Asignación del adaptador del RecyclerView
        recyclerViewAgendas.adapter = adaptadorRecyclerViewAgendas

    }

    /**
     * Obtiene las agendas de la base de datos de la aplicación
     */
    fun cargarLasAgendasDeLaBaseDeDatos(){

        // Objeto Gson para la persistencia
        val gson = Gson()
        // Obtención del JSON con los valores de la lista de agendas almacenada en la base de datos
        val json = sharedPreferences.getString("agendas",null)
        // Establecimiento del tipo de datos (la lista de agendas) que se va a obtener
        val tipoDeDatos = object : TypeToken<MutableList<Agenda>>(){}.type

        // Asignación a la lista de tareas de la aplicación de los datos obtenidos del JSON
        listaDeAgendasTotal = gson.fromJson(json,tipoDeDatos) ?: mutableListOf()
        // Ordenación de la lista de tareas por orden alfabético ascendente
        listaDeAgendasTotal.sortBy { it.nombreAgenda }

    }

    /**
     * Guarda las agendas en la base de datos de la aplicación
     */
    fun guardarLasTareasEnLaBaseDeDatos(){

        // Establecimiento del editor de la base de datos
        val editor = sharedPreferences.edit()
        // Objeto GSON para la persistencia
        val gson = Gson()
        // Conversión de la lista de tareas a un objeto JSON
        val json = gson.toJson(listaDeAgendasTotal)

        // Guardado del JSON en la base de datos
        editor.putString("agendas",json)
        editor.apply()

    }

    fun getListaDeAgendas(): MutableList<Agenda>{
        return listaDeAgendasTotal
    }

    fun getAdaptadorRecyclerViewAgendas(): AdaptadorRecyclerViewAgendas {
        return adaptadorRecyclerViewAgendas
    }

}