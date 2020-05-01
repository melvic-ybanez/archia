import com.melvic.archia.ast.geo.GeoArray
import com.melvic.archia.ast.geo.GeoHash
import com.melvic.archia.ast.geo.GeoString
import io.kotest.core.spec.style.BehaviorSpec

class GeoPolygonQueryTests : BehaviorSpec({
    given("geo polygon query") {
        `when`("the geo points are specified in object format") {
            then("the result should contain an array of geo objects") {
                assert {
                    query {
                        bool {
                            must { matchAll {} }
                            filter {
                                geoPolygon {
                                    "person.location" {
                                        points(
                                            { lat = 40; lon = -70 },
                                            { lat = 30; lon = -80 },
                                            { lat = 20; lon = -90 }
                                        )
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
                                        "geo_polygon" : {
                                            "person.location" : {
                                                "points" : [
                                                    {"lat" : 40, "lon" : -70},
                                                    {"lat" : 30, "lon" : -80},
                                                    {"lat" : 20, "lon" : -90}
                                                ]
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
        `when`("each geo points are specified in geo array format") {
            then("the result should contain an array of geo arrays") {
                assert {
                    query {
                        bool {
                            must { matchAll {} }
                            filter {
                                geoPolygon {
                                    "person.location" {
                                        points(
                                            GeoArray(-70, 40),
                                            GeoArray(-80, 30),
                                            GeoArray(-90, 20)
                                        )
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
                                        "geo_polygon" : {
                                            "person.location" : {
                                                "points" : [
                                                    [-70, 40],
                                                    [-80, 30],
                                                    [-90, 20]
                                                ]
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
        `when`("the formats of the geo points are mixed") {
            then("it should contain all the specified formats") {
                assert {
                    query {
                        bool {
                            must { matchAll {} }
                            filter {
                                geoPolygon {
                                    "person.location" {
                                        points(
                                            GeoHash("drn5x1g8cu2y"),
                                            GeoString(30, -80),
                                            GeoString(20, -90)
                                        )
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
                                       "geo_polygon" : {
                                            "person.location" : {
                                                "points" : [
                                                    "drn5x1g8cu2y",
                                                    "30, -80",
                                                    "20, -90"
                                                ]
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