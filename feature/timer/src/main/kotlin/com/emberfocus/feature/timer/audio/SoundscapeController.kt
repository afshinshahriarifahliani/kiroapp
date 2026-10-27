package com.emberfocus.feature.timer.audio

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.emberfocus.core.model.Soundscape

/**
 * Thin seam the timer uses to control ambient sound without knowing about services. It starts the
 * foreground [SoundscapeService] (so playback survives screen-off) when a real soundscape should
 * play, and stops it otherwise.
 */
interface SoundscapeController {
    fun apply(soundscape: Soundscape, soundEnabled: Boolean, isPro: Boolean)
    fun stop()
}

@UnstableApi
class DefaultSoundscapeController(
    private val context: Context,
) : SoundscapeController {

    override fun apply(soundscape: Soundscape, soundEnabled: Boolean, isPro: Boolean) {
        val gatedOut = soundscape.isPro && !isPro
        if (!soundEnabled || soundscape == Soundscape.NONE || gatedOut) {
            stop()
            return
        }
        SoundscapeService.start(context, soundscape)
    }

    override fun stop() {
        SoundscapeService.stop(context)
    }
}
