package com.bnuzhoj.bnuzhojbackendjudgeservice.judge.strategy;


import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.JudgeInfo;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.question.JudgeCase;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.Question;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private String status;// 测评机返回状态

    private List<JudgeInfo> judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
