# Archia
Archia is a Kotlin DSL for the [Elastic Search Query Language](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html).

**Note**: This project's first and upcoming release is still work in-progress.

### A Quick Example:

```kotlin
val result = runQuery {
    query {
        bool {
            must {
                term { "user" to "kimchy" }
            }
            filter { term { "tag" to "tech"} }
            mustNot {
                range {
                    "age" { gte = 10.es(); lte = 20.es() }
                }
            }
            should {
                term { "tag" to "wow" }
                term { "tag" to "elasticsearch" }
            }
            minimumShouldMatch = 1.es()
            boost = 1.0f
        }
    }
}
```

You are expected to handle errors, if any:

```kotlin
val output = when (result) {
    is Failed -> errorMessage(result.code)
    is Success<*> -> result.value().transform(JsonStringOutput)
}
``` 


A successful query produces a JSON object, which you can then transform into various forms
of your liking.  

### Transformers

So far, the only built-in _transformer_ converts the JSON object into its string representation.
To use it, you need to invoke the `transform` method and apply it to the `JsonStringOutput` transformer:
```kotlin
val result = runQuery { ... }
if (result is Failed) return result

result.value().transform(JsonStringOutput)
```

The transformer produces the following JSON string:
```json
{
   "query": {
      "bool": {
         "must": [
            { "term": { "user": { "value": "kimchy" } } }
         ],
         "should": [
            { "term": { "tag": { "value": "wow" } } },
            { "term": { "tag": { "value":"elasticsearch" } } }
         ],
         "filter": [
            { "term": { "tag": { "value":"tech" } } }
         ],
         "must_not": [
            {
               "range": {
                  "age": {
                     "gte": 10,
                     "lte": 20
                  }
               }
            }
         ],
         "minimum_should_match": 1,
         "boost": 1.0
      }
   }
}
```

Future features might add support for transforming the result into actual queries that connect to the elastic
engine. However, this isn't currently a priority.