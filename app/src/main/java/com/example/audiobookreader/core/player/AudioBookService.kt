package com.example.audiobookreader.core.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.audiobookreader.MainActivity
import com.example.audiobookreader.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioBookService : Service() {
    
    @Inject
    lateinit var audioPlayer: AudioPlayer
    
    private val binder = AudioBookBinder()
    
    companion object {
        private const val TAG = "AudioBookService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "audiobook_playback"
        
        const val ACTION_PLAY = "com.example.audiobookreader.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.audiobookreader.ACTION_PAUSE"
        const val ACTION_STOP = "com.example.audiobookreader.ACTION_STOP"
    }
    
    inner class AudioBookBinder : Binder() {
        fun getService(): AudioBookService = this@AudioBookService
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        createNotificationChannel()
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> audioPlayer.play()
            ACTION_PAUSE -> audioPlayer.pause()
            ACTION_STOP -> {
                audioPlayer.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        
        return START_NOT_STICKY
    }
    
    fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        Log.d(TAG, "Started foreground service")
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val playPauseIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, AudioBookService::class.java).apply {
                action = if (audioPlayer.playbackState.value.isPlaying) ACTION_PAUSE else ACTION_PLAY
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, AudioBookService::class.java).apply {
                action = ACTION_STOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val bookTitle = audioPlayer.playbackState.value.currentBook?.title ?: "AudioBook"
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(bookTitle)
            .setContentText(if (audioPlayer.playbackState.value.isPlaying) "Playing" else "Paused")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_media_pause,
                if (audioPlayer.playbackState.value.isPlaying) "Pause" else "Play",
                playPauseIntent
            )
            .addAction(
                android.R.drawable.ic_delete,
                "Stop",
                stopIntent
            )
            .setOngoing(true)
            .build()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "AudioBook Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Playback controls for audiobooks"
            setShowBadge(false)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.shutdown()
        Log.d(TAG, "Service destroyed")
    }
}
