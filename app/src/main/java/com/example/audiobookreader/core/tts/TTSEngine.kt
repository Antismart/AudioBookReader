package com.example.audiobookreader.core.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

class TTSEngine(private val context: Context) {
    
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    
    companion object {
        private const val TAG = "TTSEngine"
        const val MAX_TEXT_LENGTH = 4000 // Android TTS limit
    }
    
    suspend fun initialize(): Boolean = suspendCancellableCoroutine { continuation ->
        tts = TextToSpeech(context) { status ->
            isInitialized = status == TextToSpeech.SUCCESS
            if (isInitialized) {
                tts?.language = Locale.US
                Log.d(TAG, "TTS initialized successfully")
            } else {
                Log.e(TAG, "TTS initialization failed")
            }
            continuation.resume(isInitialized)
        }
    }
    
    fun setLanguage(locale: Locale): Boolean {
        return tts?.setLanguage(locale) == TextToSpeech.LANG_AVAILABLE
    }
    
    fun setPitch(pitch: Float) {
        tts?.setPitch(pitch)
    }
    
    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }
    
    fun speak(text: String, utteranceId: String = UUID.randomUUID().toString()): Boolean {
        if (!isInitialized || tts == null) {
            Log.e(TAG, "TTS not initialized")
            return false
        }
        
        return try {
            val result = tts?.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId)
            result == TextToSpeech.SUCCESS
        } catch (e: Exception) {
            Log.e(TAG, "Error speaking text", e)
            false
        }
    }
    
    fun speakWithCallback(
        text: String,
        utteranceId: String = UUID.randomUUID().toString()
    ): Flow<TTSEvent> = callbackFlow {
        
        if (!isInitialized || tts == null) {
            trySend(TTSEvent.Error("TTS not initialized"))
            close()
            return@callbackFlow
        }
        
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                trySend(TTSEvent.Started(utteranceId ?: ""))
            }
            
            override fun onDone(utteranceId: String?) {
                trySend(TTSEvent.Completed(utteranceId ?: ""))
            }
            
            override fun onError(utteranceId: String?) {
                trySend(TTSEvent.Error("TTS error for utterance: $utteranceId"))
            }
            
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?, errorCode: Int) {
                trySend(TTSEvent.Error("TTS error code: $errorCode for utterance: $utteranceId"))
            }
        })
        
        val result = tts?.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            trySend(TTSEvent.Error("Failed to start TTS"))
        }
        
        awaitClose {
            // Cleanup if needed
        }
    }
    
    fun stop() {
        tts?.stop()
    }
    
    fun pause() {
        // Note: Android TTS doesn't have native pause, so we stop
        tts?.stop()
    }
    
    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }
    
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
    
    fun getAvailableVoices(): List<Voice> {
        return tts?.voices?.map { voice ->
            Voice(
                name = voice.name,
                locale = voice.locale,
                quality = if (voice.quality == android.speech.tts.Voice.QUALITY_VERY_HIGH) 
                    VoiceQuality.VERY_HIGH 
                else if (voice.quality == android.speech.tts.Voice.QUALITY_HIGH)
                    VoiceQuality.HIGH
                else
                    VoiceQuality.NORMAL
            )
        } ?: emptyList()
    }
    
    fun setVoice(voiceName: String): Boolean {
        val voice = tts?.voices?.find { it.name == voiceName }
        return if (voice != null) {
            tts?.voice = voice
            true
        } else {
            false
        }
    }
}

data class Voice(
    val name: String,
    val locale: Locale,
    val quality: VoiceQuality
)

enum class VoiceQuality {
    VERY_HIGH, HIGH, NORMAL, LOW
}

sealed class TTSEvent {
    data class Started(val utteranceId: String) : TTSEvent()
    data class Completed(val utteranceId: String) : TTSEvent()
    data class Error(val message: String) : TTSEvent()
}
