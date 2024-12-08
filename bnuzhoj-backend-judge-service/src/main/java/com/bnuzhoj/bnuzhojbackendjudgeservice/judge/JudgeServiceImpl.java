package com.bnuzhoj.bnuzhojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;

import com.bnuzhoj.bnuzhojbackendcommon.common.ErrorCode;
import com.bnuzhoj.bnuzhojbackendcommon.exception.BusinessException;
import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.strategy.JudgeContext;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.JudgeInfo;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.question.JudgeCase;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.question.JudgeConfig;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.Question;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.QuestionSubmit;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.bnuzhoj.bnuzhojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        // 创建沙箱实例
        CodeSandbox codeSandbox = new CodeSandboxProxy(CodeSandboxFactory.newInstance(type));// 工厂模式+代理模式
        // 获取沙箱需要的输入
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeConfigStr = question.getJudgeConfig();
        // 使用 Hutool 的 JSONUtil 将 JSON 字符串转换为 JudgeConfig 对象
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);

        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .judgeConfig(judgeConfig)
                .build();

        // 执行代码
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        // 编译失败
        if(executeCodeResponse.getStatus().equals(QuestionSubmitStatusEnum.FAILED.getValue()))
        {
            // 直接更新数据库并返回响应信息
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            // 将列表转换为由 \$ 分隔的字符串
            String judgeInfoString = executeCodeResponse.getJudgeInfo().get(0).toString();
            questionSubmitUpdate.setJudgeInfo(judgeInfoString);
            update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
            return questionFeignClient.getQuestionSubmitById(questionId);
        }

        List<String> outputList = executeCodeResponse.getOutputList();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setStatus(executeCodeResponse.getMessage());
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);

        List<JudgeInfo> judgeInfoList = judgeManager.doJudge(judgeContext);

        List<String> judgeInfoStrList = new ArrayList<>();
        boolean flag = true;
        for (JudgeInfo info : judgeInfoList) {
            if (!info.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getValue())) {
                flag = false;
            }
            judgeInfoStrList.add(info.toString());
        }

        if(flag){
            question.setAcceptedNum(question.getAcceptedNum() + 1);
            questionFeignClient.updateQuestionById(question);
        }

        // 6）修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        // 将列表转换为由 \$ 分隔的字符串
        String judgeInfoString = String.join("\\$", judgeInfoStrList);
        questionSubmitUpdate.setJudgeInfo(judgeInfoString);
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return questionFeignClient.getQuestionSubmitById(questionId);
    }
}
