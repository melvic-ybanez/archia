import com.melvic.archia.ast.geo.GeoArray
import com.melvic.archia.ast.geo.GeoString
import com.melvic.archia.ast.geo.Bbox
import io.kotest.core.spec.style.BehaviorSpec

class GeoBoundingBoxQueryTests : BehaviorSpec({
    given("geo bounding box query") {
        `when`("the format is a geo point object") {
            then("it should contain lat and lon properties in the result") {
                assert {
                    query {
                        bool {
                            must {
                                matchAll { }
                            }
                            filter {
                                geoBoundingBox {
                                    "pin.location" {
                                        topLeft {
                                            lat = 40.73
                                            lon = -74.1
                                        }
                                        bottomRight {
                                            lat = 40.01
                                            lon = -71.12
                                        }
                                    }
                                }
                            }
                        }
                    }
                    expected = """
                        {
                            "query": {
                                "bool" : {
                                    "must" : {
                                        "match_all" : {}
                                    },
                                    "filter" : {
                                        "geo_bounding_box" : {
                                            "pin.location" : {
                                                "top_left" : {
                                                    "lat" : 40.73,
                                                    "lon" : -74.1
                                                },
                                                "bottom_right" : {
                                                    "lat" : 40.01,
                                                    "lon" : -71.12
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
        `when`("the geo point is in array format") {
            then("the properties should contain instances of GeoArray") {
                assert {
                    query {
                        bool {
                            must {
                                matchAll {}
                            }
                            filter {
                                geoBoundingBox {
                                    "pin.location" {
                                        topLeft = GeoArray(-74.1, 40.73)
                                        bottomRight = GeoArray(-71.12, 40.01)
                                    }
                                }
                            }
                        }
                    }
                    expected = """
                        {
                            "query": {
                                "bool" : {
                                    "must" : {
                                        "match_all" : {}
                                    },
                                    "filter" : {
                                        "geo_bounding_box" : {
                                            "pin.location" : {
                                                "top_left" : [-74.1, 40.73],
                                                "bottom_right" : [-71.12, 40.01]
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
        `when`("the geo points provided are in string format") {
            then("the resulting params should have string values") {
                assert {
                    query {
                        bool {
                            must {
                                matchAll {}
                            }
                            filter {
                                geoBoundingBox {
                                    "pin.location" {
                                        topLeft = GeoString(40.73, -74.1)
                                        bottomRight = GeoString(40.01, -71.12)
                                    }
                                }
                            }
                        }
                    }
                    expected = """
                        {
                            "query": {
                                "bool" : {
                                    "must" : {
                                        "match_all" : {}
                                    },
                                    "filter" : {
                                        "geo_bounding_box" : {
                                            "pin.location" : {
                                                "top_left" : "40.73, -74.1",
                                                "bottom_right" : "40.01, -71.12"
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
        `when`("the geo point is in wkt format") {
            then("the result value should be in BBOX geo point") {
                assert {
                    query {
                        bool {
                            must {
                                matchAll {  }
                            }
                            filter {
                                geoBoundingBox {
                                    "pin.location" {
                                        wkt = Bbox(-74.1, -71.12, 40.73, 40.01)
                                    }
                                }
                            }
                        }
                    }
                    expected = """
                        {
                            "query": {
                                "bool" : {
                                    "must" : {
                                        "match_all" : {}
                                    },
                                    "filter" : {
                                        "geo_bounding_box" : {
                                            "pin.location" : {
                                                "wkt" : "BBOX (-74.1, -71.12, 40.73, 40.01)"
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