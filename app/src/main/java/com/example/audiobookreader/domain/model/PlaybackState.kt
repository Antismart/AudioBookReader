package com.example.audiobookreader.domain.model

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val playbackSpeed: Float = 1.0f,
    val currentBook: Book? = null,
    val currentText: String = "",
    val buffering: Boolean = false
) {
    val progress: Float
        get() = if (duration > 0) currentPosition.toFloat() / duration else 0f
}

enum class PlaybackSpeed(val speed: Float, val label: String) {
    SLOW(0.5f, "0.5x"),
    SLOWER(0.75f, "0.75x"),
    NORMAL(1.0f, "1.0x"),
    FASTER(1.25f, "1.25x"),
    FAST(1.5f, "1.5x"),
    VERY_FAST(2.0f, "2.0x");

    companion object {
        fun fromSpeed(speed: Float): PlaybackSpeed {
            return entries.minByOrNull { kotlin.math.abs(it.speed - speed) } ?: NORMAL
        }
    }
}
