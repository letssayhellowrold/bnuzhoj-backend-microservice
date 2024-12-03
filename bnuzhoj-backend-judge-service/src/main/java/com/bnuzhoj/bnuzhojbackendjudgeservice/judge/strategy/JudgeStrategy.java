package com.bnuzhoj.bnuzhojbackendjudgeservice.judge.strategy;


import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.JudgeInfo;
import java.util.List;

/**
 * 判题策略
 */
public interface JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    List<JudgeInfo> doJudge(JudgeContext judgeContext);
}
