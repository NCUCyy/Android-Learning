package com.cyy.exp2.daily_word_app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cyy.exp2.R
import com.cyy.exp2.daily_word_app.PsychologicalTestApp
import com.cyy.exp2.daily_word_app.pojo.Record
import com.cyy.exp2.daily_word_app.pojo.User
import com.cyy.exp2.daily_word_app.view_model.RecordViewModel
import com.cyy.exp2.daily_word_app.view_model.SentenceViewModel
import com.cyy.exp2.daily_word_app.view_model.UserScreenViewModel
import com.cyy.exp2.daily_word_app.view_model.UserScreenViewModelFactory
import com.cyy.exp2.daily_word_app.view_model.UserViewModel
import com.cyy.exp2.daily_word_app.view_model.UserViewModelFactory
import java.time.format.DateTimeFormatter

val screens = listOf(Screen.HomePage, Screen.HistoryPage, Screen.UserPage)

/**
 *Screen类（与用于显示的Screen实体不同！要区分开！Screen类只用于提供页面需要的元数据metaData：icon、title、"route"【用于导航】）
 */
sealed class Screen(
    val route: String,
    val label: String,
    val title: String,
    val icon: Int
) {
    object HomePage :
        Screen(route = "home", label = "首页", title = "欢迎回来👏🏻", icon = R.drawable.home)

    object HistoryPage :
        Screen(route = "testHistory", label = "历史", title = "答题记录", icon = R.drawable.history)

    object UserPage :
        Screen(
            route = "user",
            label = "我的",
            title = "个人主页",
            icon = R.drawable.person
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelect(recordViewModel: RecordViewModel) {
    val application = LocalContext.current.applicationContext as PsychologicalTestApp
    val categories = application.quizRepository.categories
    var expanded by remember { mutableStateOf(false) }
    val curSelect = recordViewModel.curCategory.collectAsState().value
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Display selected option
            OutlinedTextField(
                value = curSelect,
                onValueChange = {},
                label = {
                    Text("选择题库")
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            expanded = true
                        }
                    )
                },
                readOnly = true
            )
            // Dropdown menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                categories.forEach {
                    DropdownMenuItem(onClick = {
                        recordViewModel.updateCurCategory(it)
                        expanded = false
                    }, text = {
                        Text(it)
                    })
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    resultLauncher: ActivityResultLauncher<Intent>,
    recordViewModel: RecordViewModel
) {
    // 永远获得同一个ViewModel----前提是在一个Activity之内
    // 创建一个 ViewModelProvider 实例
    val viewModelProvider = ViewModelProvider(LocalContext.current as ViewModelStoreOwner)
    // 获取指定类型的 ViewModel
    val sentenceViewModel = viewModelProvider[SentenceViewModel::class.java]

    val sentence = sentenceViewModel.sentence.collectAsState().value

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .padding(10.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
        ) {
            Row {
                Text(
                    text = "每日一句",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 40.sp,
                        fontStyle = FontStyle.Italic
                    ),
                )
                IconButton(modifier = Modifier.padding(top = 20.dp), onClick = {
                    sentenceViewModel.shuffleSentence()
                }) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                }
            }
            AsyncImage(
                model = sentence.data.pic,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.padding(10.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = sentence.data.en,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )
            Text(
                text = sentence.data.zh,
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier.padding(10.dp)
            )
        }
        // 开始按钮
        Button(
            onClick = {
                // Activity跳转到答题界面QuizActivity
                val intent = Intent(context as Activity, QuizActivity::class.java)
                intent.putExtra("category", recordViewModel.curCategory.value)
                intent.putExtra("username", recordViewModel.loginUser.value!!.username)
                resultLauncher.launch(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF66CDAA),
                contentColor = Color.White
            )
        ) {
            Text("Get Started", fontSize = 20.sp)
        }
        // 选择题库
        CategorySelect(recordViewModel)
    }
}

@Composable
fun HistoryScreen(records: State<List<Record>>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        LazyColumn {
            items(records.value.reversed()) { record ->
                RecordCard(record)
            }
        }
    }
}


