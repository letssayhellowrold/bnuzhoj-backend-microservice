package com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox;


import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用代理类让代码沙箱能够按不同的调用方式下记录不同的日志
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;


    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        /**
         * 代理类先获取请求信息，再请求代码沙箱，最后记录响应。
         * 实际上是在原本调用代码沙箱这个操作上加了封装，使用方式没有改变但功能增加了
          */

        log.info("代码沙箱请求信息：" + executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息：" + executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
