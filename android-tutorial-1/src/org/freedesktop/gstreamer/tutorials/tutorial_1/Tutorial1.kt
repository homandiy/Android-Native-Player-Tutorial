package org.freedesktop.gstreamer.tutorials.tutorial_1

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.homan.huang.common.helper.msg
import org.freedesktop.gstreamer.GStreamer
import java.lang.Exception

class Tutorial1 : Activity() {
    private external fun nativeGetGStreamerInfo(): String

    // Called when the activity is first created.
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            GStreamer.init(this)
        } catch (e: Exception) {
            msg(this, e.message.toString(), 1)
            finish()
            return
        }
        setContentView(R.layout.main)
        val tv = findViewById<View>(R.id.textview_info) as TextView
        tv.text = nativeGetGStreamerInfo() + " !"
    }

    companion object {
        init {
            System.loadLibrary("gstreamer_android")
            System.loadLibrary("tutorial-1")
        }
    }
}