package com.ucsm.catalogoproductos

/**
 * Modelo de datos para un Producto del catálogo.
 * Se serializa a XML mediante SharedPreferences usando formato clave-valor.
 *
 * Clave en SharedPreferences:
 *   - "producto_<id>_nombre"   → String
 *   - "producto_<id>_cantidad" → Int
 *   - "producto_<id>_precio"   → Float
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val precio: Float
)
