package com.melvic.archia.ast.leaf

import com.melvic.archia.ast.Boost
import com.melvic.archia.ast.Clause

data class MatchAllQuery(var boost: Boost? = null) : Clause

class MatchNone : Clause