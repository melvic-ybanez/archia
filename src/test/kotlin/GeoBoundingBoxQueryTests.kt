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
    }
})