package com.example.stopwatch

sealed interface TimerContract{
    //-- 타이머 프레젠터 --//
    interface TimerPresenter{
        fun startTimer()
        fun pauseTimer()
        fun stopTimer()
        fun countDownSetting(initTime: Int)
        fun getCurrentMilliTime(): Int
    }

    //-- 타이머 뷰 --//
    interface TimerView{
        fun updateTimeText(timeText: String, milliText: String)
        fun updateCountdown(curTimeText: String, countDownProgress: Int)
    }
}