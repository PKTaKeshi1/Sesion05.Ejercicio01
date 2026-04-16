package com.ucsm.catalogoproductos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Activity principal del Catálogo de Productos.
 *
 * Funcionalidades:
 *  - Muestra todos los productos en un RecyclerView (nombre, cantidad, precio).
 *  - Los datos se persisten automáticamente en SharedPreferences (XML interno).
 *  - Permite agregar nuevos productos desde AgregarProductoActivity.
 *  - Permite eliminar productos individuales con confirmación.
 *  - Permite limpiar todo el catálogo.
 *
 * Archivo SharedPreferences generado:
 *   data/data/com.ucsm.catalogoproductos/shared_prefs/catalogo_productos.xml
 */
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvContador: TextView
    private lateinit var adapter: ProductoAdapter
    private lateinit var repository: ProductoRepository

    // Launcher para AgregarProductoActivity con resultado
    private val agregarLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val id = result.data?.getIntExtra("producto_id", -1) ?: -1
            if (id != -1) {
                // Recarga el producto recién guardado desde SharedPreferences
                val productos = repository.cargarProductos()
                val nuevo = productos.find { it.id == id }
                nuevo?.let { adapter.agregarProducto(it) }
                actualizarContador()
            }
        }
    }

    // ─── Lifecycle ─────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository  = ProductoRepository(this)
        tvContador  = findViewById(R.id.tvContador)
        recyclerView = findViewById(R.id.recyclerViewProductos)

        // Configura el RecyclerView con LinearLayoutManager vertical
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Carga productos existentes desde SharedPreferences al iniciar
        val productosIniciales = repository.cargarProductos()
        adapter = ProductoAdapter(productosIniciales) { producto ->
            confirmarEliminar(producto)
        }
        recyclerView.adapter = adapter
        actualizarContador()

        // ── Botón Agregar ────────────────────────────────────────────────
        findViewById<Button>(R.id.btnAgregar).setOnClickListener {
            agregarLauncher.launch(Intent(this, AgregarProductoActivity::class.java))
        }

        // ── Botón Limpiar Todo ───────────────────────────────────────────
        findViewById<Button>(R.id.btnLimpiar).setOnClickListener {
            if (adapter.itemCount == 0) {
                Toast.makeText(this, "El catálogo ya está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Limpiar catálogo")
                .setMessage("¿Desea eliminar todos los productos? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar todo") { _, _ ->
                    repository.limpiarTodo()
                    adapter.actualizarLista(mutableListOf())
                    actualizarContador()
                    Toast.makeText(this, "Catálogo limpiado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────

    /** Muestra un diálogo de confirmación antes de eliminar un producto. */
    private fun confirmarEliminar(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Eliminar \"${producto.nombre}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                repository.eliminarProducto(producto.id)
                adapter.eliminarProducto(producto)
                actualizarContador()
                Toast.makeText(this, "\"${producto.nombre}\" eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Actualiza el texto del contador de productos. */
    private fun actualizarContador() {
        val count = adapter.itemCount
        tvContador.text = when (count) {
            0    -> "Sin productos registrados"
            1    -> "1 producto registrado"
            else -> "$count productos registrados"
        }
    }
}
