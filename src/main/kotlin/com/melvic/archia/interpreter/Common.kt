package com.melvic.archia.interpreter

import com.melvic.archia.ast.*
import com.melvic.archia.ast.leaf.fulltext.CommonTermsField
import com.melvic.archia.ast.leaf.geo.*
import com.melvic.archia.output.*
import com.melvic.archia.validate
import com.melvic.archia.validateRequiredParams

fun MinimumShouldMatch.interpret(parent: JsonObject = json {}): JsonValue {
    val msm: MinimumShouldMatch = this
    return with(parent) {
        fun interpretSimple(it: SimpleMSM): JsonValue = when (it) {
            is ANumber -> it.value.json()
            is Percent -> "${it.value}%".json()
            else -> JsonNull
        }
        fun interpretMin(it: MinimumShouldMatch): JsonValue = when (it) {
            is SimpleMSM -> interpretSimple(it)
            is Combination -> "${it.value}<${interpretSimple(it.simple)}".json()
            is Multiple -> array(it.values.map { i -> interpretMin(i) })

            // extended form of MSM
            is CommonTermsField.MinimumShouldMatchWithFreq -> json {
                prop(it::lowFreq) { it.interpret(parent) }
                prop(it::highFreq) { it.interpret(parent) }
            }

            else -> JsonNull
        }
        interpretMin(msm)
    }
}

fun Fuzziness.interpret(): JsonValue {
    return when (this@interpret) {
        is Fuzziness._0 -> 0.json()
        is Fuzziness._1 -> 1.json()
        is Fuzziness._2 -> 2.json()
        is Fuzziness.Auto -> this@interpret.distances?.let { distances ->
            val (low, high) = distances
            "AUTO:[$low],[$high]".json()
        } ?: "AUTO".json()
    }
}

fun GeoPoint.interpret(): JsonValue {
    return when (this) {
        is GeoObject -> with (this) {
            json {
                prop(::lat, required = true) { it.json() }
                prop(::lon, required = true) { it.json() }
            }
        }
        is GeoString -> JsonString("${this.lat},${this.lon}")
        is GeoHash -> JsonString(this.hash)
        is GeoArray -> jsonArray(JsonNumber(lat), JsonNumber(lon))
        is GeoWktPoint -> JsonString("POINT (${this.lat} )")
    }
}

fun Bbox.interpret(): JsonValue {
    return "BBOX ($topRight, $bottomRight, $bottomLeft, $topLeft)".json()
}

fun <F : Field, V> WithShortForm<F, V>.interpret(parent: JsonObject): Evaluation {
    return json {
        validateRequiredParams(this@interpret)

        esName() to run {
            customProp?.let {
                val (paramName, paramValue) = it
                json { paramName to interpretParam(paramName, paramValue) }
            } ?: interpretParamList(parameters, parent)
        }
    }.validate()
}

fun Distance.interpret(): JsonValue {
    return "$value${unit.lowerName()}".json()
}

fun GeoShape.interpret() : JsonValue {
    fun coordsToArray(coord: PointCoordinates) =
        jsonArray(coord.first.json(), coord.second.json())

    fun coordListToArray(coords: List<PointCoordinates>) =
        jsonArray(coords.map { coordsToArray(it) })

    fun coordinatesName() = when (this) {
        is GeometryCollection -> "geometries"
        else -> "coordinates"
    }

    return json {
        "type" to esName().json()
        coordinatesName() to when (this@interpret) {
            is Point -> coordsToArray(coordinates)
            is LineString -> coordListToArray(coordinates)
            is Polygon -> coordListToArray(coordinates)
            is MultiPoint -> coordListToArray(coordinates)
            is MultiLineString -> jsonArray(coordinates.map { coordListToArray(it) })
            is MultiPolygon -> jsonArray(coordinates.map { coordListToArray(it) })
            is GeometryCollection -> jsonArray(geometries.map { it.interpret() })
            is Envelope -> coordListToArray(coordinates)
        }
    }
}