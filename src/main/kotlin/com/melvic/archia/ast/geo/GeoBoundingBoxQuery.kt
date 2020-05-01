package com.melvic.archia.ast.geo

import com.melvic.archia.ast.*

class GeoBoundingBoxQuery : WithField<GeoBoundingBoxField>() {
    var _name: String by parameters
    var validationMethod: ValidationMethod by parameters
    var type: BoundingBoxType by parameters

    override fun initField(name: String): GeoBoundingBoxField {
        return GeoBoundingBoxField(name)
    }
}

class GeoBoundingBoxField(name: String) : Field(name) {
    var topLeft: Geo by parameters
    var bottomRight: Geo by parameters
    var topRight: Geo by parameters
    var bottomLeft: Geo by parameters

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

enum class ValidationMethod {
    IGNORE_MALFORMED, COERCE, STRICT
}

enum class BoundingBoxType {
    INDEXED, MEMORY
}