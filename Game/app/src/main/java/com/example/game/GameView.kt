package com.example.game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.view.MotionEvent
import android.view.SurfaceView
import java.util.Random


class GameView(activity: GameActivity, screenX: Int, screenY: Int) : SurfaceView(activity),
    Runnable {
    private var thread: Thread? = null
    private var isPlaying = false
    private var isGameOver = false
    private val background1: Background
    private val background2: Background
    private val screenX: Int
    private val screenY: Int
    private var score = 0
    private val paint: Paint
    private val flight: Flight
    private val bullets: MutableList<Bullet>
    private val birds: Array<Bird?>
    private var soundPool: SoundPool? = null
    private val random: Random
    private val prefs: SharedPreferences
    private val activity: GameActivity
    private val sound: Int

    init {
        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE)
        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()
            SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build()
        } else SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        sound = soundPool!!.load(activity, R.raw.shoot, 1)
        this.activity = activity
        this.screenX = screenX
        this.screenY = screenY
        screenRatioX = 1920f / screenX
        screenRatioY = 1080f / screenY
        background1 = Background(screenX, screenY, resources)
        background2 = Background(screenX, screenY, resources)
        flight = Flight(this, screenY, resources)
        bullets = ArrayList()
        background2.x = screenX
        paint = Paint()
        paint.textSize = 128f
        paint.color = Color.WHITE
        birds = arrayOfNulls(4)
        for (i in 0..3) {
            val bird = Bird(resources)
            birds[i] = bird
        }
        random = Random()
    }

    override fun run() {
        while (isPlaying) {
            update()
            draw()
            sleep()
        }
    }

    private fun update() {
        background1.x -= (10 * screenRatioX).toInt()
        background2.x -= (10 * screenRatioX).toInt()

        if (background1.x + background1.background.width < 0) {
            background1.x = screenX
        }

        if (background2.x + background2.background.width < 0) {
            background2.x = screenX
        }

        if (flight.isGoingUp) {
            flight.y -= (30 * screenRatioY).toInt()
        } else {
            flight.y += (30 * screenRatioY).toInt()
        }

        if (flight.y < 0) {
            flight.y = 0
        }

        if (flight.y >= screenY - flight.height) {
            flight.y = screenY - flight.height
        }

        val trash = mutableListOf<Bullet>()
        for (bullet in bullets) {
            if (bullet.x > screenX) {
                trash.add(bullet)
            }
            bullet.x += (50 * screenRatioX).toInt()
            for (bird in birds) {
                bird?.let {
                    if (Rect.intersects(it.getCollisionShape(), bullet.getCollisionShape())) {
                        score++
                        it.x = -500
                        bullet.x = screenX + 500
                        it.wasShot = true
                    }
                }
            }

        }

        bullets.removeAll(trash)

        for (bird in birds) {
            bird?.let {
                it.x -= it.speed
                if (it.x + it.width < 0) {
                    if (!it.wasShot) {
                        isGameOver = true
                        return@let
                    }
                    val bound = (30 * screenRatioX).toInt()
                    it.speed = random.nextInt(bound)
                    if (it.speed < (10 * screenRatioX).toInt()) {
                        it.speed = (10 * screenRatioX).toInt()
                    }
                    it.x = screenX
                    it.y = random.nextInt(screenY - it.height)
                    it.wasShot = false
                }
                if (Rect.intersects(it.getCollisionShape(), flight.getCollisionShape())) {
                    isGameOver = true
                    return@let
                }
            }
        }

    }


    fun draw() {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawBitmap(background1.background, background1.x.toFloat(), background1.y.toFloat(), paint)
            canvas.drawBitmap(background2.background, background2.x.toFloat(), background2.y.toFloat(), paint)

            birds.forEach { bird ->
                bird?.let {
                    canvas.drawBitmap(it.getBird(), it.x.toFloat(), it.y.toFloat(), paint)
                }
            }

            canvas.drawText(score.toString(), (screenX / 2).toFloat(), 164f, paint)

            if (isGameOver) {
                isPlaying = false
                canvas.drawBitmap(flight.getDead(), flight.x.toFloat(), flight.y.toFloat(), paint)
                holder.unlockCanvasAndPost(canvas)
                saveIfHighScore()
                waitBeforeExiting()
                return
            }

            canvas.drawBitmap(flight.getFlight(), flight.x.toFloat(), flight.y.toFloat(), paint)

            bullets.forEach { bullet ->
                canvas.drawBitmap(bullet.bullet, bullet.x.toFloat(), bullet.y.toFloat(), paint)
            }

            holder.unlockCanvasAndPost(canvas)
        }
    }


    private fun waitBeforeExiting() {
        try {
            Thread.sleep(3000)
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.finish()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun saveIfHighScore() {
        if (prefs.getInt("highscore", 0) < score) {
            val editor = prefs.edit()
            editor.putInt("highscore", score)
            editor.apply()
        }
    }

    fun sleep() {
        try {
            Thread.sleep(17)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    fun resume() {
        isPlaying = true
        thread = Thread(this)
        thread!!.start()
    }

    fun pause() {
        isPlaying = false
        try {
            thread!!.join()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (event.x < screenX / 2) {
                flight.isGoingUp = true
            } else {
                flight.isGoingUp = false // Set isGoingUp to false if touched on the right side
            }

            MotionEvent.ACTION_UP -> {
                flight.isGoingUp = false // Set isGoingUp to false when touch is released
                if (event.x > screenX / 2) flight.toShoot++
            }
        }
        return true
    }

    fun newBullet() {
        if (!prefs.getBoolean("isMute", false)) soundPool!!.play(sound, 1f, 1f, 0, 0, 1f)
        val bullet = Bullet(resources)
        bullet.x = flight.x + flight.width
        bullet.y = flight.y + flight.height / 2
        bullets.add(bullet)
    }

    companion object {
        var screenRatioX: Float = 0.0f
        var screenRatioY: Float = 0.0f
    }
    fun adjustForLandscape() {
        // Update screen ratios for landscape mode
        screenRatioX = 1080f / screenY
        screenRatioY = 1920f / screenX
    }

    fun adjustForPortrait() {
        // Update screen ratios for portrait mode
        screenRatioX = 1920f / screenX
        screenRatioY = 1080f / screenY
    }

}

