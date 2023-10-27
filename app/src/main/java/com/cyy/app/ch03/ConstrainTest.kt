package com.cyy.app.ch03

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

data class Robot(var name: String, var introduction: String, val icon: Int)

@Composable
fun RobotCard(robot: Robot) {
    // TODO：点击每个Card，可以跳转到对用的页面
    // 包裹一层Card。可以设置更多的属性进行美化
    Card(
        // Card比其他组件高出多少---》阴影
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        // 容器背景颜色、内容颜色
        colors = CardDefaults.cardColors(containerColor = Color.Blue, contentColor = Color.White),
        // 形状---圆角
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.Gray),
        modifier = Modifier.padding(8.dp)
    ) {
        /**
         * 【受限布局的使用】
         * - 核心：通过【水平引导线、垂直引导线】和【父组件的四个边界】，确定每个组件的【上下左右的边界】
         * - 注意：使用前需要配置
         */
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            // 创建三个布局
            val (iconRef, nameRef, titleRef) = remember { createRefs() }
            // 垂直引导线：距离最左部在30%的位置
            val vGuideline = createGuidelineFromStart(0.3f)
            // 水平引导线：距离最顶部40%的位置
            val hGuideline = createGuidelineFromTop(0.4f)

            Image(
                painter = painterResource(id = robot.icon),
                contentDescription = robot.introduction,
                modifier = Modifier.constrainAs(iconRef) {
                    // 定义Image组件的上下左右的边界
                    // parent.top/bottom/start/end就是上一级组件的四个边界
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(vGuideline)
                }
            )
            Text(
                text = robot.name,
                fontSize = 20.sp,
                modifier = Modifier.constrainAs(nameRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(hGuideline)
                    start.linkTo(vGuideline)
                    end.linkTo(parent.end)
                }
            )
            Text(
                text = robot.introduction,
                fontSize = 18.sp,
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(hGuideline)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(vGuideline)
                    end.linkTo(parent.end)
                }
            )

        }
    }

}

@Composable
@Preview
fun TestScreen() {
    val robots = mutableListOf<Robot>()
    for (i in 0 until 20) {
        robots.add(
            Robot(
                "机器人${i + 1}",
                "机器人${i + 1}的介绍",
                android.R.mipmap.sym_def_app_icon
            )
        )
    }
    // 懒加载/可设置逆序显示
    LazyColumn(reverseLayout = true) {
        // 注意：不要导错包！！！List<>的那个
        items(robots) {
            RobotCard(it)
        }
    }
}