import com.melvic.archia.ast.leaf.geo.SpatialRelation
import io.kotest.core.spec.style.BehaviorSpec

class ShapeQueryTests : BehaviorSpec({
    given("geo shape query") {
        `when`("shape object is specified") {
            then("the result should contain the shape's type and coordinates") {
                assert {
                    query {
                        shape {
                            "geometry" {
                                shape = envelope(
                                    1355.0 to 5355.0,
                                    1400.0 to 5200.0
                                )
                                relation = SpatialRelation.WITHIN
                            }
                        }
                    }
                    expected = """
                        {
                            "query":{
                                "shape": {
                                    "geometry": {
                                        "shape": {
                                            "type": "envelope",
                                            "coordinates" : [[1355.0, 5355.0], [1400.0, 5200.0]]
                                        },
                                        "relation": "within"
                                    }
                                }
                            }
                        }
                    """.trimIndent()
                }
            }
        }
        `when`("an indexed shape is provided") {
            then("the result contain indexed shape properties") {
                assert {
                    query {
                        shape {
                            "geometry" {
                                indexedShape {
                                    index = "shapes"
                                    id = "footprint"
                                    path = "geometry"
                                }
                            }
                        }
                    }
                    expected = """
                        {
                            "query": {
                                "shape": {
                                    "geometry": {
                                        "indexed_shape": {
                                            "index": "shapes",
                                            "id": "footprint",
                                            "path": "geometry"
                                        }
                                    }
                                }
                            }
                        }
                    """.trimIndent()
                }
            }
        }
    }
})