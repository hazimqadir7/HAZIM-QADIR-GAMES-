package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.models.GameData
import com.example.data.models.HornNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * High-performance procedural sound engine for Truck Simulator Kashmir.
 * Uses Android AudioTrack PCM wave generation for diesel engine rumbling,
 * turbo spool, musical pressure horns, air brakes, and wolf exhaust whistles.
 */
class TruckSoundEngine(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var isMuted = false

    // Haptics
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // Engine loop
    private var engineTrack: AudioTrack? = null
    private var engineLoopJob: Job? = null
    private val sampleRate = 22050
    @Volatile private var targetRpm: Float = 800f
    @Volatile private var isEngineRunning: Boolean = false

    // Horn playback job
    private var hornJob: Job? = null

    init {
        setupEngineAudioTrack()
    }

    private fun setupEngineAudioTrack() {
        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufSize = maxOf(minBufSize, 4096)

            engineTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startEngine(initialRpm: Float = 800f) {
        if (isEngineRunning) return
        isEngineRunning = true
        targetRpm = initialRpm
        playStarterCrank()

        engineLoopJob?.cancel()
        engineLoopJob = scope.launch {
            delay(400) // Wait for starter crank
            try {
                engineTrack?.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val bufferSize = 1024
            val buffer = ShortArray(bufferSize)
            var phase1 = 0.0
            var phase2 = 0.0
            var turboPhase = 0.0

            while (isActive && isEngineRunning) {
                if (isMuted) {
                    buffer.fill(0)
                } else {
                    val rpm = targetRpm.coerceIn(600f, 3500f)
                    val baseFreq = 25.0 + (rpm / 3000.0) * 80.0
                    val turboFreq = 1200.0 + (rpm / 3000.0) * 2800.0
                    val deltaPhase1 = (2.0 * PI * baseFreq) / sampleRate
                    val deltaPhase2 = (2.0 * PI * (baseFreq * 2.0)) / sampleRate
                    val deltaTurbo = (2.0 * PI * turboFreq) / sampleRate
                    val turboVol = if (rpm > 1800) 0.08 else 0.02

                    for (i in 0 until bufferSize) {
                        phase1 += deltaPhase1
                        phase2 += deltaPhase2
                        turboPhase += deltaTurbo

                        if (phase1 > 2 * PI) phase1 -= 2 * PI
                        if (phase2 > 2 * PI) phase2 -= 2 * PI
                        if (turboPhase > 2 * PI) turboPhase -= 2 * PI

                        // Heavy diesel combustion sawtooth + harmonic
                        val saw = (phase1 / PI - 1.0) * 0.45
                        val sinHarmonic = sin(phase2) * 0.35
                        val turboWhistle = sin(turboPhase) * turboVol
                        val combined = (saw + sinHarmonic + turboWhistle) * 0.7

                        buffer[i] = (combined.coerceIn(-1.0, 1.0) * 32767).toInt().toShort()
                    }
                }

                engineTrack?.write(buffer, 0, bufferSize)
            }

            try {
                engineTrack?.pause()
                engineTrack?.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateRpm(rpm: Float) {
        targetRpm = rpm
    }

    fun stopEngine() {
        isEngineRunning = false
        engineLoopJob?.cancel()
        playAirBrakes()
        vibrate(60)
    }

    fun playStarterCrank() {
        vibrate(180)
        playPcmToneSequence(
            listOf(
                HornNote(85f, 120),
                HornNote(110f, 140),
                HornNote(95f, 130)
            ),
            volume = 0.4f
        )
    }

    fun playGearShift() {
        vibrate(40)
        playPcmToneSequence(
            listOf(
                HornNote(160f, 50),
                HornNote(90f, 60)
            ),
            volume = 0.35f
        )
    }

    fun playAirBrakes() {
        vibrate(90)
        scope.launch {
            playNoiseBurst(durationMs = 380, volume = 0.4f)
        }
    }

    fun playTireScreech() {
        vibrate(80)
        scope.launch {
            playPcmTone(1200f, 220, 0.3f)
        }
    }

    fun playCollision() {
        vibrate(250)
        scope.launch {
            playNoiseBurst(durationMs = 500, volume = 0.75f)
        }
    }

    fun playWolfExhaustWhistle() {
        scope.launch {
            // High pitch howling whistle flutter: 650Hz -> 1100Hz -> 750Hz -> 300Hz
            playPcmToneSequence(
                listOf(
                    HornNote(650f, 100),
                    HornNote(1100f, 140),
                    HornNote(850f, 120),
                    HornNote(980f, 110),
                    HornNote(340f, 200)
                ),
                volume = 0.35f
            )
        }
    }

    fun playHorn(hornId: String) {
        hornJob?.cancel()
        val hornConfig = GameData.HORN_OPTIONS.find { it.id == hornId } ?: GameData.HORN_OPTIONS[0]
        vibrate(150)
        hornJob = scope.launch {
            for (note in hornConfig.melodyNotes) {
                if (!isActive) break
                playPcmTone(note.frequencyHz, note.durationMs, 0.65f)
                delay(note.durationMs.toLong() + 15)
            }
        }
    }

    private fun playPcmTone(freq: Float, durationMs: Int, volume: Float) {
        if (isMuted) return
        try {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)
            var phase = 0.0
            val deltaPhase = (2.0 * PI * freq) / sampleRate

            for (i in 0 until numSamples) {
                phase += deltaPhase
                // Air horn rich harmonics (fundamental + 1.5x harmonic)
                val fundamental = sin(phase) * 0.7
                val harmonic = sin(phase * 1.503) * 0.3
                val env = if (i < 200) i / 200.0 else if (i > numSamples - 400) (numSamples - i) / 400.0 else 1.0
                val sample = ((fundamental + harmonic) * volume * env).coerceIn(-1.0, 1.0) * 32767
                samples[i] = sample.toInt().toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            scope.launch {
                delay(durationMs.toLong() + 100)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playPcmToneSequence(notes: List<HornNote>, volume: Float) {
        scope.launch {
            for (note in notes) {
                if (!isActive) break
                playPcmTone(note.frequencyHz, note.durationMs, volume)
                delay(note.durationMs.toLong() + 10)
            }
        }
    }

    private fun playNoiseBurst(durationMs: Int, volume: Float) {
        if (isMuted) return
        try {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)
            var lastSample = 0.0

            for (i in 0 until numSamples) {
                val white = (Math.random() * 2.0 - 1.0)
                // Filtered pink noise
                lastSample = (lastSample + (0.05 * white)) / 1.05
                val env = (1.0 - (i.toDouble() / numSamples))
                val sample = (lastSample * volume * env).coerceIn(-1.0, 1.0) * 32767
                samples[i] = sample.toInt().toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            scope.launch {
                delay(durationMs.toLong() + 100)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    private fun vibrate(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        isEngineRunning = false
        engineLoopJob?.cancel()
        hornJob?.cancel()
        try {
            engineTrack?.stop()
            engineTrack?.release()
        } catch (_: Exception) {}
    }
}
