package com.melvic.archia.ast.leaf

import com.melvic.archia.ast.*

class RangeQuery : WithField<RangeField>() {
    override fun initField(name: String) = RangeField(name)
}

class RangeField(name: String): Field(name), WithNum {
    var gt: RangeValue by parameters
    var gte: RangeValue by parameters
    var lt: RangeValue by parameters
    var lte: RangeValue by parameters

    var format: DateFormat by parameters
    var relation: Relation by parameters
    var timeZone: TimeZone by parameters
    var boost: Boost by parameters
}

enum class Relation { INTERSECTS, CONTAINS, WITHIN }

interface RangeValue