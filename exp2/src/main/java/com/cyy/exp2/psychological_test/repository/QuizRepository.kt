package com.cyy.exp2.psychological_test.repository

import com.cyy.exp2.psychological_test.pojo.Quiz


class QuizRepository {
    val quizzes = listOf(
        Quiz(
            "Inevitable",
            "B. 不可避免的",
            listOf("A. 可预测的", "B. 不可避免的", "C. 令人惊讶的", "D. 不重要的")
        ),
        Quiz("Accumulate", "A. 积累", listOf("A. 积累", "B. 分散", "C. 擦除", "D. 忽略")),
        Quiz("Conceal", "C. 隐藏", listOf("A. 揭示", "B. 显示", "C. 隐藏", "D. 照亮")),
        Quiz("Amplify", "D. 增加", listOf("A. 减少", "B. 扭曲", "C. 简化", "D. 增加")),
        Quiz("Diligent", "A. 勤奋的", listOf("A. 勤奋的", "B. 懒惰的", "C. 无能的", "D. 疏忽的")),
        Quiz(
            "Rudimentary",
            "B. 基本的",
            listOf("A. 复杂的", "B. 基本的", "C. 先进的", "D. 复杂的")
        ),
        Quiz(
            "Exemplify",
            "A. 举例说明",
            listOf("A. 举例说明", "B. 反驳", "C. 使复杂化", "D. 使无效")
        ),
        Quiz("Mitigate", "C. 缓解", listOf("A. 加剧", "B. 加强", "C. 缓解", "D. 延长")),
        Quiz(
            "Vigorous",
            "D. 有活力的",
            listOf("A. 虚弱的", "B. 昏睡的", "C. 懒惰的", "D. 有活力的")
        ),
        Quiz("Replenish", "B. 补充", listOf("A. 空的", "B. 补充", "C. 耗尽", "D. 排水")),
        Quiz("Ponder", "A. 思考", listOf("A. 思考", "B. 忽略", "C. 忘记", "D. 不顾")),
        Quiz(
            "Ubiquitous",
            "C. 无处不在",
            listOf("A. 无处不在", "B. 某处", "C. 无处不在", "D. 任何地方")
        ),
        Quiz("Dismantle", "B. 拆卸", listOf("A. 组装", "B. 拆卸", "C. 建造", "D. 创造")),
        Quiz("Intricate", "A. 复杂的", listOf("A. 复杂的", "B. 简单的", "C. 清晰的", "D. 明显的")),
        Quiz("Scrutinize", "C. 仔细检查", listOf("A. 忽略", "B. 一瞥", "C. 仔细检查", "D. 忽视")),
        Quiz("Concur", "A. 同意", listOf("A. 同意", "B. 不同意", "C. 争论", "D. 反对")),
        Quiz(
            "Pragmatic",
            "D. 实际的",
            listOf("A. 理论的", "B. 不切实际的", "C. 不现实的", "D. 实际的")
        ),
        Quiz("Resilient", "B. 灵活的", listOf("A. 易碎的", "B. 灵活的", "C. 脆弱的", "D. 刚性的")),
        Quiz("Incessant", "C. 不断的", listOf("A. 间歇的", "B. 偶尔的", "C. 不断的", "D. 有时的")),
        Quiz(
            "Deceptive",
            "A. 欺骗性的",
            listOf("A. 欺骗性的", "B. 老实的", "C. 敞开的", "D. 明确的")
        )
    )
}