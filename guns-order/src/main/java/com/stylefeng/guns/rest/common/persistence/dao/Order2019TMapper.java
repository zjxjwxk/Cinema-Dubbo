package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.persistence.model.Order2019T;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 2019订单信息表 Mapper 接口
 * </p>
 *
 * @author zjxjwxk
 * @since 2020-04-09
 */
public interface Order2019TMapper extends BaseMapper<Order2019T> {

    /**
     * 根据放映场次编号获取该场次的座位位置图的文件地址
     * @param fieldId 放映场次编号
     * @return 座位位置图的文件地址
     */
    String getSeatsByFieldId(@Param("fieldId") Integer fieldId);

    /**
     * 根据订单编号获取订单VO
     * @param orderId 订单编号
     * @return 订单VO
     */
    OrderVO getOrderVOById(@Param("orderId") String orderId);

    /**
     * 根据用户编号、分页信息，获取订单VO列表
     * @param userId 用户编号
     * @param page 分页信息
     * @return 订单VO列表
     */
    List<OrderVO> getOrderVOListByUserId(@Param("userId") Integer userId, Page<OrderVO> page);

    /**
     * 根据放映场次编号，获取已售座位号
     * @param fieldId 放映场次编号
     * @return 已售座位号
     */
    String getSoldSeatsByFieldId(@Param("fieldId") Integer fieldId);

}
