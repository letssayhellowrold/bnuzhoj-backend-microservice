package com.bnuzhoj.bnuzhojbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.JudgeInfo;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.question.JudgeCase;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.question.JudgeConfig;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.Question;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.GoJudgeStatusEnum;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.JudgeInfoMessageEnum;


import java.util.ArrayList;
import java.util.List;

/**
 * 默认判题策略
 */
public class DefaultJudgeStrategy implements JudgeStrategy {
    @Override
    public List<JudgeInfo> doJudge(JudgeContext judgeContext) {
        List<JudgeInfo> judgeInfoList = judgeContext.getJudgeInfo();
//        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        // 初始化result列表，长度与测试用例数量一致
        List<JudgeInfo> result = new ArrayList<>();
        // 检查每一个判题信息和对应的输出
        for(int i = 0;i<judgeInfoList.size();i++)
        {
            JudgeInfo judgeInfoItem = judgeInfoList.get(i);
            String outputItem = outputList.get(i);
            String stdOutputItem = judgeCaseList.get(i).getOutput();
            JudgeInfo resultItem = new JudgeInfo();
            Long costMemory = judgeInfoItem.getMemory();
            Long costTime = judgeInfoItem.getTime();
            Long costStack = judgeInfoItem.getStack();
            // 先检查状态

            // 非正常退出
            if(!judgeInfoItem.getMessage().equals(GoJudgeStatusEnum.ACCEPTED.getValue()))
            {
                resultItem.setMessage(judgeInfoItem.getMessage());
                if(resultItem.getMessage().equals(GoJudgeStatusEnum.Signalled.getValue()))
                {
                    resultItem.setMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue());// 将表述修改为更通用的RE
                }
            }
            else{
                // 正常退出说明有输出，先检查运行消耗是否符合题目限制，再看答案是否正确
                String judgeConfigStr = question.getJudgeConfig();
                JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
                long limitMemory = judgeConfig.getMemoryLimit() * 1024L; // 转换为字节
                long limitTime = judgeConfig.getTimeLimit() * 1000000L; // 转换为微秒
//                Long limitStack = judgeConfig.getStackLimit() * 1024L;



                if(limitMemory<costMemory)// 超出内存限制
                {
                    resultItem.setMessage(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue());
                }
                else if(limitTime<costTime)// 超时
                {
                    resultItem.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue());
                }
                else
                {
                    // 符合判题要求，开始检查答案是否正确

                    // 消除行末换行
                    if(outputItem.endsWith("\n"))outputItem = outputItem.substring(0, outputItem.length() - 1);
                    if(stdOutputItem.endsWith("\n"))stdOutputItem = stdOutputItem.substring(0, stdOutputItem.length() - 1);

                    if(outputItem.equals(stdOutputItem))resultItem.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
                    else resultItem.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
                }
            }
            // 只在正确情况下记录资源消耗
            if(resultItem.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getValue()))
            {
                resultItem.setMemory(costMemory);
                resultItem.setTime(costTime);
                resultItem.setStack(costStack);
            }
            // 更新结果列表
            result.add(resultItem);
        }
        return result;
    }
}