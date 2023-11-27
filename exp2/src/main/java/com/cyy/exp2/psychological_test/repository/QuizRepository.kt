package com.cyy.exp2.psychological_test.repository

import com.cyy.exp2.psychological_test.pojo.Quiz


class QuizRepository {
    // 外部调用
    val categories = listOf("CET-4", "CET-6", "GRE", "IELTS", "TOEFL")

    // TODO---待修改
    val quizzes = listOf(Quiz("1", "A", listOf("A", "B", "C", "D")))

    // 15个
    private val CET_4 = listOf(
        Quiz("Accumulate", "A. 积累", listOf("A. 积累", "B. 分散", "C. 擦除", "D. 忽略")),
        Quiz("Conceal", "C. 隐藏", listOf("A. 揭示", "B. 显示", "C. 隐藏", "D. 照亮")),
        Quiz("Amplify", "D. 增加", listOf("A. 减少", "B. 扭曲", "C. 简化", "D. 增加")),
        Quiz("Diligent", "A. 勤奋的", listOf("A. 勤奋的", "B. 懒惰的", "C. 无能的", "D. 疏忽的")),
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

    // 20个
    private val CET_6 = listOf(
        Quiz("Abridge", "B. 删减", listOf("A. 增加", "B. 删减", "C. 完整", "D. 繁荣")),
        Quiz("Alleviate", "C. 缓解", listOf("A. 加重", "B. 恶化", "C. 缓解", "D. 持续")),
        Quiz("Amicable", "A. 友好的", listOf("A. 友好的", "B. 敌意的", "C. 冷淡的", "D. 不友好的")),
        Quiz(
            "Cacophony",
            "A. 刺耳的声音",
            listOf("A. 刺耳的声音", "B. 悠扬的音乐", "C. 安静的环境", "D. 悦耳的旋律")
        ),
        Quiz("Debilitate", "B. 使虚弱", listOf("A. 强健", "B. 使虚弱", "C. 增强", "D. 恢复")),
        Quiz("Deteriorate", "A. 恶化", listOf("A. 恶化", "B. 改善", "C. 加强", "D. 保持不变")),
        Quiz("Discrepancy", "D. 差异", listOf("A. 一致", "B. 同等", "C. 相似", "D. 差异")),
        Quiz("Exacerbate", "A. 恶化", listOf("A. 恶化", "B. 缓解", "C. 改善", "D. 修复")),
        Quiz("Facilitate", "C. 促进", listOf("A. 阻碍", "B. 妨碍", "C. 促进", "D. 阻止")),
        Quiz("Gregarious", "D. 群居的", listOf("A. 孤独的", "B. 内向的", "C. 独立的", "D. 群居的")),
        Quiz(
            "Hypothetical",
            "B. 假设的",
            listOf("A. 实际的", "B. 假设的", "C. 明确的", "D. 实证的")
        ),
        Quiz(
            "Inevitable",
            "B. 不可避免的",
            listOf("A. 可预测的", "B. 不可避免的", "C. 令人惊讶的", "D. 不重要的")
        ),
        Quiz("Juxtapose", "C. 并列", listOf("A. 分隔", "B. 隔开", "C. 并列", "D. 融合")),
        Quiz("Kaleidoscope", "A. 万花筒", listOf("A. 万花筒", "B. 单调", "C. 静态", "D. 昏暗")),
        Quiz("Lament", "B. 哀悼", listOf("A. 欢呼", "B. 哀悼", "C. 欢快", "D. 满足")),
        Quiz("Mitigate", "C. 缓解", listOf("A. 加剧", "B. 加强", "C. 缓解", "D. 延长")),
        Quiz("Nostalgia", "D. 怀旧", listOf("A. 现代感", "B. 激动", "C. 兴奋", "D. 怀旧")),
        Quiz("Opaque", "A. 不透明的", listOf("A. 不透明的", "B. 透明的", "C. 明亮的", "D. 模糊的")),
        Quiz("Pervasive", "C. 普遍的", listOf("A. 稀有的", "B. 特殊的", "C. 普遍的", "D. 局部的")),
        Quiz("Quell", "D. 压制", listOf("A. 鼓舞", "B. 刺激", "C. 鼓励", "D. 压制")),
        Quiz(
            "Resilient",
            "B. 有弹性的",
            listOf("A. 脆弱的", "B. 有弹性的", "C. 刚性的", "D. 坚硬的")
        )
    )

    // 20个
    private val GRE = listOf(
        Quiz(
            "Ubiquitous",
            "C. 无处不在",
            listOf("A. 稀有的", "B. 特殊的", "C. 无处不在", "D. 局部的")
        ),
        Quiz("Voracious", "B. 贪婪的", listOf("A. 节制的", "B. 贪婪的", "C. 知足的", "D. 满足的")),
        Quiz(
            "Whimsical",
            "A. 异想天开的",
            listOf("A. 异想天开的", "B. 严肃的", "C. 实际的", "D. 切实的")
        ),
        Quiz(
            "Xenophobia",
            "C. 仇外心理",
            listOf("A. 友好心理", "B. 同化心理", "C. 仇外心理", "D. 外向心理")
        ),
        Quiz("Yearn", "D. 渴望", listOf("A. 厌恶", "B. 轻视", "C. 忽视", "D. 渴望")),
        Quiz(
            "Zealous",
            "A. 热情的",
            listOf("A. 热情的", "B. 冷漠的", "C. 无动于衷的", "D. 不在意的")
        ),
        Quiz(
            "Acrimonious",
            "B. 尖刻的",
            listOf("A. 友好的", "B. 尖刻的", "C. 和善的", "D. 亲切的")
        ),
        Quiz(
            "Belligerent",
            "C. 好斗的",
            listOf("A. 和平的", "B. 友好的", "C. 好斗的", "D. 温和的")
        ),
        Quiz(
            "Cacophony",
            "A. 刺耳的声音",
            listOf("A. 刺耳的声音", "B. 悠扬的音乐", "C. 安静的环境", "D. 悦耳的旋律")
        ),
        Quiz("Dearth", "D. 缺乏", listOf("A. 富裕", "B. 充裕", "C. 丰富", "D. 缺乏")),
        Quiz("Aberrant", "B. 异常的", listOf("A. 正常的", "B. 异常的", "C. 典型的", "D. 寻常的")),
        Quiz("Capitulate", "C. 投降", listOf("A. 抵抗", "B. 反抗", "C. 投降", "D. 坚持")),
        Quiz("Desiccate", "A. 使干燥", listOf("A. 使干燥", "B. 湿润", "C. 浸湿", "D. 蒸发")),
        Quiz("Ephemeral", "C. 短暂的", listOf("A. 永久的", "B. 持久的", "C. 短暂的", "D. 长久的")),
        Quiz("Feckless", "D. 无能的", listOf("A. 有能力的", "B. 有力的", "C. 有效的", "D. 无能的")),
        Quiz(
            "Garrulous",
            "A. 喋喋不休的",
            listOf("A. 喋喋不休的", "B. 寡言的", "C. 沉默的", "D. 文静的")
        ),
        Quiz("Harangue", "C. 长篇大论", listOf("A. 赞美", "B. 私语", "C. 长篇大论", "D. 感叹")),
        Quiz(
            "Iconoclast",
            "B. 打破传统者",
            listOf("A. 尊重传统者", "B. 打破传统者", "C. 跟随传统者", "D. 挑战传统者")
        ),
        Quiz("Jettison", "D. 丢弃", listOf("A. 保留", "B. 携带", "C. 装载", "D. 丢弃")),
        Quiz("Kowtow", "A. 叩头", listOf("A. 叩头", "B. 行礼", "C. 鞠躬", "D. 举手")),
        Quiz("Lugubrious", "C. 愁绪的", listOf("A. 快乐的", "B. 活泼的", "C. 愁绪的", "D. 满足的")),
        Quiz(
            "Mellifluous",
            "B. 悠扬的",
            listOf("A. 刺耳的", "B. 悠扬的", "C. 吵闹的", "D. 喧嚣的")
        ),
        Quiz("Nefarious", "A. 邪恶的", listOf("A. 邪恶的", "B. 善良的", "C. 正直的", "D. 光明的")),
        Quiz("Obfuscate", "C. 使模糊", listOf("A. 阐明", "B. 明确", "C. 使模糊", "D. 澄清")),
        Quiz("Pernicious", "D. 有害的", listOf("A. 无害的", "B. 有利的", "C. 有益的", "D. 有害的")),
        Quiz(
            "Quixotic",
            "A. 不切实际的",
            listOf("A. 不切实际的", "B. 实际的", "C. 现实的", "D. 可行的")
        ),
        Quiz("Rancor", "B. 怨恨", listOf("A. 和解", "B. 怨恨", "C. 感激", "D. 友好")),
        Quiz("Sycophant", "C. 马屁精", listOf("A. 领导者", "B. 忠诚者", "C. 马屁精", "D. 勇士")),
        Quiz(
            "Ubiquitous",
            "C. 无处不在",
            listOf("A. 稀有的", "B. 特殊的", "C. 无处不在", "D. 局部的")
        ),
        Quiz("Voracious", "B. 贪婪的", listOf("A. 节制的", "B. 贪婪的", "C. 知足的", "D. 满足的"))
    )

    // 30个
    private val IELTS = listOf(
        Quiz("Abundant", "B. 丰富的", listOf("A. 缺乏的", "B. 丰富的", "C. 不足的", "D. 稀有的")),
        Quiz("Benevolent", "A. 仁慈的", listOf("A. 仁慈的", "B. 恶毒的", "C. 冷漠的", "D. 无情的")),
        Quiz(
            "Cacophony",
            "A. 刺耳的声音",
            listOf("A. 刺耳的声音", "B. 悠扬的音乐", "C. 安静的环境", "D. 悦耳的旋律")
        ),
        Quiz("Diligent", "C. 勤奋的", listOf("A. 懒惰的", "B. 散漫的", "C. 勤奋的", "D. 不努力的")),
        Quiz("Eloquent", "D. 雄辩的", listOf("A. 沉默的", "B. 朴实的", "C. 普通的", "D. 雄辩的")),
        Quiz("Facilitate", "B. 促进", listOf("A. 阻碍", "B. 促进", "C. 阻止", "D. 阻挠")),
        Quiz("Gregarious", "A. 群居的", listOf("A. 群居的", "B. 孤独的", "C. 独立的", "D. 合群的")),
        Quiz(
            "Hedonistic",
            "D. 享乐主义的",
            listOf("A. 苦行的", "B. 禁欲的", "C. 无趣的", "D. 享乐主义的")
        ),
        Quiz(
            "Ineffable",
            "B. 难以言表的",
            listOf("A. 可言表的", "B. 难以言表的", "C. 明了的", "D. 明白的")
        ),
        Quiz("Juxtapose", "C. 并列", listOf("A. 隔离", "B. 区分", "C. 并列", "D. 分隔")),
        Quiz("Keen", "A. 热衷的", listOf("A. 热衷的", "B. 冷漠的", "C. 无动于衷的", "D. 不在意的")),
        Quiz(
            "Lethargic",
            "D. 没精打采的",
            listOf("A. 充满活力的", "B. 活跃的", "C. 精力充沛的", "D. 没精打采的")
        ),
        Quiz(
            "Meticulous",
            "C. 一丝不苟的",
            listOf("A. 马虎的", "B. 粗心的", "C. 一丝不苟的", "D. 草率的")
        ),
        Quiz("Nefarious", "A. 邪恶的", listOf("A. 邪恶的", "B. 善良的", "C. 正直的", "D. 光明的")),
        Quiz(
            "Ostentatious",
            "D. 卖弄的",
            listOf("A. 谦逊的", "B. 低调的", "C. 简朴的", "D. 卖弄的")
        ),
        Quiz(
            "Plausible",
            "B. 貌似真实的",
            listOf("A. 不切实际的", "B. 貌似真实的", "C. 荒谬的", "D. 不可信的")
        ),
        Quiz("Quaint", "C. 古雅的", listOf("A. 简单的", "B. 普通的", "C. 古雅的", "D. 现代的")),
        Quiz("Reverence", "A. 尊敬", listOf("A. 尊敬", "B. 不敬", "C. 忽视", "D. 轻视")),
        Quiz("Sycophant", "C. 马屁精", listOf("A. 坚定者", "B. 忠实者", "C. 马屁精", "D. 勇士")),
        Quiz(
            "Taciturn",
            "D. 沉默寡言的",
            listOf("A. 外向的", "B. 口若悬河的", "C. 开朗的", "D. 沉默寡言的")
        )
    )

    // 30个
    private val TOEFL = listOf(
        Quiz("Alacrity", "B. 敏捷", listOf("A. 萎靡", "B. 敏捷", "C. 疲倦", "D. 迟缓")),
        Quiz("Benevolent", "A. 仁慈的", listOf("A. 仁慈的", "B. 恶毒的", "C. 冷漠的", "D. 无情的")),
        Quiz(
            "Cacophony",
            "A. 刺耳的声音",
            listOf("A. 刺耳的声音", "B. 悠扬的音乐", "C. 安静的环境", "D. 悦耳的旋律")
        ),
        Quiz("Debilitate", "B. 使虚弱", listOf("A. 强健", "B. 使虚弱", "C. 恢复", "D. 增长")),
        Quiz("Ephemeral", "C. 短暂的", listOf("A. 永久的", "B. 持久的", "C. 短暂的", "D. 长久的")),
        Quiz(
            "Facetious",
            "D. 滑稽的",
            listOf("A. 严肃的", "B. 意味深长的", "C. 乏味的", "D. 滑稽的")
        ),
        Quiz(
            "Garrulous",
            "A. 喋喋不休的",
            listOf("A. 喋喋不休的", "B. 寡言的", "C. 沉默的", "D. 文静的")
        ),
        Quiz("Harangue", "C. 长篇大论", listOf("A. 赞美", "B. 私语", "C. 长篇大论", "D. 感叹")),
        Quiz(
            "Ineffable",
            "B. 难以言表的",
            listOf("A. 可言表的", "B. 难以言表的", "C. 明了的", "D. 明白的")
        ),
        Quiz("Juxtapose", "C. 并列", listOf("A. 隔离", "B. 区分", "C. 并列", "D. 分隔")),
        Quiz("Kaleidoscope", "A. 万花筒", listOf("A. 万花筒", "B. 单调", "C. 静态", "D. 昏暗")),
        Quiz(
            "Lethargic",
            "D. 没精打采的",
            listOf("A. 充满活力的", "B. 活跃的", "C. 精力充沛的", "D. 没精打采的")
        ),
        Quiz(
            "Mellifluous",
            "B. 悠扬的",
            listOf("A. 刺耳的", "B. 悠扬的", "C. 吵闹的", "D. 喧嚣的")
        ),
        Quiz("Nefarious", "A. 邪恶的", listOf("A. 邪恶的", "B. 善良的", "C. 正直的", "D. 光明的")),
        Quiz("Obfuscate", "C. 使模糊", listOf("A. 阐明", "B. 明确", "C. 使模糊", "D. 澄清")),
        Quiz("Pernicious", "D. 有害的", listOf("A. 无害的", "B. 有利的", "C. 有益的", "D. 有害的")),
        Quiz(
            "Quixotic",
            "A. 不切实际的",
            listOf("A. 不切实际的", "B. 实际的", "C. 现实的", "D. 可行的")
        ),
        Quiz(
            "Resilient",
            "B. 有弹性的",
            listOf("A. 脆弱的", "B. 有弹性的", "C. 刚性的", "D. 坚硬的")
        ),
        Quiz("Sycophant", "C. 马屁精", listOf("A. 领导者", "B. 忠诚者", "C. 马屁精", "D. 勇士")),
        Quiz(
            "Taciturn",
            "D. 沉默寡言的",
            listOf("A. 外向的", "B. 口若悬河的", "C. 开朗的", "D. 沉默寡言的")
        ),
        Quiz(
            "Ubiquitous",
            "C. 无处不在",
            listOf("A. 稀有的", "B. 特殊的", "C. 无处不在", "D. 局部的")
        ),
        Quiz("Voracious", "B. 贪婪的", listOf("A. 节制的", "B. 贪婪的", "C. 知足的", "D. 满足的")),
        Quiz(
            "Whimsical",
            "A. 异想天开的",
            listOf("A. 异想天开的", "B. 严肃的", "C. 实际的", "D. 切实的")
        ),
        Quiz(
            "Xenophobia",
            "C. 仇外心理",
            listOf("A. 友好心理", "B. 同化心理", "C. 仇外心理", "D. 外向心理")
        ),
        Quiz("Yearn", "D. 渴望", listOf("A. 厌恶", "B. 轻视", "C. 忽视", "D. 渴望")),
        Quiz(
            "Zealous",
            "A. 热情的",
            listOf("A. 热情的", "B. 冷漠的", "C. 无动于衷的", "D. 不在意的")
        ),
        Quiz(
            "Acrimonious",
            "B. 尖刻的",
            listOf("A. 友好的", "B. 尖刻的", "C. 和善的", "D. 亲切的")
        ),
        Quiz(
            "Belligerent",
            "C. 好斗的",
            listOf("A. 和平的", "B. 友好的", "C. 好斗的", "D. 温和的")
        ),
        Quiz(
            "Cacophony",
            "A. 刺耳的声音",
            listOf("A. 刺耳的声音", "B. 悠扬的音乐", "C. 安静的环境", "D. 悦耳的旋律")
        )
    )

    private val quizzesDict = mapOf(
        "CET-4" to CET_4,
        "CET-6" to CET_6,
        "GRE" to GRE,
        "IELTS" to IELTS,
        "TOEFL" to TOEFL
    )

    // 外部调用：传入选择的种类，返回相应的题库
    fun getQuiz(category: String): List<Quiz> {
        return quizzesDict[category]!!
    }

}