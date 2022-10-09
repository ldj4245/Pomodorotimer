package com.leedaejin.pomodorotimer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ProgressBar
import android.widget.SeekBar

import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)

    }

    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondTextView)
    }

    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null
    private var tickingSoudId: Int? = null
    private var bellSoundId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()

    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        updateRemainTime(progress * 60 * 1000L)

                    }


                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {//내가 다시 조작하기 시작할때
                    stopCountDown()


                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return
                    if (seekBar.progress == 0) {
                        stopCountDown()

                    } else {

                    }
                    startCountDown()

                }
            }

        )

    }

    private fun initSounds() {
        tickingSoudId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)

    }

    private fun createCountDownTimer(initalMillis: Long) =
        object : CountDownTimer(initalMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) { //ontick 시간마다 갱신
                soundPool.autoResume()
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)

            }

            override fun onFinish() {

                completeCountDown()


            }
        }

    private fun startCountDown() {
        currentCountDownTimer =
            createCountDownTimer(seekBar.progress * 60 * 1000L).start() //ms로 변환해서 보내줘야 하기 때문에
        currentCountDownTimer?.start() //현재 진행되고 변수

        tickingSoudId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)

        }
   }

    private fun stopCountDown(){
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()

    }

    private fun completeCountDown() {
        updateRemainTime(0)
        updateSeekBar(0)


        soundPool.autoPause()
        bellSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, 0, 1F)

        }


    }


    private fun updateRemainTime(remainMillis: Long) { //ms를 second로 가공해서 update
        val remainseconds = remainMillis / 1000    //mills를 1000으로 나누게 되면 초가됩니다.

        remainMinutesTextView.text = "%02d'".format(remainseconds / 60)
        remainSecondsTextView.text = "%02d".format(remainseconds % 60)


    }

    private fun updateSeekBar(remainMillis: Long) {

        seekBar.progress = (remainMillis / 1000 / 60).toInt()


    }

}