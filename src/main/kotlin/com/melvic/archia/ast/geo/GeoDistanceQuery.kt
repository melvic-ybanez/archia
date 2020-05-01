package com.melvic.archia.ast.geo

import com.melvic.archia.ast.*

class GeoDistanceQuery : Clause(), WithDistance {
    var field: Param<GeoPoint> by parameters
    var distance: Distance by parameters
    var distanceType: DistanceType by parameters
    var _name: String by parameters
    var validationMethod: ValidationMethod by parameters

    operator fun String.invoke(init: Init<GeoObject>) {
        setProp(init) { field = Pair(this, it) }
    }

    infix fun String.to(geo: GeoPoint) {
        field = Pair(this, geo)
    }
}