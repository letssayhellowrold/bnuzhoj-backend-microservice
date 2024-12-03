package com.bnuzhoj.bnuzhojbackendpostservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.bnuzhoj.bnuzhojbackendcommon.common.BaseResponse;
import com.bnuzhoj.bnuzhojbackendcommon.common.ErrorCode;
import com.bnuzhoj.bnuzhojbackendcommon.common.ResultUtils;
import com.bnuzhoj.bnuzhojbackendcommon.exception.BusinessException;
import com.bnuzhoj.bnuzhojbackendcommon.exception.ThrowUtils;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.post.PostQueryRequest;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.postfavour.PostFavourAddRequest;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.postfavour.PostFavourCheckRequest;
import com.bnuzhoj.bnuzhojbackendmodel.model.dto.postfavour.PostFavourQueryRequest;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.Post;
import com.bnuzhoj.bnuzhojbackendmodel.model.entity.User;
import com.bnuzhoj.bnuzhojbackendmodel.model.vo.PostVO;
import com.bnuzhoj.bnuzhojbackendpostservice.service.PostFavourService;
import com.bnuzhoj.bnuzhojbackendpostservice.service.PostService;
import com.bnuzhoj.bnuzhojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏接口
 *
 * 
 * 
 */
@RestController
@RequestMapping("/post_favour")
@Slf4j
public class PostFavourController {

    @Resource
    private PostFavourService postFavourService;

    @Resource
    private PostService postService;

    @Resource
    private UserFeignClient userFeignClient;

    /**
     * 收藏 / 取消收藏
     *
     * @param postFavourAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doPostFavour(@RequestBody PostFavourAddRequest postFavourAddRequest,
                                              HttpServletRequest request) {
        if (postFavourAddRequest == null || postFavourAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userFeignClient.getLoginUser(request);
        long postId = postFavourAddRequest.getPostId();
        int result = postFavourService.doPostFavour(postId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我收藏的帖子列表
     *
     * @param postQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<PostVO>> listMyFavourPostByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                             HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest), loginUser.getId());
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    /**
     * 获取用户收藏的帖子列表
     *
     * @param postFavourQueryRequest
     * @param request
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<PostVO>> listFavourPostByPage(@RequestBody PostFavourQueryRequest postFavourQueryRequest,
            HttpServletRequest request) {
        if (postFavourQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = postFavourQueryRequest.getCurrent();
        long size = postFavourQueryRequest.getPageSize();
        Long userId = postFavourQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size),
                postService.getQueryWrapper(postFavourQueryRequest.getPostQueryRequest()), userId);
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    /**
     * 检查用户是否已经点赞了某个帖子
     *
     * @param postFavourCheckRequest 包含 userId 和 postId 的请求体
     * @return BaseResponse 包含是否点赞的布尔值
     */
    @PostMapping("/check_has_favoured")
    public BaseResponse<Boolean> checkHasFavoured(@RequestBody PostFavourCheckRequest postFavourCheckRequest) {
        if (postFavourCheckRequest == null || postFavourCheckRequest.getUserId() == null || postFavourCheckRequest.getPostId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean hasThumbed = postFavourService.hasFavoured(postFavourCheckRequest.getUserId(), postFavourCheckRequest.getPostId());
        return ResultUtils.success(hasThumbed);
    }
}
