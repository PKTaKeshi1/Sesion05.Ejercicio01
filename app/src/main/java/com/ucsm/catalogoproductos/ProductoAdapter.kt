package com.ucsm.catalogoproductos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter para el RecyclerView del catálogo de productos.
 * Muestra nombre, cantidad y precio de cada producto.
 * Expone un callback onEliminar para delegar la eliminación a la Activity.
 */
class ProductoAdapter(
    private val productos: MutableList<Producto>,
    private val onEliminar: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    // ─── ViewHolder ────────────────────────────────────────────────────────

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView   = itemView.findViewById(R.id.tvNombreProducto)
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val tvPrecio: TextView   = itemView.findViewById(R.id.tvPrecio)
        val btnEliminar: Button  = itemView.findViewById(R.id.btnEliminar)
    }

    // ─── Ciclo de vida del Adapter ─────────────────────────────────────────

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvNombre.text   = producto.nombre
        holder.tvCantidad.text = "Cant: ${producto.cantidad}"
        holder.tvPrecio.text   = "S/ ${"%.2f".format(producto.precio)}"

        holder.btnEliminar.setOnClickListener {
            onEliminar(producto)
        }
    }

    override fun getItemCount(): Int = productos.size

    // ─── Métodos de actualización ──────────────────────────────────────────

    /** Agrega un producto al final de la lista y notifica el cambio. */
    fun agregarProducto(producto: Producto) {
        productos.add(producto)
        notifyItemInserted(productos.size - 1)
    }

    /** Elimina un producto por su ID y notifica el cambio. */
    fun eliminarProducto(producto: Producto) {
        val index = productos.indexOfFirst { it.id == producto.id }
        if (index != -1) {
            productos.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    /** Reemplaza la lista completa (usado en cargar/limpiar). */
    fun actualizarLista(nuevaLista: MutableList<Producto>) {
        productos.clear()
        productos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
