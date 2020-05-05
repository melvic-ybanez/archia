package com.melvic.archia.ast.leaf.geo

import com.melvic.archia.ast.Field
import com.melvic.archia.ast.Init
import com.melvic.archia.ast.setProp

class GeoBoundingBoxQuery : GeoQuery<GeoBoundingBoxField>() {
    var type: BoundingBoxType by parameters

    override fun initField(name: String): GeoBoundingBoxField {
        return GeoBoundingBoxField(name)
    }
}

class GeoBoundingBoxField(name: String) : Field(name) {
    var topLeft: GeoPoint by parameters
    var bottomRight: GeoPoint by parameters
    var topRight: GeoPoint by parameters
    var bottomLeft: GeoPoint by parameters

    var wkt: Bbox by parameters

    // Vertices
    var top: Double by parameters
    var left: Double by parameters
    var bottom: Double by parameters
    var right: Double by parameters

    fun topLeft(init: Init<GeoObject>) {
        setProp(init) { topLeft = it }
    }

    fun bottomRight(init: Init<GeoObject>) {
        setProp(init) { bottomRight = it }
    }

    fun topRight(init: Init<GeoObject>) {
        setProp(init) { topRight = it }
    }

    fun bottomLeft(init: Init<GeoObject>) {
        setProp(init) { bottomLeft = it }
    }
}