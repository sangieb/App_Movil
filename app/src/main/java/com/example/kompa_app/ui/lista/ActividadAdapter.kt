package com.example.kompa_app.ui.lista

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kompa_app.R
import com.example.kompa_app.core.util.FechaFormatos
import com.example.kompa_app.data.Actividad
import java.io.File

class ActividadAdapter :
    ListAdapter<Actividad, ActividadAdapter.ActividadViewHolder>(DiffActividad) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad, parent, false)
        return ActividadViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        holder.vincular(getItem(position))
    }

    class ActividadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgFoto = itemView.findViewById<ImageView>(R.id.img_foto_item)
        private val tvNombre = itemView.findViewById<TextView>(R.id.tv_nombre_item)
        private val tvUbicacion = itemView.findViewById<TextView>(R.id.tv_ubicacion_item)
        private val tvCreador = itemView.findViewById<TextView>(R.id.tv_creador_item)
        private val tvDuracion = itemView.findViewById<TextView>(R.id.tv_duracion_item)
        private val tvFecha = itemView.findViewById<TextView>(R.id.tv_fecha_item)

        fun vincular(actividad: Actividad) {
            val contexto = itemView.context
            tvNombre.text = actividad.nombre
            tvUbicacion.text = actividad.ubicacion.ifBlank {
                contexto.getString(R.string.item_sin_dato)
            }
            tvCreador.text = contexto.getString(R.string.item_creado_por, actividad.creadoPor)
            tvDuracion.text = actividad.duracionMin?.let { formatearDuracion(contexto, it) }
                ?: contexto.getString(R.string.item_sin_dato)
            tvFecha.text = actividad.fechaActividadLong?.let { FechaFormatos.fechaHora(it) }
                ?: actividad.fechaCreacionLong?.let { FechaFormatos.fecha(it) }
                ?: contexto.getString(R.string.item_sin_dato)

            val fotoRuta = actividad.fotoRuta
            if (fotoRuta != null) {
                imgFoto.setImageURI(Uri.fromFile(File(fotoRuta)))
            } else {
                imgFoto.setImageResource(R.drawable.ic_actividad_placeholder)
            }
        }

        private fun formatearDuracion(contexto: android.content.Context, minutos: Int): String {
            val horas = minutos / 60
            val resto = minutos % 60
            return when {
                horas == 0 -> contexto.getString(R.string.item_duracion_minutos, minutos)
                resto == 0 -> contexto.getString(R.string.item_duracion_horas, horas)
                else -> contexto.getString(R.string.item_duracion_horas_minutos, horas, resto)
            }
        }
    }

    object DiffActividad : DiffUtil.ItemCallback<Actividad>() {
        override fun areItemsTheSame(oldItem: Actividad, newItem: Actividad): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Actividad, newItem: Actividad): Boolean =
            oldItem == newItem
    }
}