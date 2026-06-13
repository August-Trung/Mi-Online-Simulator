package com.example.core.sound

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.sin
import kotlin.math.PI
import kotlin.random.Random

object SoundSynth {
    private const val SAMPLE_RATE = 22050 // Optimized sample rate for swift synthesis

    fun playTone(frequency: Double, durationMs: Int, volume: Float = 0.5f) {
        val numSamples = (durationMs * SAMPLE_RATE / 1000)
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            samples[i] = (sin(2.0 * PI * frequency * t) * 32767.0 * volume).toInt().toShort()
        }
        playRawPCM(samples)
    }

    fun playBubbleSound(count: Int = 4, volume: Float = 0.6f) {
        val samplesList = mutableListOf<Short>()
        val rnd = Random
        for (b in 0 until count) {
            val bubbleDuration = 110 // ms
            val startFreq = 650.0 + rnd.nextDouble() * 350.0
            val numSamples = (bubbleDuration * SAMPLE_RATE / 1000)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val tRatio = i.toDouble() / numSamples
                // Slide frequency upwards to mimic rising/popping water bubble ASMR
                val freq = startFreq + (450.0 * tRatio)
                val env = (1.0 - tRatio)
                val sampleVal = (sin(2.0 * PI * freq * t) * 32767.0 * env * volume).toInt().toShort()
                samplesList.add(sampleVal)
            }
            // Ambient steam gap
            val gapSamples = (30 * SAMPLE_RATE / 1000)
            for (i in 0 until gapSamples) {
                samplesList.add(0.toShort())
            }
        }
        playRawPCM(samplesList.toShortArray())
    }

    fun playSizzleSound(durationMs: Int = 1100, volume: Float = 0.35f) {
        val numSamples = (durationMs * SAMPLE_RATE / 1000)
        val samples = ShortArray(numSamples)
        val rnd = Random
        for (i in 0 until numSamples) {
            val tRatio = i.toDouble() / numSamples
            val noise = (rnd.nextDouble() * 2.0 - 1.0)
            // Heat amplitude oscillation to sound like boiling/frying sizzle
            val sizzleEnvelope = 0.55 + 0.45 * sin(2.0 * PI * 18.0 * (i.toDouble() / SAMPLE_RATE))
            var sampleVal = (noise * 32767.0 * volume * sizzleEnvelope)
            
            // Emulate filter difference
            if (i > 0) {
                sampleVal = (sampleVal - samples[i - 1]) * 0.55
            }
            samples[i] = (sampleVal * (1.0 - tRatio)).toInt().toShort()
        }
        playRawPCM(samples)
    }

    fun playNoodleSplash(volume: Float = 0.5f) {
        val durationMs = 280
        val numSamples = (durationMs * SAMPLE_RATE / 1000)
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val tRatio = i.toDouble() / numSamples
            // High to low frequency slide for a wet splash
            val freq = 450.0 - (280.0 * tRatio)
            val env = sin(PI * tRatio)
            samples[i] = (sin(2.0 * PI * freq * t) * 32767.0 * env * volume).toInt().toShort()
        }
        playRawPCM(samples)
    }

    fun playTickCut(volume: Float = 0.65f) {
        val durationMs = 50
        val numSamples = (durationMs * SAMPLE_RATE / 1000)
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val tRatio = i.toDouble() / numSamples
            val freq = 850.0 - (550.0 * tRatio)
            val env = Math.exp(-14.0 * tRatio)
            samples[i] = (sin(2.0 * PI * freq * t) * 32767.0 * env * volume).toInt().toShort()
        }
        playRawPCM(samples)
    }

    fun playSlurp(volume: Float = 0.55f) {
        val samplesList = mutableListOf<Short>()
        val segments = 3
        for (l in 0 until segments) {
            val durationMs = 140
            val numSamples = (durationMs * SAMPLE_RATE / 1000)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val tRatio = i.toDouble() / numSamples
                val freq = 180.0 + sin(2 * PI * 2.5 * tRatio) * 90.0
                val env = sin(PI * tRatio) * 0.75
                val sampleVal = (sin(2.0 * PI * freq * t) * 32767.0 * env * volume).toInt().toShort()
                samplesList.add(sampleVal)
            }
        }
        playRawPCM(samplesList.toShortArray())
    }

    fun playSuccessTheme(volume: Float = 0.45f) {
        val melody = listOf(261.63, 329.63, 392.00, 523.25, 659.25) // C4, E4, G4, C5, E5
        val samplesList = mutableListOf<Short>()
        
        melody.forEachIndexed { index, freq ->
            val durationMs = 130
            val numSamples = (durationMs * SAMPLE_RATE / 1000)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val tRatio = i.toDouble() / numSamples
                val env = (1.0 - tRatio)
                samplesList.add((sin(2.0 * PI * freq * t) * 32767.0 * env * volume).toInt().toShort())
            }
        }
        playRawPCM(samplesList.toShortArray())
    }

    fun playClick(volume: Float = 0.4f) {
        // Simple tick click sound for general tab/button selection
        val durationMs = 15
        val numSamples = (durationMs * SAMPLE_RATE / 1000)
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val tRatio = i.toDouble() / numSamples
            val freq = 1200.0 - 450.0 * tRatio
            val env = 1.0 - tRatio
            samples[i] = (sin(2.0 * PI * freq * t) * 32767.0 * env * volume).toInt().toShort()
        }
        playRawPCM(samples)
    }

    private fun playRawPCM(samples: ShortArray) {
        Thread {
            try {
                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    samples.size * 2,
                    AudioTrack.MODE_STATIC
                )
                audioTrack.write(samples, 0, samples.size)
                audioTrack.play()
                val playDurationMs = (samples.size * 1000L) / SAMPLE_RATE
                Thread.sleep(playDurationMs + 60)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                // Silently bypass errors if device hardware cannot initialize
            }
        }.start()
    }
}
