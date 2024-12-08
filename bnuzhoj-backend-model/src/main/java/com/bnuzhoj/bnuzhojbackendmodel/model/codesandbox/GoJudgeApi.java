package com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.core.util.StrUtil;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.question.JudgeConfig;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.QuestionSubmitLanguageEnum;

import java.util.Objects;

public class GoJudgeApi {

    private final long maxFileMemoryAllow = 1048576000L;//单位字节，所有文件操作都给予 100 MB 的操作空间

    private final long maxTimeAllow = 10_000_000_000L;// 单位纳秒，最多给予 10s

    private final long maxThreadCount = 50;// 最大线程数量
    /**
     * 构建编译命令的JSON结构体
     * @param sourceCode 源代码字符串
     * @return 构建好的JSON字符串
     */
    public JSON compileCmd(String language, String sourceCode) {
        // 创建一个JSONObject来表示整个JSON对象
        JSONObject root = new JSONObject();

        // 创建cmd数组
        JSONArray cmdArray = new JSONArray();
        JSONObject cmd = new JSONObject();
        if (Objects.equals(QuestionSubmitLanguageEnum.getEnumByValue(language), QuestionSubmitLanguageEnum.CPLUSPLUS))
        {
            // 创建args数组
            JSONArray argsArray = new JSONArray();
            argsArray.add("/usr/bin/g++");
            argsArray.add("a.cc");
            argsArray.add("-o");
            argsArray.add("a");
            cmd.put("args", argsArray);

            // 创建env数组
            JSONArray envArray = new JSONArray();
            envArray.add("PATH=/usr/bin:/bin");
            cmd.put("env", envArray);

            // 创建files数组
            JSONArray filesArray = new JSONArray();
            JSONObject fileContent = new JSONObject();
            fileContent.put("content", "");
            filesArray.add(fileContent);

            JSONObject stdout = new JSONObject();
            stdout.put("name", "stdout");
            stdout.put("max", maxFileMemoryAllow);
            filesArray.add(stdout);

            JSONObject stderr = new JSONObject();
            stderr.put("name", "stderr");
            stderr.put("max", maxFileMemoryAllow);
            filesArray.add(stderr);
            cmd.put("files", filesArray);

            cmd.put("cpuLimit", maxTimeAllow);
            cmd.put("memoryLimit", maxFileMemoryAllow);// 10MB
            cmd.put("procLimit", maxThreadCount);

            // 创建copyIn对象
            JSONObject copyIn = new JSONObject();
            JSONObject aCc = new JSONObject();
            // 转译 \r\n 为 \n
            String unixNewlineCode = sourceCode.replace("\r\n", "\n");
//            // 转译 \ 和 "
//            String escapedCode = unixNewlineCode.replace("\\", "\\\\").replace("\"", "\\\"");
            aCc.put("content", unixNewlineCode); // 使用传入的转义后的字符串
            copyIn.put("a.cc", aCc);
            cmd.put("copyIn", copyIn);

            // 创建copyOut和copyOutCached数组
            JSONArray copyOut = new JSONArray();
            copyOut.add("stdout");
            copyOut.add("stderr");
            cmd.put("copyOut", copyOut);

            JSONArray copyOutCached = new JSONArray();
            copyOutCached.add("a");
            cmd.put("copyOutCached", copyOutCached);

            // 将cmd对象添加到cmd数组中
            cmdArray.add(cmd);
            root.put("cmd", cmdArray);
        }
        // 返回构建的JSON字符串
        return root;
    }
    /**
     * 构建运行命令的JSON结构体
     * @param fileId 缓存文件的ID
     * @return 构建好的JSON字符串
     */
    public String runCmd(String fileId,String input, JudgeConfig judgeConfig) {
        // 创建一个JSONObject来表示整个JSON对象
        JSONObject root = new JSONObject();

        // 创建cmd数组
        JSONArray cmdArray = new JSONArray();
        JSONObject cmd = new JSONObject();

        // 创建args数组
        JSONArray argsArray = new JSONArray();
        argsArray.add("a");
        cmd.put("args", argsArray);

        // 创建env数组
        JSONArray envArray = new JSONArray();
        envArray.add("PATH=/usr/bin:/bin");
        cmd.put("env", envArray);

        // 创建files数组
        JSONArray filesArray = new JSONArray();
        JSONObject fileContent = new JSONObject();
        fileContent.put("content", input); // 这里可以根据需要传入输入数据
        filesArray.add(fileContent);

        JSONObject stdout = new JSONObject();
        stdout.put("name", "stdout");
        stdout.put("max", maxFileMemoryAllow);
        filesArray.add(stdout);

        JSONObject stderr = new JSONObject();
        stderr.put("name", "stderr");
        stderr.put("max", maxFileMemoryAllow);
        filesArray.add(stderr);
        cmd.put("files", filesArray);

        cmd.put("cpuLimit", judgeConfig.getTimeLimit()*1_000_000L);// 时间限制
        cmd.put("memoryLimit", judgeConfig.getMemoryLimit()*1024);// 内存限制
        cmd.put("stackLimit",judgeConfig.getStackLimit()*1024);// 堆限制
        cmd.put("procLimit", maxThreadCount);// 线程限制

        // 创建copyIn对象
        JSONObject copyIn = new JSONObject();
        JSONObject aFile = new JSONObject();
        aFile.put("fileId", fileId); // 使用传入的fileId
        copyIn.put("a", aFile);
        cmd.put("copyIn", copyIn);

        // 将cmd对象添加到cmd数组中
        cmdArray.add(cmd);
        root.put("cmd", cmdArray);

        // 返回构建的JSON字符串
        return root.toStringPretty();
    }
}

