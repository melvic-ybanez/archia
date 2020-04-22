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
Using it requires the application of the `transform` method to the `JsonStringOutput` transformer:
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

The transformer will then produce the following JSON string:
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

Future features might add support for transforming the result into actual queries that connect to the elastic
engine. However, this isn't currently a priority.