package com.bnuzhoj.bnuzhojbackendmodel.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 *
 * 
 * 
 */
public enum goJudgeStatusEnum {
    ACCEPTED("正常结束","Accepted"),
    Time_Limit_Exceeded("时间超限","Time Limit Exceeded"),
    Memory_Limit_Exceeded("内存超限","Memory Limit Exceeded"),
    Output_Limit_Exceeded("输出超限","Output Limit Exceeded"),
    File_Error("文件错误","File Error"),
    Nonzero_Exit_Status("非零返回","Nonzero Exit Status"),
    Signalled("进程被信号终止","'Signalled'"),
    InternalError("内部错误","Internal Error");

    private final String text;

    private final String value;

    goJudgeStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static goJudgeStatusEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (goJudgeStatusEnum anEnum : goJudgeStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}