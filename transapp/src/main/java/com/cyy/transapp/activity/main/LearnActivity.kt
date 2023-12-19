package com.cyy.transapp.activity.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cyy.app.word_bank.model.WordItem
import com.google.gson.Gson

class LearnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", 0)
        val vocabulary = intent.getStringExtra("vocabulary")!!
        val allWordsStr = intent.getStringExtra("allWordsStr")!!
        val allWords = Gson().fromJson(allWordsStr, Array<WordItem>::class.java).toList()
        Log.i("LearnActivity", "allWords: $allWords")

        setContent {
//            LearnMainScreen(userId, vocabulary)
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LearnMainScreen(userId: Int, vocabulary: String) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    // TODO：显示查询的词汇
//                    Text(text = query, fontWeight = FontWeight.Bold, fontSize = 25.sp, maxLines = 1)
//                },
//                // 左侧图标
//                navigationIcon = {
//                    // 图标按钮
//                    IconButton(onClick = {
//                        // TODO：返回查词页面
//                        context.finish()
//                    }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.back),
//                            contentDescription = null,
//                        )
//                    }
//                },
//                actions = {
//                    if (isStared.value) {
//                        IconButton(onClick = {
//                            // TODO：取消收藏
//                            transViewModel.unstarWord()
//                            Toast.makeText(context, "取消成功！", Toast.LENGTH_SHORT).show()
//                        }, modifier = Modifier.padding(end = 16.dp)) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.star_fill),
//                                contentDescription = null,
//                                Modifier.size(30.dp),
//                                tint = Color(0xFFE8C11C)
//                            )
//                        }
//                    } else {
//                        IconButton(onClick = {
//                            // TODO：收藏
//                            transViewModel.starWord()
//                            Toast.makeText(context, "收藏成功！", Toast.LENGTH_SHORT).show()
//                        }, modifier = Modifier.padding(end = 16.dp)) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.star),
//                                contentDescription = null,
//                                Modifier.size(30.dp),
//                                tint = Color(0xFFE8C11C)
//                            )
//                        }
//                    }
//                }
//            )
//        },
//        content = {
//            // 页面的主体部分
//            Box(modifier = Modifier.padding(it)) {
//                ContentScreen(query, userId, transViewModel)
//            }
//        },
//        floatingActionButton = {
//        })
//}