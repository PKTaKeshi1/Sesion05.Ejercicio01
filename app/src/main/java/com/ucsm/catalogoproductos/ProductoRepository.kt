package com.ucsm.catalogoproductos

import android.content.Context
import android.content.SharedPreferences

/**
 * Repositorio de productos usando SharedPreferences.
 *
 * Los datos se almacenan en el archivo XML interno:
 *   data/data/com.ucsm.catalogoproductos/shared_prefs/catalogo_productos.xml
 *
 * Estructura del XML generado:
 *   <map>
 *       <int name="total_productos" value="3" />
 *       <string name="producto_1_nombre">Laptop HP</string>
 *       <int name="producto_1_cantidad" value="5" />
 *       <float name="producto_1_precio" value="1299.99" />
 *       ...
 *   </map>
 */
class ProductoRepository(context: Context) {

    companion object {
        private const val PREFS_NAME = "catalogo_productos"
        private const val KEY_TOTAL = "total_productos"
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_CANTIDAD = "cantidad"
        private const val KEY_PRECIO = "precio"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ─── GUARDAR ────────────────────────────────────────────────────────────

    /**
     * Guarda un producto en SharedPreferences.
     * Asigna un ID autoincremental y actualiza el contador total.
     */
    fun guardarProducto(nombre: String, cantidad: Int, precio: Float): Producto {
        val total = prefs.getInt(KEY_TOTAL, 0)
        val nuevoId = total + 1

        prefs.edit()
            .putInt(KEY_TOTAL, nuevoId)
            .putString("producto_${nuevoId}_$KEY_NOMBRE", nombre)
            .putInt("producto_${nuevoId}_$KEY_CANTIDAD", cantidad)
            .putFloat("producto_${nuevoId}_$KEY_PRECIO", precio)
            .apply()

        return Producto(id = nuevoId, nombre = nombre, cantidad = cantidad, precio = precio)
    }

    // ─── CARGAR ────────────────────────────────────────────────────────────

    /**
     * Carga todos los productos almacenados en SharedPreferences.
     * Omite entradas cuyo nombre sea vacío (productos eliminados).
     */
    fun cargarProductos(): MutableList<Producto> {
        val total = prefs.getInt(KEY_TOTAL, 0)
        val lista = mutableListOf<Producto>()

        for (id in 1..total) {
            val nombre = prefs.getString("producto_${id}_$KEY_NOMBRE", "") ?: ""
            if (nombre.isEmpty()) continue   // Producto eliminado

            val cantidad = prefs.getInt("producto_${id}_$KEY_CANTIDAD", 0)
            val precio   = prefs.getFloat("producto_${id}_$KEY_PRECIO", 0f)

            lista.add(Producto(id = id, nombre = nombre, cantidad = cantidad, precio = precio))
        }

        return lista
    }

    // ─── ELIMINAR ──────────────────────────────────────────────────────────

    /**
     * Elimina un producto de SharedPreferences borrando sus tres claves.
     */
    fun eliminarProducto(id: Int) {
        prefs.edit()
            .remove("producto_${id}_$KEY_NOMBRE")
            .remove("producto_${id}_$KEY_CANTIDAD")
            .remove("producto_${id}_$KEY_PRECIO")
            .apply()
    }

    // ─── LIMPIAR TODO ─────────────────────────────────────────────────────

    /**
     * Elimina todos los datos del catálogo.
     */
    fun limpiarTodo() {
        prefs.edit().clear().apply()
    }
}
