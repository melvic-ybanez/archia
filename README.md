# Archia
Archia is a Kotlin DSL for the [Elastic Search Query Language](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html).

**Note**: This project's first and upcoming release is still work in-progress.

### A Quick Example:

```kotlin
val result = runQuery {
    query {
        match {
            "message" {
                query = text("to be or not to be")
                operator = Operator.AND
                zeroTermsQuery = ZeroTermsQuery.ALL
            }
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

So far, the only built-in _transformer_ converts the JSON object into its string representation:

```json
{
    "query": {
        "match" : {
            "message" : {
                "query" : "to be or not to be",
                "operator" : "and",
                "zero_terms_query": "all"
            }
        }
    }
}
```

Future features might add support for transforming the result into actual queries that connect to the elastic
engine. However, this isn't currently a priority.