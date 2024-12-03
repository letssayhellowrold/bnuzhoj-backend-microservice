package com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    /**
     * 接口信息
     */
    private String message;

    /**
     * 执行状态
     */
    private Integer status;
    /**
     * 每一个测试点的输出
     * 沙箱不负责检查是否正确
     */
    private List<String> outputList;

    /**
     * 每一个测试点判题过程中得到的信息
     * 运行情况，时间消耗，内存消耗
     */
    private List<JudgeInfo> judgeInfo;
}
