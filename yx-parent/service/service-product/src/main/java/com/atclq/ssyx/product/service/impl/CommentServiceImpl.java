package com.atclq.ssyx.product.service.impl;


import com.atclq.ssyx.model.product.Comment;
import com.atclq.ssyx.product.mapper.CommentMapper;
import com.atclq.ssyx.product.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品评价 服务实现类
 * </p>
 *
 * @author atclq
 * @since 2024-04-25
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
