package com.emberfocus.feature.timer.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import com.emberfocus.core.model.Soundscape
import org.koin.android.ext.android.inject

/**
 * Foreground service that keeps the ambient soundscape playing while a focus session runs, even
 * with the screen off. It owns no player of its own — it drives the shared [SoundscapePlayer] that
 * the timer also controls, so UI and service never fight over playback.
 */
@UnstableApi
class SoundscapeService : android.app.Service() {

    private val player: SoundscapePlayer by inject()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val soundscape = intent.getStringExtra(EXTRA_SOUNDSCAPE)
                    ?.let { runCatching { Soundscape.valueOf(it) }.getOrNull() }
                    ?: Soundscape.NONE
                startForeground(NOTIFICATION_ID, buildNotification(soundscape))
                player.play(soundscape)
            }
            ACTION_STOP -> {
                player.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }

    private fun buildNotification(soundscape: Soundscape): Notification {
        createChannel()
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val contentPending = launchIntent?.let {
            PendingIntent.getActivity(
                this,
                0,
                it,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("EmberFocus")
            .setContentText("Playing ${soundscape.displayName}")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(contentPending)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                manager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        "Focus soundscape",
                        NotificationManager.IMPORTANCE_LOW,
                    ).apply { setShowBadge(false) },
                )
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "emberfocus_soundscape"
        private const val NOTIFICATION_ID = 42
        const val ACTION_START = "com.emberfocus.action.START_SOUND"
        const val ACTION_STOP = "com.emberfocus.action.STOP_SOUND"
        const val EXTRA_SOUNDSCAPE = "soundscape"

        fun start(context: Context, soundscape: Soundscape) {
            val intent = Intent(context, SoundscapeService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_SOUNDSCAPE, soundscape.name)
            }
            androidx.core.content.ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, SoundscapeService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
