package com.example.speechify.ktx

import java.util.concurrent.TimeUnit

fun Long.toMinSec(): String {
    if (this < 0)
        return "00:00"

    return String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(this),
        TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(
                this
            )
        )
    );
}