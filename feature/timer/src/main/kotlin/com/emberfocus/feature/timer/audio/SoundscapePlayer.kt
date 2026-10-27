package com.emberfocus.feature.timer.audio

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.emberfocus.core.model.Soundscape

/**
 * Loops a single ambient soundscape from bundled assets using ExoPlayer.
 *
 * The clips are short seamless loops shipped in `assets/soundscapes`. We use
 * [Player.REPEAT_MODE_ONE] so a clip plays continuously with no gap. Audio is marked as
 * media/music usage so it ducks correctly with system audio.
 */
class SoundscapePlayer(
    private val context: Context,
) {
    private var player: ExoPlayer? = null
    private var current: Soundscape = Soundscape.NONE

    private fun ensurePlayer(): ExoPlayer {
        return player ?: ExoPlayer.Builder(context).build().also {
            it.repeatMode = Player.REPEAT_MODE_ONE
            it.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true,
            )
            it.volume = 1f
            player = it
        }
    }

    /** Switch to [soundscape]; NONE (or a null asset) stops playback. */
    fun play(soundscape: Soundscape) {
        if (soundscape == current && player?.isPlaying == true) return
        current = soundscape
        val asset = soundscape.assetFileName
        if (asset == null) {
            stop()
            return
        }
        val exo = ensurePlayer()
        exo.setMediaItem(MediaItem.fromUri("asset:///soundscapes/$asset"))
        exo.prepare()
        exo.playWhenReady = true
    }

    fun pause() {
        player?.playWhenReady = false
    }

    fun resume() {
        if (current.assetFileName != null) {
            player?.playWhenReady = true
        }
    }

    fun stop() {
        player?.let {
            it.stop()
            it.clearMediaItems()
        }
    }

    fun release() {
        player?.release()
        player = null
        current = Soundscape.NONE
    }

    val currentSoundscape: Soundscape get() = current
}
