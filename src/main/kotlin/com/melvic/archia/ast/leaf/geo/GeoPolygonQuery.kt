package com.melvic.archia.ast.leaf.geo

import com.melvic.archia.ast.Field
import com.melvic.archia.ast.Init

class GeoPolygonQuery : GeoQuery<GeoPolygonField>() {
    override fun initField(name: String): GeoPolygonField {
        return GeoPolygonField(name)
    }
}

class GeoPolygonField(name: String) : Field(name) {
    var points: List<GeoPoint> by parameters

    fun points(vararg init: Init<GeoObject>) {
        points = init.map { GeoObject().apply(it) }
    }

    fun points(vararg point: GeoPoint) {
        points = point.toList()
    }
}