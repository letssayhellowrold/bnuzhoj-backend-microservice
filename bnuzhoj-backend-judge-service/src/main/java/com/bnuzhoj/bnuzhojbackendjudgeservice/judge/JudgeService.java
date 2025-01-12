package com.bnuzhoj.bnuzhojbackendjudgeservice.judge;


import com.bnuzhoj.bnuzhojbackendmodel.model.entity.QuestionSubmit;

/**
 * 判题服务
 */
public interface JudgeService {

    /**
     * 判题
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
