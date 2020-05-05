import com.melvic.archia.ast.leaf.geo.GeoArray
import io.kotest.core.spec.style.BehaviorSpec

class GeoDistanceQueryTests : BehaviorSpec({
    given("geo distance query") {
        `when`("distance is provided") {
            then("the result should contain distance field") {
                assert {
                    query {
                        bool {
                            must {
                                matchAll {}
                            }
                            filter {
                                geoDistance {
                                    distance = 200.km()
                                    "pin.location" {
                                        lat = 40
                                        lon = -70
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
                                        "geo_distance" : {
                                            "distance" : "200km",
                                            "pin.location" : {
                                                "lat" : 40,
                                                "lon" : -70
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
        `when`("the provided geo point isn't a geo object") {
            then("the result shouldn't contain lat and lon properties") {
                assert {
                    query {
                        bool {
                            must { matchAll{} }
                            filter {
                                geoDistance {
                                    distance = 12.km()
                                    "pin.location" to GeoArray(-70, 40)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
})