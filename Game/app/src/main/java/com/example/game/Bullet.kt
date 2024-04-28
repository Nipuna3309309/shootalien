package com.example.game

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.example.game.GameView.Companion.screenRatioX
import com.example.game.GameView.Companion.screenRatioY

class Bullet(res: Resources) {
    var x = 0
    var y = 0
    var width = 0
    var height = 0
    var bullet: Bitmap

    init {
        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet)

        width = bullet.width
        height = bullet.height

        width /= 4
        height /= 4

        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false)
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}