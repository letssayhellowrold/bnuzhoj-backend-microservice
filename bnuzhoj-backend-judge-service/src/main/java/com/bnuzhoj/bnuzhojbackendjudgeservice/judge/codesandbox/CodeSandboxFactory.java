package com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox;


import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.impl.goJudgeCodeSandbox;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 * 静态工厂模式
 */
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱示例
     *
     * @param type 沙箱类型
     * @return
     */
    public static CodeSandbox newInstance(String type) {// 静态方法
        // 根据传入的类别，创建不同的对象
        switch (type) {
            case "goJudge":
                return new goJudgeCodeSandbox();
            case "example":
            default:
                return new ExampleCodeSandbox();
        }
    }
}
