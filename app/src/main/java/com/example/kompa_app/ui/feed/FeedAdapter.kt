package com.example.kompa_app.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kompa_app.R
import com.example.kompa_app.core.util.FechaFormatos
import com.example.kompa_app.data.Publicacion

class FeedAdapter :
    ListAdapter<Publicacion, FeedAdapter.PublicacionViewHolder>(DiffPublicacion) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_publicacion, parent, false)
        return PublicacionViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        holder.vincular(getItem(position))
    }

    class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvAutor = itemView.findViewById<TextView>(R.id.tv_autor_item)
        private val tvTitulo = itemView.findViewById<TextView>(R.id.tv_titulo_item)
        private val tvCuerpo = itemView.findViewById<TextView>(R.id.tv_cuerpo_item)
        private val tvFecha = itemView.findViewById<TextView>(R.id.tv_fecha_item)

        fun vincular(publicacion: Publicacion) {
            tvAutor.text = publicacion.autor
            tvTitulo.text = publicacion.titulo
            tvCuerpo.text = publicacion.cuerpo
            tvFecha.text = publicacion.fechaCreacionLong?.let { FechaFormatos.fechaHora(it) }
                ?: itemView.context.getString(R.string.item_sin_dato)
        }
    }

    object DiffPublicacion : DiffUtil.ItemCallback<Publicacion>() {
        override fun areItemsTheSame(oldItem: Publicacion, newItem: Publicacion): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Publicacion, newItem: Publicacion): Boolean =
            oldItem == newItem
    }
}