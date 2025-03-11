package com.lucas.agendadetareas.controlador

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lucas.agendadetareas.R
import com.lucas.agendadetareas.modelo.Agenda
import com.lucas.agendadetareas.vista.Dialogos
import java.time.format.DateTimeFormatter

class AdaptadorRecyclerViewAgendas(
    private val mainActivityAgendas: MainActivityAgendas,
    private val listaDeAgendas: MutableList<Agenda>,
    private val verLasTareasDeEstaAgenda : (String,String) -> Unit/*,
    private val eliminarAgenda: (Int) -> Unit*/)
    : RecyclerView.Adapter<AdaptadorRecyclerViewAgendas.ViewHolder>() {

    /**
     * Clase interna para el ViewHolder que contiene su estructura
     */
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val nombreAgenda: TextView = view.findViewById(R.id.textViewNombreAgenda)
        val marcoAgenda: LinearLayout = view.findViewById(R.id.marcoAgenda)

    }

    /**
     * Crea el ViewHolder de cada elemento de la lista
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val vistaAgendas = LayoutInflater.from(parent.context).inflate(R.layout.vista_items_agendas,parent,false)
        return ViewHolder(vistaAgendas)

    }

    /**
     * Asigna los datos y los métodos al ViewHolder de cada elemento de la lista
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Obtención del elemento de la lista
        val itemDeLaLista = listaDeAgendas[position]

        // Asignación al ViewHolder de los datos que se van a mostrar en el RecyclerView
        holder.nombreAgenda.text = itemDeLaLista.nombreAgenda

        // Establecimiento de la función "Modificar o Eliminar" para "marcoAgenda"
        holder.marcoAgenda.setOnLongClickListener{
            Dialogos().mostrarDialogoModificarOEliminarTarea(mainActivityAgendas,/*holder,*/itemDeLaLista,position)
            true
        }

        // Establecimiento de la funcionalidad de ir a la lista de tareas de la agenda pulsada
        holder.marcoAgenda.setOnClickListener{

            Log.d("DEBUG", "ID de la agenda pulsada: ${itemDeLaLista.idAgenda}")

            verLasTareasDeEstaAgenda(itemDeLaLista.idAgenda,itemDeLaLista.nombreAgenda)

//            // Conversión de la lista de tareas de la agenda en un JSON para enviarlo con "putString"
//            val gson = Gson()
//            val jsonString = gson.toJson(itemDeLaLista.listaDeTareas)
//
//            // Envío de la lista de tareas en forma de JSON a la pantalla de tareas
//            val intent = Intent(mainActivityAgendas, MainActivityTareas::class.java)
//            intent.putExtra("listaDeTareasJSON",jsonString)
//            mainActivityAgendas.startActivity(intent)

        }

    }

    /**
     * Devuelve la cantidad de elementos que contiene la lista
     */
    override fun getItemCount(): Int = listaDeAgendas.size

    /**
     * Ordena la lista por nombre de la agenda por nombre alfabético en orden ascendente
     */
    fun ordenarPorOrderAlfabetico(){
        val formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        listaDeAgendas.sortBy { it.nombreAgenda }
        notifyDataSetChanged()
    }

}