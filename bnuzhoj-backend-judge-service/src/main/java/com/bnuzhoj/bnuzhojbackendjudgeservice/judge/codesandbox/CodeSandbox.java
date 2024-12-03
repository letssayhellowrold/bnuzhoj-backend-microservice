package com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox;

import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeRequest;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
