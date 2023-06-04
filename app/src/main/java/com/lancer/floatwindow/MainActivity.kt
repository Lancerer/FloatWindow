package com.lancer.floatwindow

import android.app.ActivityManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    companion object{
        var isFloatWindowShow:Boolean=false

        var isCalling:Boolean=false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.go_to_call).apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, CallActivity::class.java))
            }
        }

    }

    override fun onResume() {
        super.onResume()
        gotoCallActivity()
    }

    private fun gotoCallActivity() {
        if (isCalling && !isFloatWindowShow) {
            view().postDelayed({
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startActivity(Intent(this, CallActivity::class.java))
                } else {
                    moveToFront()
                }
            }, 50)
        }
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

    private fun view() : View {
        return window.decorView.rootView
    }


}