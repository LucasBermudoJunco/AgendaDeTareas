package com.lucas.agendadetareas.controlador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lucas.agendadetareas.R
import com.lucas.agendadetareas.modelo.Tarea
import com.lucas.agendadetareas.vista.Dialogos
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdaptadorRecyclerViewTareas(
    private val mainActivityTareas: MainActivityTareas,
    private val listaDeTareasDeEstaAgenda: MutableList<Tarea>,
    private val marcarTareaComoCompletada: () -> Unit,/*,
    private val eliminarTarea: (Int) -> Unit*/)
    : RecyclerView.Adapter<AdaptadorRecyclerViewTareas.ViewHolder>() {

    /**
     * Clase interna para el ViewHolder que contiene su estructura
     */
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val checkBoxTareaEstaCompletada: CheckBox = view.findViewById(R.id.checkBoxTareaEstaCompletada)
        val tituloTarea: TextView = view.findViewById(R.id.textViewTituloTarea)
        val fechaLimiteTarea: TextView = view.findViewById(R.id.textViewFechaLimiteTarea)
        val marcoTarea: LinearLayout = view.findViewById(R.id.marcoTarea)
        val marcoCampos : LinearLayout = view.findViewById(R.id.marcoCampos)

    }

    /**
     * Crea el ViewHolder de cada elemento de la lista
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val vistaTareas = LayoutInflater.from(parent.context).inflate(R.layout.vista_items_tareas,parent,false)
        return ViewHolder(vistaTareas)

    }

    /**
     * Asigna los datos y los métodos al ViewHolder de cada elemento de la lista
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Obtención del elemento de la lista
        val itemDeLaLista = listaDeTareasDeEstaAgenda[position]

        // Asignación al ViewHolder de los datos que se van a mostrar en el RecyclerView
        holder.checkBoxTareaEstaCompletada.isChecked = itemDeLaLista.tareaEstaCompletada
        holder.tituloTarea.text = itemDeLaLista.tituloTarea
        holder.fechaLimiteTarea.text = itemDeLaLista.fechaLimiteTarea.toString()
//        val formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//        holder.fechaLimiteTarea.text = itemDeLaLista.fechaLimiteTarea.toString().format(formateadorFecha)

        // Establecimiento de la funcón Marcar y Desmarcar la Tarea como Completada
        holder.checkBoxTareaEstaCompletada.setOnCheckedChangeListener{_, estaMarcadaComoCompletada ->

            itemDeLaLista.tareaEstaCompletada = estaMarcadaComoCompletada
            marcarTareaComoCompletada()

            // Cambio del color de fondo en función de si la tarea está marcada como completada o no
            if(estaMarcadaComoCompletada){
                holder.marcoTarea.setBackgroundColor(ContextCompat.getColor(holder.checkBoxTareaEstaCompletada.context,
                    R.color.verdeCompletado
                ))
            } else{
                holder.marcoTarea.setBackgroundColor(ContextCompat.getColor(holder.checkBoxTareaEstaCompletada.context,
                    R.color.amarilloPendiente
                ))
            }

        }

        // Establecimiento del color de fondo en función de si la tarea está marcada como completada o no
        if(holder.checkBoxTareaEstaCompletada.isChecked){
            holder.marcoTarea.setBackgroundColor(ContextCompat.getColor(holder.checkBoxTareaEstaCompletada.context,
                R.color.verdeCompletado
            ))
        } else{
            holder.marcoTarea.setBackgroundColor(ContextCompat.getColor(holder.checkBoxTareaEstaCompletada.context,
                R.color.amarilloPendiente
            ))
        }

        // Establecimiento de la función "Modificar o Eliminar" para "marcoCampos"
        holder.marcoCampos.setOnClickListener{
            Dialogos().mostrarDialogoModificarOEliminarTarea(mainActivityTareas,/*holder,*/itemDeLaLista,position)
        }

    }

    /**
     * Devuelve la cantidad de elementos que contiene la lista
     */
    override fun getItemCount(): Int = listaDeTareasDeEstaAgenda.size

    /**
     * Ordena la lista por fecha límite de forma ascendente
     */
    fun ordenarPorFechaLimite(){
        val formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        listaDeTareasDeEstaAgenda.sortBy { LocalDate.parse(it.fechaLimiteTarea,formateadorFecha) }
        notifyDataSetChanged()
    }

}