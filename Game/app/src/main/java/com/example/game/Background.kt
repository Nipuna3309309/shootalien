package com.example.game


import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.game.R

class Background internal constructor(screenX: Int, screenY: Int, res: Resources?) {
    var x = 0
    var y = 0
    var background: Bitmap

    init {
        background = BitmapFactory.decodeResource(res, R.drawable.background)
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false)
    }
}
