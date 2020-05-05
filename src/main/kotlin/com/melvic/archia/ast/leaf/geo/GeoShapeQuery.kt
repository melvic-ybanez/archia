package com.melvic.archia.ast.leaf.geo

import com.melvic.archia.ast.*
import kotlin.reflect.KProperty

open class GeoShapeQuery : WithField<GeoShapeField>() {
    override fun initField(name: String): GeoShapeField {
        return GeoShapeField(name)
    }
}

class ShapeQuery : GeoShapeQuery()

class GeoShapeField(name: String) : Field(name), WithGeoShape {
    private var indexedShape: IndexedShape by parameters
    var shape: GeoShape by parameters
    var relation: SpatialRelation by parameters

    fun indexedShape(init: Init<IndexedShape>) {
        setProp(init) { indexedShape = it }
    }
}

class IndexedShape : TreeNode() {
    var id: String by parameters
    var index: String by parameters
    var path: String by parameters
    var routing: String by parameters

    override val requiredParams: List<KProperty<Any>>
        get() = listOf(::id)
}

enum class SpatialRelation {
    INTERSECTS, DISJOINT, WITHIN, CONTAINS
}