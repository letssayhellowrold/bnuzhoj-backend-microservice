package com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bnuzhoj.bnuzhojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.GoJudgeApi;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.JudgeInfo;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.JudgeInfoMessageEnum;

import java.util.ArrayList;
import java.util.List;


/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class RemoteCodeSandbox implements CodeSandbox {


    // 远程沙箱接口的根URL
    private static final String REMOTE_SANDBOX_URL = "http://119.29.172.40:5555";


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        System.out.println("远程判题启动");

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        GoJudgeApi goJudgeApi = new GoJudgeApi();
        String fileId = "";

        // 得到编译命令
        String compileCmd = goJudgeApi.compileCmd(language, code);
//        System.out.println("编译命令"+compileCmd);

        String compileUrl = REMOTE_SANDBOX_URL + "/run";
        HttpResponse compileResponse = HttpRequest.post(compileUrl)
                .header("Content-Type", "application/json")
                .body(compileCmd)
                .execute();
//        System.out.println("请求返回值"+compileResponse);
        // 检查响应状态
        // 获取响应体中的 JSON 对象
        String responseBody = compileResponse.body();
        try {
            JSONArray responseJsonArray = JSONUtil.parseArray(responseBody);// 转化为JSON数组
            // 遍历JSONArray
            for (int i = 0; i < responseJsonArray.size(); i++) {
                // 获取每个元素（JSONObject）
                JSONObject jsonObject = responseJsonArray.getJSONObject(i);
                // 访问嵌套的JSONObject（fileIds）
                JSONObject fileIds = jsonObject.getJSONObject("fileIds");
                fileId = fileIds.getStr("a");
            }
        } catch (Exception e) {
            // 处理解析错误，例如打印日志或返回错误信息
            executeCodeResponse.setStatus(510);
            executeCodeResponse.setMessage("程序编译报错");
            System.out.println("编译错误信息"+e.getMessage());
            List<JudgeInfo> judgeInfoList = new ArrayList<>();
            JudgeInfo judgeInfoItem = new JudgeInfo();
            judgeInfoItem.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            judgeInfoList.add(new JudgeInfo());
            executeCodeResponse.setJudgeInfo(judgeInfoList);
            return executeCodeResponse;
        }

        // 运行
        List<String> outputList = new ArrayList<>();
        List<JudgeInfo> judgeInfoList = new ArrayList<>();

        String runUrl = REMOTE_SANDBOX_URL + "/run";
        for (String input : inputList) {
            String runCmd = goJudgeApi.runCmd(fileId,input);
            HttpResponse runResponse = HttpRequest.post(runUrl)
                    .header("Content-Type", "application/json")
                    .body(runCmd)
                    .execute();
            System.out.println("运行响应"+runResponse);
            // 假设runResponse是成功的响应
            if (runResponse.isOk()) {
                String runResponseBody = runResponse.body();
                try {
                    JSONArray responseJsonArray = JSONUtil.parseArray(runResponseBody);// 转化为JSON数组
                    // 遍历JSONArray
                    for (int i = 0; i < responseJsonArray.size(); i++) {
                        // 获取每个元素（JSONObject）
                        JSONObject jsonObject = responseJsonArray.getJSONObject(i);
                        // 访问键值对
                        String status = jsonObject.getStr("status");
                        int exitStatus = jsonObject.getInt("exitStatus");
//                        long time = jsonObject.getLong("time");
                        long memory = jsonObject.getLong("memory");
                        long runTime = jsonObject.getLong("runTime");

                        // 访问嵌套的JSONObject（files）
                        JSONObject files = jsonObject.getJSONObject("files");
//                        String stderr = files.getStr("stderr");
                        String stdout = files.getStr("stdout");

                        JudgeInfo judgeInfoItem = new JudgeInfo();
                        outputList.add(stdout);
                        judgeInfoItem.setMessage(status);
                        judgeInfoItem.setTime(runTime);
                        judgeInfoItem.setMemory(memory);
                        judgeInfoList.add(judgeInfoItem);
                        System.out.println("判题数据"+judgeInfoItem);
                    }
                } catch (Exception e) {
                    // 处理解析错误，例如打印日志或返回错误信息
                    e.printStackTrace();
                }
            }
            else {
                executeCodeResponse.setStatus(511);
                executeCodeResponse.setMessage("运行时有测试点报错");
            }
        }
        executeCodeResponse.setJudgeInfo(judgeInfoList);
        executeCodeResponse.setOutputList(outputList);

        System.out.println(executeCodeResponse);
        return executeCodeResponse;
    }
}