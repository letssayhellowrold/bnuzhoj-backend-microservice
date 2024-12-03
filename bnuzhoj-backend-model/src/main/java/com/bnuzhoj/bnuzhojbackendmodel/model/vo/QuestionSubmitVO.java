package com.bnuzhoj.bnuzhojbackendmodel.model.vo;

import cn.hutool.json.JSONUtil;
import com.bnuzhoj.bnuzhojbackendmodel.model.codesandbox.JudgeInfo;
import  com.bnuzhoj.bnuzhojbackendmodel.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目提交封装类
 */
@Data
public class QuestionSubmitVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息（作为对象返回，便于前端处理）
     */
    private List<JudgeInfo> judgeInfo;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 提交用户信息
     */
    private UserVO userVO;
    /**
     * 题目信息
     */
    private QuestionVO questionVO;
    /**
     * 包装类转对象
     *
     * @param questionSubmitVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);// 拷贝同名属性
        // 列表类型转换
        List<JudgeInfo> judgeInfoList = questionSubmitVO.getJudgeInfo();
        List<String> judgeInfoStrList = judgeInfoList.stream()
                .map(JSONUtil::toJsonStr)
                .collect(Collectors.toList());
        // 将列表转换为由 \$ 分隔的字符串
        String judgeInfoString = String.join("\\$", judgeInfoStrList);
        questionSubmit.setJudgeInfo(judgeInfoString);
        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        try {
            BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 确保通过 questionSubmit 对象来访问 judgeInfo 字段
        String judgeInfo = questionSubmit.getJudgeInfo(); // 获取 questionSubmit 对象的 judgeInfo 字段

        List<String> judgeInfoStrList = Arrays.asList(judgeInfo.split("\\$"));
        List<JudgeInfo> judgeInfoList = judgeInfoStrList.stream()
                .map(judgeInfoStr -> JSONUtil.toBean(judgeInfoStr, JudgeInfo.class))
                .collect(Collectors.toList());
//        System.out.println(judgeInfoList);
        questionSubmitVO.setJudgeInfo(judgeInfoList);
        return questionSubmitVO;
    }

    private static final long serialVersionUID = 1L;
}