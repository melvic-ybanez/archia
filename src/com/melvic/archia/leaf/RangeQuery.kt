package com.melvic.archia.leaf

import com.melvic.archia.DateFormat
import com.melvic.archia.Init
import com.melvic.archia.Clause
import com.melvic.archia.WithNum

data class RangeQuery(var field: RangeField? = null) : Clause() {
    operator fun String.invoke(init: Init<RangeField>) {
        field = RangeField(this).apply(init)
    }
}

data class RangeField(
    val name: String,

    var gt: RangeValue? = null,
    var gte: RangeValue? = null,
    var lt: RangeValue? = null,
    var lte: RangeValue? = null,

    var format: DateFormat? = null,
    var relation: Relation? = null,
    var timeZone: String? = null,
    var boost: Float? = null
): WithNum

enum class Relation { INTERSECTS, CONTAINS, WITHIN }

interface RangeValue