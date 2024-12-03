package com.bnuzhoj.bnuzhojbackendmodel.model.dto.postfavour;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子点赞检查请求
 */
@Data
public class PostFavourCheckRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 帖子 id
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}