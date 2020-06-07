# Archia
Archia is an [Elastic Search Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html) DSL for Kotlin.

To reduce learning curve, Archia tries to mimic Elasticsearch's own query language as close as
possible. This means you can take advantage of any existing knowledge you have on Elasticsearch.
Even if you didn't have much Elasticsearch knowledge, Elasticsearch queries you have googled should be 
easily convertible into Archia's DSL.

**Note**: This project's first and upcoming release is still work-in-progress. 
_Contributions are welcome_. 

### Why do we need this DSL?
Off the top of my head:
* **Type-safety**. Most of the values are type-checked.
* **Compile-time structural validation**. You can't just create any random JSON objects and pretend it's a 
valid query.
* **Leverage the power of the host-language**. Archia code is Kotlin code. So you can
reuse all of Kotlin's high-level abstractions and advanced language features to construct your
queries.

### Running the tests
You can run the tests using the Gradle test command:
```kotlin
gradle clean test -i
```
If you are using Intellij, you can also just run the tests from within the IDE itself.

### A Quick Example:

```kotlin
import main.kotlin.archia.interpreter.*

val result = interpret {
    query {
        bool {
            must {
                term { "user" to "melvic" }
            }
            filter { term { "tag" to "tech"} }
            mustNot {
                range {
                    "age" { gte = 10.es(); lte = 20.es() }
                }
            }
            should {
                term { "message" to "go beyond plus ultra" }
                term {
                    "tag" {
                        value = "elasticsearch"
                        boost = 1.4
                    }
                }
            }
            minimumShouldMatch = 1.es()
            boost = 1.0
        }
    }
}
```

There is also a shorter form, `evalQuery`, that asks for a query and wraps it in a
`interpret { query { ... }}`. 

Here's another example showing `evalQuery` (along with other features) in action:
```kotlin
val output = evalQuery {
    functionScore {
        query { matchAll {} }
        boost = "5"
        functions(
            {
                filter { term { "test" to "bar" } }
                randomScore {  }
                weight = 23
            },
            {
                filter { term { "test" to "cat" } }
                weight = 42
            }
        )
        maxBoost = 42
        scoreMode = ScoreMode.MAX
        boostMode = BoostMode.MULTIPLY
        minScore = 42
    }
}
```

It also shows a different way of writing arrays of query clauses. 

You are expected to handle errors, if any:

```kotlin
val output = when (result) {
    is Failed -> result.show()      
    is Success<*> -> result.value()     
}
``` 

A successful query produces a JSON object, which you can then transform into various forms
of your liking.  

### Transformers

So far, the only built-in _transformer_ converts the JSON object into its string representation.
Using it requires the application of the `mapTo` method to the `JsonStringOutput` transformer:
```kotlin
...
import main.kotlin.archia.output.*

val result = ...
val output = when (result) {
    ...
}
val jsonString = output.mapTo(JsonStringOutput)
println(jsonString)
```

For instance, the first example would produce the following JSON string:
```json
{
   "query": {
      "bool": {
         "must": {
            "term": { "user":"melvic" }
         },
         "should": [
            { "term": { "message": "go beyond plus ultra" } },
            {
               "term": {
                  "tag": {
                     "value": "elasticsearch",
                     "boost": 1.4
                  }
               }
            }
         ],
         "filter": {
            "term": { "tag": "tech" }
         },
         "must_not": {
            "range": {
               "age": {
                  "gte": 10,
                  "lte": 20
               }
            }
         },
         "minimum_should_match": 1,
         "boost": 1.0
      }
   }
}
```

(_The resulting JSON string is manually prettified. At the time of this writing, 
prettification of the string output isn't supported yet, but soon will be._)

The following are the currently supported queries:

### Compound Queries: 
* Bool Query
* Boosting Query
* Constant Score Query
* Disjunction Max Query
* Function Score Query

### Leaf Queries

#### Full Text Queries
* Common Terms Query
* Intervals Query
* Match Bool Prefix Query
* Match Phrase Prefix Query
* Match Phrase Query
* Match Query
* Multi Match Query
* Query String Query
* Simple Query String Query

#### Geo & Shape Queries
* Geo Bounding Box Query
* Geo Distance Query
* Geo Point
* Geo Polygon Query
* Geo Shape Query
* Shape Query

#### Joining Queries
* Nested Query
* Has Child Query
* Has Parent Query
* Parent Id Query

#### Term-level Queries
* Exists Query
* Fuzzy Query
* IDs Query
* Prefix Query
* Range Query
* Regexp Query
* Term Query
* Terms Query
* Terms Set Query
* Type Query

#### Others
* Match All Query
* Match None Query

Future features might add support for transforming the result into actual queries that connect to the elastic
engine. However, this isn't currently a priority.