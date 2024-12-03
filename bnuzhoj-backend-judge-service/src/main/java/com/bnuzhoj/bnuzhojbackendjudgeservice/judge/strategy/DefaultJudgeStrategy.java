package com.bnuzhoj.bnuzhojbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.JudgeInfo;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.question.JudgeCase;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.question.JudgeConfig;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.Question;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.JudgeInfoMessageEnum;


import java.util.ArrayList;
import java.util.List;

/**
 * 默认判题策略
 */
public class DefaultJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    @Override
    public List<JudgeInfo> doJudge(JudgeContext judgeContext) {
        List<JudgeInfo> judgeInfoList = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        // 初始化result列表，长度与测试用例数量一致
        List<JudgeInfo> result = new ArrayList<>();
        for (int i = 0; i < judgeCaseList.size(); i++) {
            result.add(new JudgeInfo());
        }

        // 判断输出数量是否和预期输出数量相等
        if (outputList.size() != inputList.size()) {
            for (JudgeInfo judgeInfo : result) {
                judgeInfo.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
            }
            return result;
        }

        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                result.get(i).setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
            }
        }

        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit() * 1048576L; // 转换为字节
        Long needTimeLimit = judgeConfig.getTimeLimit() * 1000000L; // 转换为微秒

        for (int i = 0; i < judgeInfoList.size(); i++) {
            JudgeInfo judgeInfo = judgeInfoList.get(i);
            Long memory = judgeInfo.getMemory();
            Long time = judgeInfo.getTime();
            JudgeInfo judgeInfoResponse = result.get(i);

            if (memory > needMemoryLimit) {
                judgeInfoResponse.setMessage(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue());
            } else if (time > needTimeLimit) {
                judgeInfoResponse.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue());
            } else {
                judgeInfoResponse.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
                judgeInfoResponse.setMemory(memory);
                judgeInfoResponse.setTime(time);
            }
        }
        return result;
    }
}