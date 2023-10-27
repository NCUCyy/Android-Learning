package com.cyy.app.ch03

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.cyy.app.R
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(var icon: Int, var title: String, var content: String, var left: Boolean) :
    Parcelable

/**
 * 每一条消息---卡片
 */
@Composable
fun MessageCard(message: Message) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(5.dp)
    ) {
        // 判断是否是发送方：左右显示
        if (message.left) {
            // 受限布局
            LeftMessageCard(message)
        } else {
            // 受限布局
            RightMessageCard(message)
        }
    }
}

@Composable
fun LeftMessageCard(message: Message) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xc208f0f))
            .padding(top = 5.dp)
    ) {
        val (iconRef, titleRef, contentRef) = remember { createRefs() }
        val vGuideline = createGuidelineFromStart(0.2f)
        val hGuideline = createGuidelineFromTop(0.4f)
        Image(
            modifier = Modifier
                .size(60.dp, 60.dp)
                .clip(CircleShape)
                .background(Color.Blue)
                .constrainAs(iconRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(vGuideline)
                },
            painter = painterResource(id = message.icon),
            contentDescription = message.content
        )
        Text(
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                bottom.linkTo(hGuideline)
                start.linkTo(vGuideline)
                end.linkTo(parent.end)
            },
            text = message.title,
            fontSize = 16.sp
        )
        Text(
            modifier = Modifier.constrainAs(contentRef) {
                top.linkTo(hGuideline)
                bottom.linkTo(parent.bottom)
                start.linkTo(vGuideline)
                end.linkTo(parent.end)
            },
            text = message.content, fontSize = 16.sp
        )
    }
}

@Composable
fun RightMessageCard(message: Message) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xc208f0f))
            .padding(top = 5.dp)
    ) {
        val (iconRef, titleRef, contentRef) = remember { createRefs() }
        val vGuideline = createGuidelineFromStart(0.8f)
        val hGuideline = createGuidelineFromTop(0.4f)
        Image(
            modifier = Modifier
                .size(60.dp, 60.dp)
                .clip(CircleShape)
                .background(Color.Green)
                .constrainAs(iconRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(vGuideline)
                    end.linkTo(parent.end)
                },
            painter = painterResource(id = message.icon),
            contentDescription = message.content
        )
        Text(
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                bottom.linkTo(hGuideline)
                start.linkTo(parent.start)
                end.linkTo(vGuideline)
            },
            text = message.title,
            fontSize = 16.sp
        )
        Text(
            modifier = Modifier.constrainAs(contentRef) {
                top.linkTo(hGuideline)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(vGuideline)
            },
            text = message.content, fontSize = 16.sp
        )
    }
}

/**
 * 所有消息
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
// 状态提升
fun MessageContent(messageList: SnapshotStateList<Message>, sendOnClick: (Message) -> Unit) {
    val scope = rememberCoroutineScope()

    var inputTxt = remember {
        mutableStateOf("")
    }
    var lazyState = rememberLazyListState()

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = lazyState) {
            items(messageList) { message: Message ->
                MessageCard(message = message)
            }
        }
        // 输入框的布局
        val inputRef by remember {
            mutableStateOf(createRef())
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(5.dp)
                .background(Color.White)
                .constrainAs(inputRef) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        ) {
            TextField(
                modifier = Modifier
                    .width(300.dp)
                    .wrapContentHeight()
                    .background(Color(0xC8B5B3FF)),
                value = inputTxt.value,
                onValueChange = { it: String -> inputTxt.value = it },
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.White)
            )
            TextButton(
                modifier = Modifier
                    .width(100.dp)
                    .wrapContentHeight()
                    .shadow(2.dp)
                    .background(Color.DarkGray)
                    .border(
                        BorderStroke(1.dp, Color.Cyan)
                    ), onClick = {
                    scope.launch {
                        val msg =
                            Message(android.R.mipmap.sym_def_app_icon, "我", inputTxt.value, false)
                        // 滚动到最后一条消息
                        lazyState.scrollToItem(messageList.size - 1)
                        sendOnClick(msg)
                        // 清空输入框的内容
                        inputTxt.value = ""
                    }
                }
            ) {
                Row {
                    Text(text = "发送", fontSize = 16.sp, color = Color.White)
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

        }
    }

}

/**
 * 测试
 */
@Preview
@SuppressLint("UnrememberedMutableState")
@Composable
fun MessageScreen() {
    // SnapShotStateList是可变列表，对列表的任何增删改都将触发LazyColumn中的更新
    // 快照不能保存
    val messageList: SnapshotStateList<Message> = mutableStateListOf<Message>()

    messageList.add((Message(android.R.mipmap.sym_def_app_icon, "机器人", "你好！", true)))
    messageList.add((Message(android.R.mipmap.sym_def_app_icon, "机器人", "你好！", true)))
    messageList.add((Message(android.R.mipmap.sym_def_app_icon, "机器人", "你好！", true)))
    messageList.add((Message(android.R.mipmap.sym_def_app_icon, "机器人", "你好！", true)))
    // 调用方
    MessageContent(
        messageList = messageList,
        sendOnClick = { message -> messageList.add(message) })

}