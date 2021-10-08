package com.example.speechify.ktx

import android.view.View

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.inVisible() {
    this.visibility = View.INVISIBLE
}

fun List<View>.gone() {
    this.forEach {
        it.gone()
    }
}

fun View.visibleIfOrInvisible(status: Boolean) {
    if (status)
        this.visible()
    else
        this.inVisible()
}
