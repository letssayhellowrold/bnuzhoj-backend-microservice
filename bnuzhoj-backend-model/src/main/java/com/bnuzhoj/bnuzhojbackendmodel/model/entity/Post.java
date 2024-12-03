package com.bnuzhoj.bnuzhojbackendmodel.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子
 *
 * 
 * 
 */
@TableName(value = "post")
@Data
public class Post implements Serializable {

    /**
     * 帖子id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属的题号
     */
    private Long questionId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

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
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    //    serialVersionUID 是一个用于在序列化和反序列化过程中保持版本一致性的字段。它是 java.io.Serializable 接口的一个常量，用于唯一标识类的版本。
}