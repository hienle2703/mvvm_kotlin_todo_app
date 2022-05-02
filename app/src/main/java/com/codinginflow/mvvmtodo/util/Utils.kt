package com.codinginflow.mvvmtodo.util

// An extension property. Return same object. This can turn an expression into the statement
val<T> T.exhaustive: T
 get() = this