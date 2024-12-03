package com.bnuzhoj.bnuzhojbackendmodel.model.dto.question;
import lombok.Data;

//@Data 是一个由Lombok库提供的注解（annotation），它用于自动为类生成标准的getter和setter方法、equals()、hashCode() 和 toString() 方法
@Data
public class JudgeConfig {
    /**
     * 时间限制（ms）
     */
    private long timeLimit;
    /**
     * 内存限制（KB）
     */
    private long memoryLimit;
    /**
     * 栈限制（KB）
     */
    private long stackLimit;

}
