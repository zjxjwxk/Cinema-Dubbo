package com.stylefeng.guns.api.order;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVO;

import java.util.List;

/**
 * @author zjxjwxk
 */
public interface OrderServiceApi {

    /**
     * 验证所购买的票是否为真
     * @param fieldId 放映场次编号
     * @param seats 座位号
     * @return 是否为真
     */
    boolean isTrueSeats(Integer fieldId, String seats);

    /**
     * 检查所购买的票是否已售出
     * @param fieldId 放映场次编号
     * @param seats 座位号
     * @return 是否
     */
    boolean isSoldSeats(Integer fieldId, String seats);

    /**
     * 创建订单信息
     * @param fieldId 放映场次信息
     * @param soldSeats 座位号
     * @param seatsName 座位名称
     * @param userId 用户编号
     * @return 订单VO
     */
    OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId);

    /**
     * 获取当前用户的订单
     * @param userId 用户编号
     * @return 订单VO列表
     */
    Page<OrderVO> getOrderVOListByUserId(Integer userId, Page<OrderVO> page);

    /**
     * 根据放映场次编号获取所有已经售出的座位编号
     * @param fieldId 放映场次编号
     * @return 已经售出的座位编号
     */
    String getSoldSeatsByFieldId(Integer fieldId);
}
