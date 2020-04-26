package com.melvic.archia.ast.fulltext

import com.melvic.archia.ast.*

class CommonTermsQuery : WithField<CommonTermsField>() {
    override fun getField(name: String): CommonTermsField {
        return CommonTermsField(name)
    }

    override fun esName(): String = "common"
}

class CommonTermsField(
    name: String,
    var query: String? = null,
    var cutoffFrequency: CutoffFrequency? = null,
    var lowFreqOperator: Operator? = null,
    var minimumShouldMatch: MinimumShouldMatch? = null
) : Field(name), WithNum {
    inner class MinimumShouldMatchWithFreq(
        var lowFreq: MinimumShouldMatch? = null,
        var highFreq: MinimumShouldMatch? = null
    ) : MinimumShouldMatch

    fun minimumShouldMatch(init: Init<MinimumShouldMatchWithFreq>) {
        // We can't use the setProp utility here because minimum-should-match
        // with frequency is an inner class and we'd get a reflection error
        // if we use setProp
        minimumShouldMatch = MinimumShouldMatchWithFreq().apply(init)
    }
}
