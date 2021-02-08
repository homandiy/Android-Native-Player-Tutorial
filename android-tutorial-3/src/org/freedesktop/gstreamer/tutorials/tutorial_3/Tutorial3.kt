package org.freedesktop.gstreamer.tutorials.tutorial_3

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import org.freedesktop.gstreamer.GStreamer
import org.freedesktop.gstreamer.tutorials.tutorial_2.common.lgd
import org.freedesktop.gstreamer.tutorials.tutorial_2.common.lgi

class Tutorial3 : Activity(), SurfaceHolder.Callback {
    // JNI
    private external fun nativeInit() // Initialize native code, build pipeline, etc
    private external fun nativeFinalize() // Destroy pipeline and shutdown native code
    private external fun nativePlay() // Set pipeline to PLAYING
    private external fun nativePause() // Set pipeline to PAUSED
    private external fun nativeSurfaceInit(surface: Any)
    private external fun nativeSurfaceFinalize()
    private val native_custom_data // Native code will use this to keep private data
            : Long = 0
    private var is_playing_desired // Whether the user asked to go to PLAYING
            = false

    // UI
    val play:ImageButton by lazy { findViewById(R.id.button_play) }
    val pause:ImageButton by lazy { findViewById(R.id.button_stop) }
    val sv:SurfaceView by lazy { findViewById(R.id.surface_video) }
    val tvmsg:TextView by lazy { findViewById(R.id.textview_message) }

    // Called when the activity is first created.
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize GStreamer and warn if it fails
        try {
            GStreamer.init(this)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // layout
        setContentView(R.layout.main)

        // buttons
        play.setOnClickListener {
            is_playing_desired = true
            nativePlay()
        }

        pause.setOnClickListener {
            is_playing_desired = false
            nativePause()
        }

        // video echo
        sv.holder.addCallback(this)

        // memory
        if (savedInstanceState != null) {
            is_playing_desired = savedInstanceState.getBoolean("playing")
            lgi("GStreamer--Activity created. Saved state is playing:$is_playing_desired")
        } else {
            is_playing_desired = false
            lgi("GStreamer--Activity created. There is no saved state, playing: false")
        }

        // Start with disabled buttons, until native code is initialized
        play.isEnabled = false
        pause.isEnabled = false

        // JNI init
        nativeInit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        lgd("GStreamer--Saving state, playing:$is_playing_desired")
        outState.putBoolean("playing", is_playing_desired)
    }

    override fun onDestroy() {
        nativeFinalize()
        super.onDestroy()
    }

    // Called from native code. This sets the content of the TextView from the UI thread.
    private fun setMessage(message: String) {
        tvmsg.text = message
    }

    // Called from native code. Native code calls this once it has created its pipeline and
    // the main loop is running, so it is ready to accept commands.
    private fun onGStreamerInitialized() {
        lgi("GStreamer--Gst initialized. Restoring state, playing:$is_playing_desired")
        // Restore previous playing state
        if (is_playing_desired) {
            nativePlay()
        } else {
            nativePause()
        }

        // Re-enable buttons, now that GStreamer is initialized
        runOnUiThread {
            play.isEnabled = true
            pause.isEnabled = true
        }

    }

    companion object {
        @JvmStatic
        private external fun nativeClassInit(): Boolean // Initialize native class: cache Method IDs for callbacks

        init {
            System.loadLibrary("gstreamer_android")
            System.loadLibrary("tutorial-3")
            nativeClassInit()
        }
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int
    ) {
        lgd("GStreamer--Surface changed to " +
                "format $format width $width height $height")
        nativeSurfaceInit(holder.surface)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        lgd("GStreamer--Surface created: " + holder.surface)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        lgd("GStreamer--Surface destroyed")
        nativeSurfaceFinalize()
    }
}