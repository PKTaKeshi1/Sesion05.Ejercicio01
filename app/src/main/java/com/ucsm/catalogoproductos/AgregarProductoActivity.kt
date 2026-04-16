package com.ucsm.catalogoproductos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity para ingresar un nuevo producto.
 * Valida los campos y retorna el ID del producto guardado
 * mediante setResult() para que MainActivity actualice la lista.
 */
class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCantidad: EditText
    private lateinit var etPrecio: EditText
    private lateinit var repository: ProductoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        etNombre   = findViewById(R.id.etNombre)
        etCantidad = findViewById(R.id.etCantidad)
        etPrecio   = findViewById(R.id.etPrecio)
        repository = ProductoRepository(this)

        // ── Guardar ──────────────────────────────────────────────────────
        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            if (validarCampos()) {
                val nombre   = etNombre.text.toString().trim()
                val cantidad = etCantidad.text.toString().toInt()
                val precio   = etPrecio.text.toString().toFloat()

                // Persiste en SharedPreferences
                val nuevo = repository.guardarProducto(nombre, cantidad, precio)

                Toast.makeText(this,
                    "✓ Producto \"${nuevo.nombre}\" guardado (ID: ${nuevo.id})",
                    Toast.LENGTH_SHORT).show()

                // Devuelve el ID a MainActivity para actualizar el RecyclerView
                val resultado = Intent().putExtra("producto_id", nuevo.id)
                setResult(Activity.RESULT_OK, resultado)
                finish()
            }
        }

        // ── Cancelar ─────────────────────────────────────────────────────
        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    // ─── Validación ────────────────────────────────────────────────────────

    private fun validarCampos(): Boolean {
        if (etNombre.text.isBlank()) {
            etNombre.error = "Ingrese el nombre del producto"
            etNombre.requestFocus()
            return false
        }
        if (etCantidad.text.isBlank()) {
            etCantidad.error = "Ingrese la cantidad"
            etCantidad.requestFocus()
            return false
        }
        if (etPrecio.text.isBlank()) {
            etPrecio.error = "Ingrese el precio"
            etPrecio.requestFocus()
            return false
        }
        val precio = etPrecio.text.toString().toFloatOrNull()
        if (precio == null || precio < 0) {
            etPrecio.error = "Precio inválido"
            etPrecio.requestFocus()
            return false
        }
        return true
    }
}
