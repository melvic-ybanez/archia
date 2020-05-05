package com.melvic.archia.ast.leaf.fulltext

import com.melvic.archia.ast.*

class CommonTermsQuery : WithField<CommonTermsField>() {
    override fun initField(name: String): CommonTermsField {
        return CommonTermsField(name)
    }

    override fun esName(): String = "common"
}

class CommonTermsField(name: String) : Field(name), WithNum {
    var query: String by parameters
    var cutoffFrequency: CutoffFrequency by parameters
    var lowFreqOperator: Operator by parameters
    var minimumShouldMatch: MinimumShouldMatch by parameters

    inner class MinimumShouldMatchWithFreq : TreeNode(), MinimumShouldMatch {
        var lowFreq: MinimumShouldMatch by parameters
        var highFreq: MinimumShouldMatch by parameters
    }

    fun minimumShouldMatch(init: Init<MinimumShouldMatchWithFreq>) {
        // We can't use the setProp utility here because minimum-should-match
        // with frequency is an inner class and we'd get a reflection error
        // if we use setProp
        minimumShouldMatch = MinimumShouldMatchWithFreq().apply(init)
    }
}
