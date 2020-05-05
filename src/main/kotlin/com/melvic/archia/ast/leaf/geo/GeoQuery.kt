package com.melvic.archia.ast.leaf.geo

import com.melvic.archia.ast.Field
import com.melvic.archia.ast.WithField

/**
 * Base class for geo-based queries
 */
abstract class GeoQuery<F : Field> : WithField<F>() {
    var _name: String by parameters
    var validationMethod: ValidationMethod by parameters
}

enum class ValidationMethod {
    IGNORE_MALFORMED, COERCE, STRICT
}

enum class BoundingBoxType {
    INDEXED, MEMORY
}

enum class DistanceType {
    ARC, PLANE
}