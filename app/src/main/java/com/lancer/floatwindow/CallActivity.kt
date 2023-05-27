package com.lancer.floatwindow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import com.blankj.utilcode.util.PermissionUtils
import com.hjq.window.EasyWindow

class CallActivity : AppCompatActivity() {

    private var floatWindow: EasyWindow<*>? = null

    private lateinit var timeTv: TextView


    //region 生命周期方法

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("currentTime", currentTime)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        if (savedInstanceState != null) {
            currentTime = savedInstanceState.getLong("currentTime")
        }

        findViewById<Button>(R.id.create_float_window_btn).apply {
            setOnClickListener {
                requestWindowPermission()
            }
        }

        timeTv = findViewById(R.id.time_tv)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }

    override fun onDestroy() {
        super.onDestroy()
        floatWindow?.recycle()
        cancelTimer()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    //endregion

    //region 悬浮窗权限请求，以及悬浮窗创建

    private fun requestWindowPermission() {
        if (PermissionUtils.isGrantedDrawOverlays()) {
            createFloatWindow()
        } else {
            PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    createFloatWindow()
                }

                override fun onDenied() {
                    Toast.makeText(this@CallActivity, "请求悬浮窗权限失败", Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }
    }

    private fun createFloatWindow() {

    }


    //endregion


    //region 定时器

    private var timer: CountDownTimer? = null

    private var currentTime = 5 * 60 * 1000L

    private fun startTimer() {
        if (timer != null) {
            cancelTimer()
        }

        timer = object : CountDownTimer(currentTime, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                currentTime = millisUntilFinished
            }

            override fun onFinish() {
                //todo finish
            }

        }
        timer?.start()

    }

    private fun cancelTimer() {
        timer?.cancel()
        timer = null
    }


    //endregion
}