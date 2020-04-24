package com.melvic.archia.ast.leaf

import com.melvic.archia.ast.*

class RangeQuery : WithField<RangeField>() {
    override fun getField(name: String) = RangeField(name)
}

class RangeField(
    name: String,

    var gt: RangeValue? = null,
    var gte: RangeValue? = null,
    var lt: RangeValue? = null,
    var lte: RangeValue? = null,

    var format: DateFormat? = null,
    var relation: Relation? = null,
    var timeZone: String? = null,
    var boost: Boost? = null
): Field(name), WithNum

enum class Relation { INTERSECTS, CONTAINS, WITHIN }

interface RangeValue