package com.lancer.floatwindow

import android.app.ActivityManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.hjq.window.EasyWindow
import com.hjq.window.draggable.SpringDraggable

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

        findViewById<Button>(R.id.hang_up_btn).setOnClickListener {
            release()
            finishAndRemoveTask()

        }

        startTimer()
        MainActivity.isCalling=true
    }

    override fun onResume() {
        super.onResume()
        floatWindow?.let { fw ->
            if (fw.isShowing) {
                fw.cancel()
                MainActivity.isFloatWindowShow=false
            }
        }
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lancer","CallingActivity onDestroy")
    }

    private fun release() {
        floatWindow?.recycle()
        cancelTimer()
        MainActivity.isCalling = false
        MainActivity.isFloatWindowShow = false
    }

    /**
     * 禁止手势返回
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode== KeyEvent.KEYCODE_BACK && event?.repeatCount == 0){
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    //endregion

    //region 悬浮窗权限请求，以及悬浮窗创建

    private fun requestWindowPermission() {
        if (PermissionUtils.isGrantedDrawOverlays()) {
            createFloatWindow{

            }
        } else {
            PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    createFloatWindow{

                    }
                }

                override fun onDenied() {
                    //ToDo 请求失败会
                    Toast.makeText(this@CallActivity, "请求悬浮窗权限失败", Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }
    }

    private fun createFloatWindow(finishCreate:(()->Unit)) {
        floatWindow = EasyWindow<EasyWindow<*>>(MyApp.getMyApp()).apply {
            setContentView(R.layout.layout_call_float_window)
            // 设置成可拖拽的
            draggable = SpringDraggable(SpringDraggable.ORIENTATION_HORIZONTAL)
            // 设置显示时长
            setDuration(1000 * 60 * 60)
            setGravity(Gravity.END)
        }
        floatWindow?.setOnClickListener { window, view ->
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                startActivity(Intent(this, MainActivity::class.java))
                startActivity(Intent(this, CallActivity::class.java))
            }else{
                moveToFront()
            }
            floatWindow?.cancel()
            MainActivity.isFloatWindowShow = false
        }
        floatWindow?.show()
        moveTaskToBack(true)
        MainActivity.isFloatWindowShow = true
        finishCreate()
    }

    private fun moveToFront() {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        if (manager != null) {
            val recentTasks = manager.getRunningTasks(Int.MAX_VALUE)
            if (recentTasks != null && recentTasks.isNotEmpty()) {
                for (taskInfo in recentTasks) {
                    val cpn = taskInfo.baseActivity
                    if (null != cpn && TextUtils.equals(
                            CallActivity::class.java.name,
                            cpn.className
                        )
                    ) {
                        manager.moveTaskToFront(
                            taskInfo.id,
                            ActivityManager.MOVE_TASK_NO_USER_ACTION
                        )
                        break
                    }
                }
            }
        }
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

                timeTv.text = returnTime(millisUntilFinished)
                floatWindow?.let { fw ->
                    if (fw.isShowing) {
                        fw.setText(
                            R.id.float_time_tv,
                            returnTime(millisUntilFinished)
                        )
                    }
                }
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

    private fun returnTime(currentTime: Long): String {
        val min = (currentTime/(60*1000)).toInt()
        val second = ((currentTime % (60 * 1000)) / 1000).toInt()
        return "${formatString(min)}:${formatString(second)}"
    }

    private fun formatString(t: Int): String {
        val m: String = if (t > 0) {
            if (t < 10) {
                "0$t"
            } else {
                t.toString() + ""
            }
        } else {
            "00"
        }
        return m
    }


    //endregion
}