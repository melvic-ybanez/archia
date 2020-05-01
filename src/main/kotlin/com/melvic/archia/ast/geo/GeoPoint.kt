package com.melvic.archia.ast.geo

import com.melvic.archia.ast.DecayFieldType
import kotlin.reflect.KProperty

sealed class GeoPoint : DecayFieldType
data class GeoObject(var lat: Number? = null, var lon: Number? = null) : GeoPoint()
data class GeoString(val lat: Number, val lon: Number) : GeoPoint()
data class GeoHash(val hash: String) : GeoPoint()
data class GeoArray(val lat: Double, val lon: Double) : GeoPoint()
data class GeoWktPoint(val lat: Double, val lon: Double) : GeoPoint()

data class Bbox(
    val topRight: Double,
    val bottomRight: Double,
    val bottomLeft: Double,
    val topLeft: Double
)