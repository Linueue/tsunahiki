package com.kldevs.tsunahiki.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.media3.common.AudioAttributes as Media3AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import java.util.concurrent.ConcurrentHashMap

actual class AudioEngine(private val context: Context) {

    // ---- SFX ----------------------------------------------------------------
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(8)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    /** Map of Sfx -> SoundPool sampleId */
    private val sfxIds = ConcurrentHashMap<AudioTag, Int>()

    // ---- Music --------------------------------------------------------------
    private var musicPlayer: ExoPlayer? = null
    private var musicWasPlaying = false

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            // app went to background
            musicWasPlaying = musicPlayer?.playWhenReady == true
            musicPlayer?.pause()
            soundPool.autoPause()
        }

        override fun onStart(owner: LifecycleOwner) {
            // app came back to foreground
            if (musicWasPlaying) {
                musicPlayer?.play()
            }
            soundPool.autoResume()
        }
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }

    actual fun loadSfx(sfx: AudioTag, assetPath: String) {
        val resource = assetPath.substringAfter("android_asset/")
        val afd = context.assets.openFd(resource)
        val id = afd.use { soundPool.load(it, 1) }
        sfxIds[sfx] = id
    }

    actual fun playSfx(sfx: AudioTag) {
        val id = sfxIds[sfx] ?: return
        soundPool.play(
            id,
            1f,   // left volume
            1f,   // right volume
            1,    // priority
            0,    // loop
            1f    // rate
        )
    }

    actual fun playMusic(assetPath: String, loop: Boolean) {
        stopMusic()

        musicPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(
                Media3AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(assetPath))
                repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                prepare()
                playWhenReady = true
            }
    }

    actual fun stopMusic() {
        musicPlayer?.run {
            stop()
            release()
        }
        musicPlayer = null
    }

    actual fun release() {
        stopMusic()
        soundPool.release()
        sfxIds.clear()
    }
}