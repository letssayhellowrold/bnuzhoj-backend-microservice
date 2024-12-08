package com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.GoJudgeApi;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.JudgeInfo;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.question.JudgeConfig;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.GoJudgeStatusEnum;


import java.util.ArrayList;
import java.util.List;


/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class goJudgeCodeSandbox implements CodeSandbox {


    // 远程沙箱接口的根URL
    private static final String REMOTE_SANDBOX_URL = "http://119.29.172.40:5555";

    private  String runnableFileId;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        JudgeConfig judgeConfig = executeCodeRequest.getJudgeConfig();
        GoJudgeApi goJudgeApi = new GoJudgeApi();

        // 得到编译命令
        JSON compileCmd = goJudgeApi.compileCmd(language, code);

        // 执行编译
        String compileUrl = REMOTE_SANDBOX_URL + "/run";
        HttpResponse compileResponse = HttpRequest.post(compileUrl)
                .header("Content-Type", "application/json")
                .body(String.valueOf(compileCmd))
                .execute();

        // 获取响应体中的 JSON 对象
        String responseBody = compileResponse.body();
        JSONArray responseJsonArray = JSONUtil.parseArray(responseBody);// 转化为JSON数组
        // 遍历JSONArray
        for (int i = 0; i < responseJsonArray.size(); i++) {
            // 获取每个元素（JSONObject）
            JSONObject jsonObject = responseJsonArray.getJSONObject(i);
            // 编译错误
            if (jsonObject.getStr("status").equals(GoJudgeStatusEnum.Nonzero_Exit_Status.getValue())) {

                executeCodeResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
                executeCodeResponse.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getText());

                List<JudgeInfo> judgeInfoList = new ArrayList<>();
                JudgeInfo judgeInfoItem = new JudgeInfo();
                judgeInfoItem.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
                judgeInfoList.add(judgeInfoItem);
                executeCodeResponse.setJudgeInfo(judgeInfoList);
                return executeCodeResponse;
            } else {// 正常返回
                // 访问嵌套的JSONObject（runnableFileId）
                JSONObject fileIds = jsonObject.getJSONObject("fileIds");
                // 获得可运行文件的编号
                runnableFileId = fileIds.getStr("a");
            }
        }
        // 运行
        List<String> outputList = new ArrayList<>();
        List<JudgeInfo> judgeInfoList = new ArrayList<>();

        String runUrl = REMOTE_SANDBOX_URL + "/run";
        // 对每个测试点输入分别运行
        for (String input : inputList) {
            String runCmd = goJudgeApi.runCmd(runnableFileId, input, judgeConfig);
            HttpResponse runResponse = HttpRequest.post(runUrl)
                    .header("Content-Type", "application/json")
                    .body(runCmd)
                    .execute();
            // runResponse是成功的响应
            if (runResponse.isOk()) {
                String runResponseBody = runResponse.body();
                responseJsonArray = JSONUtil.parseArray(runResponseBody);// 转化为JSON数组
                // 遍历JSONArray
                for (int i = 0; i < responseJsonArray.size(); i++) {
                    // 获取每个元素（JSONObject）
                    JSONObject jsonObject = responseJsonArray.getJSONObject(i);
                    try {
                        // 访问键值对
                        String status = jsonObject.getStr("status");
//                    int exitStatus = jsonObject.getInt("exitStatus");// 退出码
//                    long cpuTime = jsonObject.getLong("time");// 内核时间
                        long memory = jsonObject.getLong("memory");
                        long runTime = jsonObject.getLong("runTime");// 运行时间（包括输入输出）

                        // 访问嵌套的JSONObject（files）
                        JSONObject files = jsonObject.getJSONObject("files");
//                    String stderr = files.getStr("stderr");
                        String stdout = files.getStr("stdout");

                        // 将信息更新到判题信息中
                        JudgeInfo judgeInfoItem = new JudgeInfo();

                        outputList.add(stdout);

                        judgeInfoItem.setMessage(status);
                        judgeInfoItem.setTime(runTime);
                        judgeInfoItem.setMemory(memory);

                        judgeInfoList.add(judgeInfoItem);
                    } catch (Exception e) {
                        // json结构出错，报告系统错误
                        JudgeInfo judgeInfoItem = new JudgeInfo();
                        outputList.add(JudgeInfoMessageEnum.SYSTEM_ERROR.getText());// 标准输出列表中进行零填充，保证长度一致
                        judgeInfoItem.setMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
                        judgeInfoList.add(judgeInfoItem);
                    }
                }
            } else {
                // 无响应。系统错误
                JudgeInfo judgeInfoItem = new JudgeInfo();
                outputList.add(JudgeInfoMessageEnum.SYSTEM_ERROR.getText());// 标准输出列表中进行零填充，保证长度一致
                judgeInfoItem.setMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
                judgeInfoList.add(judgeInfoItem);
            }
        }
        executeCodeResponse.setJudgeInfo(judgeInfoList);
        executeCodeResponse.setOutputList(outputList);
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());

        return executeCodeResponse;
    }
}