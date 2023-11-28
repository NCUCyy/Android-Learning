package com.cyy.exp2.psychological_test

import java.time.Duration
import java.time.Instant

fun calculateTimeDifference(timestamp1: Long, timestamp2: Long): Duration {
    val instant1 = Instant.ofEpochMilli(timestamp1)
    val instant2 = Instant.ofEpochMilli(timestamp2)

    return Duration.between(instant1, instant2)
}

fun formatDuration(duration: Duration): String {
    val minutes = duration.toMinutesPart()
    val seconds = duration.toSecondsPart()

    return String.format("%02d:%02d", minutes, seconds)
}

fun main() {
    val duration = calculateTimeDifference(1000, 100000)
    println(formatDuration(duration))
}