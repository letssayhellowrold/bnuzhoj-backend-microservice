package com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.bnuzhoj.bnuzhojbackendmodel.model.enums.QuestionSubmitLanguageEnum;

import java.util.Objects;

public class GoJudgeApi {

    /**
     * 构建编译命令的JSON结构体
     * @param sourceCode 源代码字符串
     * @return 构建好的JSON字符串
     */
    public String compileCmd(String language,String sourceCode) {
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
            stdout.put("max", 10240);
            filesArray.add(stdout);

            JSONObject stderr = new JSONObject();
            stderr.put("name", "stderr");
            stderr.put("max", 10240);
            filesArray.add(stderr);
            cmd.put("files", filesArray);

            cmd.put("cpuLimit", 1000000000000L);
            cmd.put("memoryLimit", 10485760000L);
            cmd.put("procLimit", 50);

            // 创建copyIn对象
            JSONObject copyIn = new JSONObject();
            JSONObject aCc = new JSONObject();
            aCc.put("content", sourceCode); // 使用传入的源代码字符串
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
        else if (Objects.equals(QuestionSubmitLanguageEnum.getEnumByValue(language), QuestionSubmitLanguageEnum.PYTHON)) {
            // 创建args数组
            JSONArray argsArray = new JSONArray();
            argsArray.add("/usr/bin/python3"); // 假设使用python3
            argsArray.add("a.py");
            cmd.put("args", argsArray);

            // 创建env数组
            JSONArray envArray = new JSONArray();
            envArray.add("PATH=/usr/bin:/bin");
            cmd.put("env", envArray);

            // 创建files数组
            JSONArray filesArray = new JSONArray();
            JSONObject fileContent = new JSONObject();
            fileContent.put("content", sourceCode);
            filesArray.add(fileContent);

            JSONObject stdout = new JSONObject();
            stdout.put("name", "stdout");
            stdout.put("max", 10240);
            filesArray.add(stdout);

            JSONObject stderr = new JSONObject();
            stderr.put("name", "stderr");
            stderr.put("max", 10240);
            filesArray.add(stderr);
            cmd.put("files", filesArray);

            cmd.put("cpuLimit", 1000000000000L);
            cmd.put("memoryLimit", 10485760000L);
            cmd.put("procLimit", 50);

            // 创建copyIn对象
            JSONObject copyIn = new JSONObject();
            JSONObject aPy = new JSONObject();
            aPy.put("content", sourceCode);
            copyIn.put("a.py", aPy);
            cmd.put("copyIn", copyIn);

            // 创建copyOut和copyOutCached数组
            JSONArray copyOut = new JSONArray();
            copyOut.add("stdout");
            copyOut.add("stderr");
            cmd.put("copyOut", copyOut);

            JSONArray copyOutCached = new JSONArray();
            copyOutCached.add("a.py");
            cmd.put("copyOutCached", copyOutCached);

            cmdArray.add(cmd);
            root.put("cmd", cmdArray);
        }
        // 返回构建的JSON字符串
        return root.toStringPretty();
    }
    /**
     * 构建运行命令的JSON结构体
     * @param fileId 缓存文件的ID
     * @return 构建好的JSON字符串
     */
    public String runCmd(String fileId,String input) {
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
        stdout.put("max", 10240);
        filesArray.add(stdout);

        JSONObject stderr = new JSONObject();
        stderr.put("name", "stderr");
        stderr.put("max", 10240);
        filesArray.add(stderr);
        cmd.put("files", filesArray);

        cmd.put("cpuLimit", 10000000000L);
        cmd.put("memoryLimit", 104857600L);
        /*
         * CPU限制（cpuLimit）：
         *
         * 此字段设置了程序可以使用的CPU时间限制，单位通常是纳秒。在这个例子中，10000000000L纳秒等于10秒。
         * 内存限制（memoryLimit）：
         *
         * 此字段设置了程序可以使用的最大内存量，单位是字节。在这个例子中，104857600L字节等于100MB。
         */
        cmd.put("procLimit", 50);

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
