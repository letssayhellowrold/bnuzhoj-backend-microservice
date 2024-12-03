package com.bnuzhoj.bnuzhojbackendjudgeservice.judge;

import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.strategy.JudgeContext;
import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.JudgeInfo;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.QuestionSubmit;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    List<JudgeInfo> doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        // 根据情况调用不同的判题服务实现类
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();

        return judgeStrategy.doJudge(judgeContext);
    }

}
