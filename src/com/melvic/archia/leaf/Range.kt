package com.melvic.archia.leaf

import com.melvic.archia.DateFormat
import com.melvic.archia.Init
import com.melvic.archia.Query

data class Range(var field: RangeField? = null) : Query() {
    operator fun String.invoke(init: Init<RangeField>) =
        RangeField(this).apply(init)
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
)

enum class Relation { INTERSECTS, CONTAINS, WITHIN }

sealed class RangeValue {
    class RangeFormat(format: DateFormat) : RangeValue()
    class RangeNum(value: Float) : RangeValue()
}