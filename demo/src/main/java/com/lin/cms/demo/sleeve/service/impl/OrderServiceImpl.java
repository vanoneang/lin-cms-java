package com.lin.cms.demo.sleeve.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin.cms.demo.sleeve.mapper.OrderMapper;
import com.lin.cms.demo.sleeve.model.Order;
import com.lin.cms.demo.sleeve.service.IOrderService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pedro
 * @since 2019-07-23
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}