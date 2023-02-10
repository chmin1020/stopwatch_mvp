package com.example.stopwatch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timer

class TimerLogic(private val timerView: TimerContract.TimerView): TimerContract.TimerPresenter {
    private val defaultTimeText = "00:00"

    //타이머 객체
    private var timer: Timer? = null

    //현재 분초 변수
    private var currentSecond = 0
    private var currentMinute = 0

    //밀리세컨드 세는 변수
    private var milliSecondCount = 0

    //카운트다운 세는 변수(초)
    private var countdownFirst = 0.0
    private var countdownMilliSecond = 0


    //-----------------------------------------
    // 오버라이드 함수 영역

    override fun startTimer(){
        timer = timer(initialDelay = 0L, period = 100){


            if(countdownMilliSecond == 0) {
                //시간 변수 업데이트
                updateTimeValues()

                //메인 스레드에서 ui 업데이트
                val curTimeText = makeTimeFormat(minute = currentMinute, second = currentSecond)
                CoroutineScope(Dispatchers.Main).launch {
                    timerView.updateTimeText(curTimeText, milliSecondCount.toString())
                }
            }
            else{
                //카운트다운 밀리초 업데이트
                countdownMilliSecond--

                //메인 스레드에서 ui 업데이트
                CoroutineScope(Dispatchers.Main).launch {
                    val countdownSecond = countdownMilliSecond / 10
                    timerView.updateCountdown(String.format("%02d",countdownSecond),
                        ((countdownMilliSecond / countdownFirst) * 100).toInt())
                }
            }


        }
    }

    override fun pauseTimer(){
        timer?.cancel()
        timer = null
    }

    override fun stopTimer(){
        timer?.cancel()

        //세는 변수 다 초기화
        currentSecond = 0
        currentMinute = 0
        milliSecondCount = 0

        CoroutineScope(Dispatchers.Main).launch { timerView.updateTimeText(defaultTimeText, "0") }
    }

    override fun countDownSetting(initTime: Int) {
        countdownMilliSecond = initTime * 10
        countdownFirst = initTime * 10.0

        CoroutineScope(Dispatchers.Main).launch {
            timerView.updateCountdown(String.format("%02d",initTime),
                ((countdownMilliSecond / countdownFirst) * 100).toInt())
        }
    }

    override fun getCurrentMilliTime(): Int {
        return currentMinute * 600 + currentSecond * 10 + milliSecondCount
    }


    //------------------------------------------------
    // 내부 함수

    private fun updateTimeValues(){
        milliSecondCount++ //밀리세컨드 증가

        //1초가 지나면
        if(milliSecondCount == 10) {
            milliSecondCount = 0
            currentSecond++

            //60초 -> 1분 증가
            if(currentSecond == 60){
                currentSecond = 0
                currentMinute++
            }
        }
    }

    private fun makeTimeFormat(minute: Int, second: Int): String {
        val formatMaker = StringBuilder()

        //분, 콜론, 초 추가
        formatMaker.append(String.format("%02d", minute))
        formatMaker.append(":")
        formatMaker.append(String.format("%02d", second))

        return formatMaker.toString()
    }
}