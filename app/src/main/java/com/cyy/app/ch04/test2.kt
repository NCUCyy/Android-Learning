package com.cyy.app.ch04

import android.content.Intent
import android.content.LocusId
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerArrayResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.parcelize.Parcelize

@Parcelize
data class T(var id: Int) : Parcelable

@Parcelize
class TVM() : ViewModel(), Parcelable {
    private val _t: MutableStateFlow<SnapshotStateList<T>> = MutableStateFlow(mutableStateListOf())
    val t = _t.asStateFlow()

    init {
        _t.value.add(T(123))
    }

    fun update(id: Int) {
        for (i in 0.._t.value.size) {
            if (_t.value[i].id == id) {
                Log.i("13222222222222222222222222222222222", "123")
                _t.value[i].id = 999
                break
            }
        }
    }
}

class Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用ActivityResultLauncher进行意图跳转
        val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            // 意图结束后，执行这个「回调函数」
            ActivityResultCallback {
            }
        )
        setContent {
            Screen(resultLauncher)
            Screen2()
        }
    }
}


@Composable
fun Screen(resultLauncher: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current as Activity
    val vm: TVM = viewModel()
    Log.i("vm1", vm.toString())
}

@Composable
fun Screen2() {
    val vm: TVM = viewModel()
    Log.i("vm2", vm.toString())
}

