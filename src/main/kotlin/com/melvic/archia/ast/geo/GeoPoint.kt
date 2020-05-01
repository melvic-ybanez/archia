package com.melvic.archia.ast.geo

import com.melvic.archia.ast.DecayFieldType


sealed class Geo : DecayFieldType
data class GeoObject(var lat: Number? = null, var lon: Number? = null) : Geo()
data class GeoString(val lat: Number, val lon: Number) : Geo()
data class GeoHash(val hash: String) : Geo()
data class GeoArray(val lat: Double, val lon: Double) : Geo()
data class GeoWktPoint(val lat: Double, val lon: Double) : Geo()

data class GeoWktBb(
    val topRight: Double,
    val bottomRight: Double,
    val bottomLeft: Double,
    val topLeft: Double
)