package com.homan.huang.common.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView

// A simple SurfaceView whose width and height can be set from the outside
class GStreamerSurfaceView : SurfaceView {
    var media_width = 320
    var media_height = 240

    // Mandatory constructors, they do not do much
    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    // Called by the layout manager to find out our size and give us some rules.
    // We will try to maximize our size, and preserve the media's aspect ratio if
    // we are given the freedom to do so.
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 0
        var height = 0
        val wmode = MeasureSpec.getMode(widthMeasureSpec)
        val hmode = MeasureSpec.getMode(heightMeasureSpec)
        val wsize = MeasureSpec.getSize(widthMeasureSpec)
        val hsize = MeasureSpec.getSize(heightMeasureSpec)
        Log.i("GStreamer", "onMeasure called with " + media_width + "x" + media_height)
        when (wmode) {
            MeasureSpec.AT_MOST -> {
                width = if (hmode == MeasureSpec.EXACTLY) {
                    (hsize * media_width / media_height).coerceAtMost(wsize)
                } else {
                    wsize
                }
            }
            MeasureSpec.EXACTLY -> width = wsize
            MeasureSpec.UNSPECIFIED -> width = media_width
        }
        when (hmode) {
            MeasureSpec.AT_MOST -> {
                height = if (wmode == MeasureSpec.EXACTLY) {
                    (wsize * media_height / media_width).coerceAtMost(hsize)
                } else {
                    hsize
                }
            }
            MeasureSpec.EXACTLY -> height = hsize
            MeasureSpec.UNSPECIFIED -> height = media_height
        }

        // Finally, calculate best size when both axis are free
        if (hmode == MeasureSpec.AT_MOST && wmode == MeasureSpec.AT_MOST) {
            val correct_height = width * media_height / media_width
            val correct_width = height * media_width / media_height
            if (correct_height < height) height = correct_height else width = correct_width
        }

        // Obey minimum size
        width = suggestedMinimumWidth.coerceAtLeast(width)
        height = suggestedMinimumHeight.coerceAtLeast(height)
        setMeasuredDimension(width, height)
    }
}