@SuppressLint("RememberReturnType")
@Composable
fun RecordCard(record: Record) {
    val containColorState = remember { mutableStateOf(Color.White) }
    val contentColorState = remember { mutableStateOf(Color.Black) }
    if (record.right < 5) {
        containColorState.value = Color(0xFFF70F5E)
        contentColorState.value = Color.White
    } else {
        containColorState.value = Color(0xFF0DFCA9)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                // TODO
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            var (scoreRef, categoryRef, timeRef) = remember {
                createRefs()
            }
            val vGuideline = createGuidelineFromStart(0.3f)
            val hGuideline = createGuidelineFromTop(0.7f)

            Card(shape = RoundedCornerShape(40.dp),
                colors = CardDefaults.cardColors(
                    containerColor = containColorState.value,
                    contentColor = contentColorState.value
                ),
                modifier = Modifier
                    .size(80.dp)
                    .padding(10.dp)
                    .constrainAs(scoreRef) {
                        start.linkTo(parent.start)
                        end.linkTo(vGuideline)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "${record.right}/20",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Text(
                text = record.category,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .constrainAs(categoryRef) {
                        start.linkTo(vGuideline)
                        top.linkTo(parent.top)
                        bottom.linkTo(hGuideline)
                    }
                    .padding(start = 32.dp)
            )
            // 转化时间表示方式，用于显示
            val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日  HH:mm:ss")
            val testTime = record.testTime.format(formatter)
            Text(
                text = "答题时间：${testTime}",
                color = Color.Gray,
                modifier = Modifier
                    .constrainAs(timeRef) {
                        start.linkTo(vGuideline)
                        end.linkTo(parent.end)
                        top.linkTo(hGuideline)
                        bottom.linkTo(parent.bottom)
                    })
        }
    }
}

// 3、用户界面
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun UserScreen(loginUser: User = User("cyy", "cyy", "男"), userViewModel: UserViewModel? = null) {
    val application = LocalContext.current.applicationContext as PsychologicalTestApp
    val context = LocalContext.current as ComponentActivity
    // 用于存储页面数据
    val userScreenViewModel = viewModel<UserScreenViewModel>(
        factory = UserScreenViewModelFactory(
            loginUser
        )
    )
    // 用于持久化修改后的数据
    val userViewModel = viewModel<UserViewModel>(
        factory = UserViewModelFactory(
            application.userRepository
        )
    )
    val username = userScreenViewModel.username.collectAsState().value
    val password = userScreenViewModel.password.collectAsState().value

    userViewModel.updateRes.observe(context) {
        if (userViewModel.updateRes.value!!) {
            Toast.makeText(context, "修改成功！", Toast.LENGTH_LONG).show()
            userViewModel.updateRes.value = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Image(
                painter = painterResource(id = android.R.mipmap.sym_def_app_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
            )
            Text(
                text = loginUser.username,
                fontSize = 50.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 20.dp),
//            shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {

                ConstraintLayout {
                    var (titleRef, valueRef) = remember {
                        createRefs()
                    }
                    val vGuideline = createGuidelineFromStart(0.4f)
                    Column(modifier = Modifier.constrainAs(titleRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(vGuideline)
                    }, horizontalAlignment = Alignment.End) {
                        Spacer(modifier = Modifier.height(50.dp))
                        Text(
                            text = "用户名：",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(end = 10.dp),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "密码：",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(end = 10.dp),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "性别：",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(end = 10.dp),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "答题次数：",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 10.dp),
                            color = Color.Gray
                        )
                    }

                    Column(modifier = Modifier
                        .padding(end = 10.dp)
                        .constrainAs(valueRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(vGuideline)
                            end.linkTo(parent.end)
                        }) {
                        Spacer(modifier = Modifier.height(25.dp))
                        InputModifyBox(
                            input = username,
                            action = { userScreenViewModel.updateUsername(it) }, readOnly = true
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        InputModifyBox(
                            input = password,
                            action = { userScreenViewModel.updatePassword(it) }, readOnly = false
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = loginUser.sex,
                            modifier = Modifier
                                .padding(end = 10.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = loginUser.testTurns.toString(),
                            modifier = Modifier.padding(end = 10.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }
        Button(
            onClick = {
                loginUser.username = username
                loginUser.password = password
                userViewModel.update(loginUser)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF66CDAA),
                contentColor = Color.White
            )
        ) {
            Text("保存修改", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(id = R.drawable.done),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputModifyBox(input: String, action: (String) -> Unit, readOnly: Boolean) {
    Card(shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp)) {
        TextField(
            value = input,
            modifier = Modifier.size(width = 200.dp, height = 60.dp),
            onValueChange = {
                action(it)
            },
            singleLine = true, // 单行文本框
            shape = MaterialTheme.shapes.extraSmall, // 设置边框形状
            textStyle = TextStyle.Default.copy(color = Color.Black, fontSize = 20.sp), // 设置文本颜色
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                placeholderColor = Color.Gray,
            ),
            readOnly = readOnly,
        )
    }
}
