package com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.impl;

import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.JudgeInfo;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 示例代码沙箱（仅为了跑通业务流程）
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        List<JudgeInfo> juedgeInfoList = new ArrayList<>();
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        juedgeInfoList.add(judgeInfo);
        executeCodeResponse.setJudgeInfo(juedgeInfoList);
        return executeCodeResponse;
    }
}
