package com.example.stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.stopwatch.databinding.ActivityMainBinding
import com.example.stopwatch.databinding.DialogCountdownBinding

class MainActivity : AppCompatActivity(), TimerContract.TimerView {
    //바인딩
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //타이머 로직 객체
    private val timerLogic:TimerContract.TimerPresenter = TimerLogic(this)


    //--------------------------------------------
    // 생명주기 콜백 함수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //버튼 시작 함수
        initListeners()
    }


    //--------------------------------------------
    // 초기화 함수

    private fun initListeners() {
        //시작 버튼 클릭 시
        binding.startButton.setOnClickListener {
            //타이머 시작
            timerLogic.startTimer()

            //버튼 변경
            it.visibility = View.GONE
            binding.pauseButton.visibility = View.VISIBLE
            binding.stopButton.visibility = View.GONE
            binding.lapButton.visibility = View.VISIBLE
        }

        //일시 중지 버튼 클릭 시
        binding.pauseButton.setOnClickListener {
            //타이머 일시 중지
            timerLogic.pauseTimer()

            //버튼 변경
            it.visibility = View.GONE
            binding.startButton.visibility = View.VISIBLE
            binding.stopButton.visibility = View.VISIBLE
            binding.lapButton.visibility = View.GONE
        }

        //중지 버튼 클릭 시
        binding.stopButton.setOnClickListener {
            //타이머 중지
            timerLogic.stopTimer()

            //카운트다운 초기화
            timerLogic.countDownSetting(0)

            //랩 초기화
            binding.lapContainerLinearLayout.removeAllViews()

            //버튼 변경
            binding.lapButton.visibility = View.GONE
            binding.startButton.visibility = View.VISIBLE
            binding.pauseButton.visibility = View.GONE
        }

        //랩 버튼 클릭 시
        binding.lapButton.setOnClickListener {
            //타이머 기록
            lap()
        }

        //카운트다운 글자 클릭 시
        binding.countdownTextView.setOnClickListener {
            showCountdownDialog()
        }

    }


    //------------------------------------------
    // 오버라이딩 함수 영역

    override fun updateTimeText(timeText: String, milliText: String) {
        binding.timeTextView.text = timeText
        binding.tickTextView.text = milliText
    }

    override fun updateCountdown(curTimeText: String, countDownProgress: Int) {
        binding.countdownTextView.text = curTimeText
        binding.countdownProgressBar.progress = countDownProgress
    }


    //------------------------------------------
    // 내부 함수 영역

    private fun showCountdownDialog(){
        AlertDialog.Builder(this).apply {
            val dialogBinding = DialogCountdownBinding.inflate(layoutInflater)
            with(dialogBinding.countdownSecondPicker) {
                maxValue = 20
                minValue = 0
                value = 5
                wrapSelectorWheel = false
            }

            setTitle("카운트다운 설정")
            setView(dialogBinding.root)
            setPositiveButton("설정") { _, _ ->
                timerLogic.countDownSetting(dialogBinding.countdownSecondPicker.value)
            }
            setNegativeButton("취소", null)
        }.show()
    }

    private fun lap() {
        val currentMilliTime = timerLogic.getCurrentMilliTime()
        if (currentMilliTime == 0) return

        //시간 값 얻기
        val minutes = currentMilliTime.div(10) / 60
        val seconds = currentMilliTime.div(10) % 60
        val deciSeconds = currentMilliTime % 10

        val container = binding.lapContainerLinearLayout
        TextView(this).apply {
            textSize = 20f
            gravity = Gravity.CENTER

            val lapText = "${container.childCount.inc()}. " + String.format("%02d:%02d %01d", minutes, seconds, deciSeconds)
            text = lapText
            setPadding(30, 10,10,10)
        }.let { labTextView ->
            container.addView(labTextView, 0)
        }
    }
}