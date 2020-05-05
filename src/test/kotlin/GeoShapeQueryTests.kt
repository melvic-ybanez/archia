import com.melvic.archia.ast.leaf.geo.SpatialRelation
import io.kotest.core.spec.style.BehaviorSpec

class GeoShapeQueryTests : BehaviorSpec({
    given("geo shape query") {
        `when`("shape object is specified") {
            then("the result should contain the shape's type and coordinates") {
                assert {
                    query {
                        bool {
                            must {
                                matchAll {}
                            }
                            filter {
                                geoShape {
                                    "location" {
                                        shape = envelope(
                                            13.0 to 53.0,
                                            14.0 to 52.0
                                        )
                                        relation = SpatialRelation.WITHIN
                                    }
                                }
                            }
                        }
                    }
                    expected = """
                        {
                            "query":{
                                "bool": {
                                    "must": {
                                        "match_all": {}
                                    },
                                    "filter": {
                                        "geo_shape": {
                                            "location": {
                                                "shape": {
                                                    "type": "envelope",
                                                    "coordinates" : [[13.0, 53.0], [14.0, 52.0]]
                                                },
                                                "relation": "within"
                                            }
                                        }
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
                        bool {
                            filter {
                                geoShape {
                                    "location" {
                                        indexedShape {
                                            index = "shapes"
                                            id = "deu"
                                            path = "location"
                                        }
                                    }
                                }
                            }
                        }
                    }
                    expected = """
                        {
                            "query": {
                                "bool": {
                                    "filter": {
                                        "geo_shape": {
                                            "location": {
                                                "indexed_shape": {
                                                    "index": "shapes",
                                                    "id": "deu",
                                                    "path": "location"
                                                }
                                            }
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