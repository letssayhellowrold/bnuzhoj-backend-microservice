package com.bnuzhoj.bnuzhojbackendserviceclient.service;

import com.bnuzhoj.bnuzhojbackendmodel.model.entity.Question;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
* @author Lenovo
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2024-11-04 12:28:19
*/
@FeignClient(name = "bnuzhoj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient{

    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId);

    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);

    /**
     * 根据Question对象更新数据库中的题目信息
     * @param question 要更新的Question对象
     * @return 更新成功返回true，否则返回false
     */
    @PostMapping("/question/update")
    boolean updateQuestionById(@RequestBody Question question);
}
