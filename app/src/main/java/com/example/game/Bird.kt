package com.example.game

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.example.game.GameView.Companion.screenRatioX
import com.example.game.GameView.Companion.screenRatioY

class Bird(res: Resources) {
    var speed = 20
    var wasShot = true
    var x = 0
    var y: Int
    var width = 0
    var height = 0
    private var birdCounter = 1
    private var bird1: Bitmap
    private var bird2: Bitmap
    private var bird3: Bitmap
    private var bird4: Bitmap

    init {
        bird1 = BitmapFactory.decodeResource(res, R.drawable.bird1)
        bird2 = BitmapFactory.decodeResource(res, R.drawable.bird2)
        bird3 = BitmapFactory.decodeResource(res, R.drawable.bird3)
        bird4 = BitmapFactory.decodeResource(res, R.drawable.bird4)

        width = bird1.width
        height = bird1.height

        width /= 6
        height /= 6


        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false)
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false)
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false)
        bird4 = Bitmap.createScaledBitmap(bird4, width, height, false)

        y = -height
    }

    fun getBird(): Bitmap {
        if (birdCounter == 1) {
            birdCounter++
            return bird1
        }

        if (birdCounter == 2) {
            birdCounter++
            return bird2
        }

        if (birdCounter == 3) {
            birdCounter++
            return bird3
        }

        birdCounter = 1

        return bird4
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}