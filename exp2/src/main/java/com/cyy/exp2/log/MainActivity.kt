package com.cyy.exp2.log

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.cyy.exp2.memo.MainScreen

class MainActivity : ComponentActivity() {

    val TAG = "MAINACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        Log.d(TAG, "调用OnCreate()")

        setContent {

            Text("HELLO WORLD")

        }

    }

    override fun onStart() {

        super.onStart()

        Log.d(TAG, "调用OnStart()")

    }

    override fun onRestart() {

        super.onRestart()

        Log.d(TAG, "调用OnReStart()")

    }

    override fun onResume() {

        super.onResume()

        Log.d(TAG, "调用OnResume()")

    }

    override fun onPause() {

        super.onPause()

        Log.d(TAG, "调用OnPause()")

    }

    override fun onStop() {

        super.onStop()

        Log.d(TAG, "调用OnStop()")

    }

    override fun onDestroy() {

        super.onDestroy()

        Log.d(TAG, "调用OnDestroy()")

    }

}