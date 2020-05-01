import io.kotest.core.spec.style.BehaviorSpec

class GeoPolygonQueryTests : BehaviorSpec({
    given("geo polygon query") {
        `when`("a geo objects are specified") {
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
    }
})