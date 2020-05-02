package com.melvic.archia.ast.geo

import com.melvic.archia.interpreter.toSnakeCase
import java.util.*

sealed class GeoShape {
    fun esName(): String {
        val simpleName = this::class.java.simpleName
        return simpleName.toSnakeCase()
    }
}

typealias PointCoordinates = Pair<Double, Double>

data class Point(val coordinates: PointCoordinates) : GeoShape()

class LineString(val coordinates: List<PointCoordinates>) : GeoShape()

class Polygon(
    private val exterior: PointCoordinates,
    private val interior: List<PointCoordinates>
) : GeoShape() {
    val coordinates: List<PointCoordinates> by lazy {
        val points = LinkedList(interior.toList())
        points.push(exterior)
        points
    }
}

class MultiPoint(val coordinates: List<PointCoordinates>) : GeoShape()

class MultiLineString(val coordinates: List<List<PointCoordinates>>) : GeoShape()

class MultiPolygon(
    private val polygonCoords: List<Pair<PointCoordinates, List<PointCoordinates>>>
) : GeoShape() {
    val coordinates: List<List<PointCoordinates>> by lazy {
        val coords = mutableListOf<List<PointCoordinates>>()
        for ((exterior, interior) in polygonCoords) {
            val polygon = Polygon(exterior, interior)
            coords.add(polygon.coordinates)
        }
        coords
    }
}

class GeometryCollection(val geometries: List<GeoShape>) : GeoShape()

class Envelope(val coordinates: List<PointCoordinates>) : GeoShape()

interface WithGeoShape {
    fun point(coordinates: PointCoordinates) = Point(coordinates)

    fun lineString(vararg coordinates: PointCoordinates) = LineString(coordinates.toList())

    fun polygon(exterior: PointCoordinates, vararg interior: PointCoordinates) =
        Polygon(exterior, interior.toList())

    fun multiPoint(vararg coordinates: PointCoordinates) = MultiPoint(coordinates.toList())

    fun multiLineString(vararg coordinates: List<PointCoordinates>) =
        MultiLineString(coordinates.toList())

    fun multiPolygon(vararg coordinates: Pair<PointCoordinates, List<PointCoordinates>>) =
        MultiPolygon(coordinates.toList())

    fun geometryCollection(vararg geometry: GeoShape) = GeometryCollection(geometry.toList())

    fun envelope(vararg coordinates: PointCoordinates) = Envelope(coordinates.toList())
}