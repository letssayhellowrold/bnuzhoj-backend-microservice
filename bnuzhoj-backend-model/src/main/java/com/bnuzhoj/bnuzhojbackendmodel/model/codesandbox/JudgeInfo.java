package com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox;
import lombok.Data;

//@Data 是一个由Lombok库提供的注解（annotation），它用于自动为类生成标准的getter和setter方法、equals()、hashCode() 和 toString() 方法
@Data
public class JudgeInfo {
    /**
     * 判题信息
     */
    private String message;
    /**
     * 内存消耗（KB）
     */
    private Long memory;
    /**
     * 时间消耗（ms）
     */
    private Long time;

    @Override
    public String toString() {
        return "{" +
                "\"message\":\"" + message + "\"," +
                "\"memory\":" + memory + "," +
                "\"time\":" + time +
                '}';
    }
}
