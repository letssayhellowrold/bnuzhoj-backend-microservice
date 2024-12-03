package com.bnuzhoj.bnuzhojbackendpostservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.PostThumb;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.User;


/**
 * 帖子点赞服务
 *
 * 
 * 
 */
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    int doPostThumb(long postId, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostThumbInner(long userId, long postId);

    /**
     * 检查用户是否已经点赞了某个帖子
     *
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 如果用户已经点赞返回true，否则返回false
     */
    boolean hasThumbed(long userId, long postId);
}
