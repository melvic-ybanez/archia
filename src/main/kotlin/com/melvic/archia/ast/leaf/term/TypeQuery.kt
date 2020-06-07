package com.melvic.archia.ast.leaf.term

import com.melvic.archia.ast.Clause

class TypeQuery : Clause() {
    var value: String by parameters
}