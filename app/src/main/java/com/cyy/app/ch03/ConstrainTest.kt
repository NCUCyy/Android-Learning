package com.cyy.app.ch03

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

data class Robot(var name: String, var introduction: String, val icon: Int)

@Composable
@Preview
fun RobotCard() {
    var robot = Robot("机器人", "机器人的介绍", android.R.mipmap.sym_def_app_icon)
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